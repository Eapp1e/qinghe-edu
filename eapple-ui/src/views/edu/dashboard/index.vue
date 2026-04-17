<template>
  <div class="app-container dashboard-page">
    <section class="hero-card">
      <div>
        <span class="hero-badge">Platform Overview</span>
        <h1>平台首页</h1>
        <p>集中查看课后服务平台的课程运行、报名情况、作业问答与整体活跃趋势。</p>
      </div>
    </section>

    <section class="metric-grid">
      <div v-for="card in cards" :key="card.label" class="metric-card">
        <span>{{ card.label }}</span>
        <strong>{{ card.value }}</strong>
      </div>
    </section>

    <section ref="chartSection" class="chart-grid">
      <el-card shadow="never" class="chart-card">
        <div slot="header">业务总览对比</div>
        <div ref="overviewChart" class="chart-box"></div>
      </el-card>
      <el-card shadow="never" class="chart-card">
        <div slot="header">平台业务结构</div>
        <div ref="structureChart" class="chart-box"></div>
      </el-card>
      <el-card shadow="never" class="chart-card">
        <div slot="header">热门选课排行</div>
        <div ref="popularCourseChart" class="chart-box"></div>
      </el-card>
      <el-card shadow="never" class="chart-card wide-card">
        <div slot="header">近期活跃趋势</div>
        <div ref="activityChart" class="chart-box"></div>
      </el-card>
    </section>

    <section class="content-grid">
      <el-card shadow="never" class="insight-card">
        <div slot="header">平台动态</div>
        <div class="insight-layout">
          <div class="info-panel timeline-panel">
            <div class="panel-head">
              <strong>近期动态</strong>
              <span>聚合课程发布与学生提问等平台动态</span>
            </div>
            <div class="timeline-scroll">
              <el-timeline>
                <el-timeline-item v-for="item in timeline" :key="item.key" :timestamp="item.time">
                  <div class="timeline-card">
                    <strong>{{ item.text }}</strong>
                    <span>{{ item.time }}</span>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>

          <div class="right-column">
            <div class="info-panel schedule-panel">
              <div class="panel-head">
                <strong>近期课程安排</strong>
                <span>快速查看最新课程与开课时间</span>
              </div>
              <div v-if="recentCourseCards.length" class="mini-list">
                <div v-for="item in recentCourseCards" :key="item.key" class="mini-item">
                  <div class="mini-main">
                    <strong>{{ item.title }}</strong>
                    <p>{{ item.desc }}</p>
                  </div>
                  <span>{{ item.meta }}</span>
                </div>
              </div>
              <div v-else class="empty-state">暂无最新课程安排</div>
            </div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { parseTime } from '@/utils/eapple'
import { getEduDashboard } from '@/api/edu/dashboard'

export default {
  name: 'EduDashboard',
  data() {
    return {
      dashboard: {
        totalCourses: 0,
        totalEnrollments: 0,
        totalQuestions: 0,
        totalAiCalls: 0,
        activeStudents: 0,
        activeTeachers: 0,
        recentCourses: [],
        recentQuestions: [],
        recentAiLogs: [],
        popularCourses: []
      },
      charts: []
    }
  },
  computed: {
    cards() {
      return [
        { label: '课程总数', value: this.dashboard.totalCourses || 0 },
        { label: '报名总数', value: this.dashboard.totalEnrollments || 0 },
        { label: '提问总数', value: this.dashboard.totalQuestions || 0 },
        { label: 'AI 调用', value: this.dashboard.totalAiCalls || 0 },
        { label: '活跃学生', value: this.dashboard.activeStudents || 0 },
        { label: '活跃教师', value: this.dashboard.activeTeachers || 0 }
      ]
    },
    timeline() {
      const items = []
      ;(this.dashboard.recentCourses || []).slice(0, 3).forEach(item => {
        items.push({
          key: `course-${item.courseId}`,
          time: this.formatCourseTime(item),
          text: `课程《${item.courseName}》已发布`
        })
      })
      ;(this.dashboard.recentQuestions || []).slice(0, 4).forEach(item => {
        items.push({
          key: `question-${item.questionId}`,
          time: this.formatDisplayTime(item.createTime),
          text: `收到问题《${item.questionTitle}》`
        })
      })
      return items.length ? items : [{ key: 'empty', time: '当前', text: '暂无最新平台动态' }]
    },
    recentCourseCards() {
      return (this.dashboard.recentCourses || []).slice(0, 3).map(item => ({
        key: item.courseId,
        title: item.courseName,
        desc: `授课教师：${item.teacherName || '待安排'} · 容量 ${item.maxCapacity || item.capacity || 0} 人`,
        meta: this.formatCourseTime(item)
      }))
    }
  },
  created() {
    this.getData()
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeCharts()
  },
  methods: {
    parseDate(value) {
      if (!value) return null
      if (value instanceof Date) return value
      const normalized = typeof value === 'string' ? value.replace(/-/g, '/') : value
      const date = new Date(normalized)
      return Number.isNaN(date.getTime()) ? null : date
    },
    isCurrentWeek(value) {
      const date = this.parseDate(value)
      if (!date) return false
      const now = new Date()
      const currentDay = now.getDay() || 7
      const startOfWeek = new Date(now)
      startOfWeek.setHours(0, 0, 0, 0)
      startOfWeek.setDate(now.getDate() - currentDay + 1)
      const endOfWeek = new Date(startOfWeek)
      endOfWeek.setDate(startOfWeek.getDate() + 7)
      return date >= startOfWeek && date < endOfWeek
    },
    getWeekLabel(value) {
      const date = this.parseDate(value)
      if (!date) return ''
      const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return weekMap[date.getDay()]
    },
    formatDisplayTime(value, timeText = '') {
      const date = this.parseDate(value)
      if (!date) {
        return timeText || '刚刚'
      }
      if (this.isCurrentWeek(date)) {
        return `${this.getWeekLabel(date)}${timeText ? ' ' + timeText : ''}`.trim()
      }
      return `${parseTime(date, '{y}-{m}-{d}')}${timeText ? ' ' + timeText : ''}`.trim()
    },
    formatCourseTime(item) {
      return this.formatDisplayTime(item.startDate || item.createTime, item.startTime || '')
    },
    formatCourseName(name) {
      if (!name) return '未命名课程'
      return name.length > 12 ? `${name.slice(0, 12)}...` : name
    },
    getData() {
      getEduDashboard().then(res => {
        this.dashboard = Object.assign({}, this.dashboard, res.data || {})
        this.$nextTick(() => {
          this.renderCharts()
        })
      })
    },
    renderCharts() {
      this.disposeCharts()
      this.renderOverviewChart()
      this.renderStructureChart()
      this.renderPopularCourseChart()
      this.renderActivityChart()
    },
    renderOverviewChart() {
      const chart = echarts.init(this.$refs.overviewChart, 'macarons')
      chart.setOption({
        grid: { top: 36, left: 40, right: 20, bottom: 30 },
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          data: ['课程', '报名', '问答', '学生', '教师']
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          barWidth: 24,
          itemStyle: {
            borderRadius: [10, 10, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#20e0b6' },
              { offset: 1, color: '#3095ff' }
            ])
          },
          data: [
            this.dashboard.totalCourses || 0,
            this.dashboard.totalEnrollments || 0,
            this.dashboard.totalQuestions || 0,
            this.dashboard.activeStudents || 0,
            this.dashboard.activeTeachers || 0
          ]
        }]
      })
      this.charts.push(chart)
    },
    renderStructureChart() {
      const chart = echarts.init(this.$refs.structureChart, 'macarons')
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['45%', '70%'],
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 4 },
          data: [
            { value: this.dashboard.totalCourses || 0, name: '课程' },
            { value: this.dashboard.totalEnrollments || 0, name: '报名' },
            { value: this.dashboard.totalQuestions || 0, name: '问答' }
          ]
        }]
      })
      this.charts.push(chart)
    },
    renderPopularCourseChart() {
      const chart = echarts.init(this.$refs.popularCourseChart, 'macarons')
      const popularCourses = (this.dashboard.popularCourses || [])
        .slice()
        .sort((a, b) => (b.enrollCount || 0) - (a.enrollCount || 0))
        .slice(0, 5)
      const rankLabels = popularCourses.length
        ? popularCourses.map((item, index) => `TOP ${index + 1}`)
        : ['TOP 1']
      const courseNames = popularCourses.length
        ? popularCourses.map(item => this.formatCourseName(item.courseName))
        : ['暂无课程数据']
      const values = popularCourses.length ? popularCourses.map(item => item.enrollCount || 0) : [0]
      const maxValue = Math.max(...values, 1)

      chart.setOption({
        grid: { top: 28, left: 18, right: 138, bottom: 28, containLabel: true },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'none' },
          formatter: params => {
            const target = (params || []).find(item => item.seriesName === '报名人数')
            if (!target || !popularCourses[target.dataIndex]) {
              return '暂无课程数据'
            }
            const course = popularCourses[target.dataIndex]
            return `${rankLabels[target.dataIndex]}<br/>${course.courseName}<br/>报名人数：${course.enrollCount || 0}`
          }
        },
        xAxis: {
          type: 'value',
          max: Math.max(Math.ceil(maxValue * 1.08), 3),
          splitLine: { show: false },
          splitArea: { show: false },
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { show: false }
        },
        yAxis: [
          {
            type: 'category',
            inverse: true,
            axisTick: { show: false },
            axisLine: { show: false },
            axisLabel: {
              color: '#5f7285',
              fontSize: 12,
              fontWeight: 700,
              margin: 20
            },
            data: rankLabels
          },
          {
            type: 'category',
            inverse: true,
            axisTick: { show: false },
            axisLine: { show: false },
            axisLabel: {
              color: '#22384a',
              fontSize: 13,
              margin: 54,
              width: 126,
              overflow: 'truncate'
            },
            data: courseNames
          }
        ],
        series: [
          {
            type: 'bar',
            yAxisIndex: 1,
            silent: true,
            barWidth: 18,
            barGap: '-100%',
            z: 1,
            itemStyle: {
              color: 'rgba(48, 149, 255, 0.12)',
              borderRadius: 999
            },
            data: values.map(() => maxValue)
          },
          {
            name: '报名人数',
            type: 'bar',
            yAxisIndex: 1,
            barWidth: 18,
            label: {
              show: true,
              position: 'right',
              distance: 12,
              color: '#3b5568',
              fontWeight: 700,
              formatter: ({ value }) => (value > 0 ? `${value} 人` : '')
            },
            z: 3,
            itemStyle: {
              borderRadius: 999,
              color: params => {
                const palette = [
                  ['#23dfb7', '#2f95ff'],
                  ['#48cfff', '#4c7cff'],
                  ['#5ce5af', '#29c7a2'],
                  ['#7ea7ff', '#5968ff'],
                  ['#78d5ff', '#3db8ff']
                ]
                const colors = palette[params.dataIndex] || palette[palette.length - 1]
                return new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                  { offset: 0, color: colors[0] },
                  { offset: 1, color: colors[1] }
                ])
              }
            },
            data: values
          }
        ]
      })
      this.charts.push(chart)
    },
    renderActivityChart() {
      const today = new Date()
      today.setHours(23, 59, 59, 999)
      const windowStart = new Date(today)
      windowStart.setHours(0, 0, 0, 0)
      windowStart.setDate(windowStart.getDate() - 6)
      const recentCourseMap = {}
      ;(this.dashboard.recentCourses || []).forEach(item => {
        const date = this.parseDate(item.startDate || item.createTime)
        if (!date) return
        if (date > today || date < windowStart) return
        const key = parseTime(date, '{y}-{m}-{d}')
        recentCourseMap[key] = (recentCourseMap[key] || 0) + 1
      })
      const questionMap = {}
      ;(this.dashboard.recentQuestions || []).forEach(item => {
        const date = this.parseDate(item.createTime)
        if (!date) return
        if (date > today || date < windowStart) return
        const key = parseTime(date, '{y}-{m}-{d}')
        questionMap[key] = (questionMap[key] || 0) + 1
      })
      const categoryKeys = Array.from({ length: 7 }, (_, index) => {
        const date = new Date(windowStart)
        date.setDate(windowStart.getDate() + index)
        return parseTime(date, '{y}-{m}-{d}')
      })
      const categoryLabels = categoryKeys.map(label => this.formatDisplayTime(label))
      const chart = echarts.init(this.$refs.activityChart, 'macarons')
      chart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { top: 8 },
        grid: { top: 42, left: 34, right: 24, bottom: 30 },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: categoryLabels,
          axisLabel: {
            interval: 0,
            hideOverlap: true,
            color: '#5f7285'
          }
        },
        yAxis: { type: 'value' },
        series: [
          {
            name: '课程活跃',
            type: 'line',
            smooth: true,
            areaStyle: { color: 'rgba(32, 221, 182, 0.12)' },
            data: categoryKeys.map(label => recentCourseMap[label] || 0)
          },
          {
            name: '提问活跃',
            type: 'line',
            smooth: true,
            areaStyle: { color: 'rgba(47, 152, 255, 0.12)' },
            data: categoryKeys.map(label => questionMap[label] || 0)
          }
        ]
      })
      this.charts.push(chart)
    },
    disposeCharts() {
      this.charts.forEach(chart => chart && chart.dispose())
      this.charts = []
    },
    handleResize() {
      this.charts.forEach(chart => chart && chart.resize())
    }
  }
}
</script>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 20px;
}

.hero-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 32px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(21, 34, 46, 0.96), rgba(29, 73, 82, 0.9));
  color: #fff;
}

.hero-badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(32, 224, 182, 0.14);
  color: #8ff6dc;
  font-size: 12px;
  letter-spacing: 0.02em;
  text-transform: none;
}

.hero-card h1 {
  margin: 14px 0 10px;
  font-size: 34px;
}

.hero-card p {
  max-width: 720px;
  margin: 0;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.7;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
}

.metric-card,
.chart-card,
.insight-card {
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 16px 40px rgba(31, 107, 127, 0.08);
}

.metric-card {
  padding: 20px 22px;
}

.metric-card span {
  display: block;
  color: #6d8591;
  font-size: 13px;
}

.metric-card strong {
  display: block;
  margin-top: 14px;
  color: #203544;
  font-size: 30px;
  font-weight: 700;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.wide-card {
  grid-column: span 3;
}

.chart-card ::v-deep .el-card__header,
.insight-card ::v-deep .el-card__header {
  padding: 18px 22px 0;
  border-bottom: none;
  color: #203544;
  font-size: 16px;
  font-weight: 700;
}

.chart-card ::v-deep .el-card__body,
.insight-card ::v-deep .el-card__body {
  padding: 18px 22px 22px;
}

.chart-box {
  height: 326px;
}

.wide-card .chart-box {
  height: 300px;
}

.content-grid {
  display: grid;
}

.insight-layout {
  display: grid;
  grid-template-columns: 1.3fr 1fr;
  gap: 18px;
}

.info-panel {
  border: 1px solid rgba(61, 136, 160, 0.34);
  border-radius: 24px;
  background:
    radial-gradient(circle at bottom right, rgba(32, 224, 182, 0.08), transparent 28%),
    linear-gradient(180deg, rgba(237, 251, 252, 0.96), rgba(246, 250, 253, 0.98));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.76),
    inset 0 0 0 1px rgba(255, 255, 255, 0.35),
    0 18px 36px rgba(31, 107, 127, 0.08);
}

.timeline-panel {
  padding: 24px 24px 18px;
  min-height: 520px;
  display: flex;
  flex-direction: column;
}

.timeline-scroll {
  flex: 1;
  padding-right: 8px;
  overflow-y: auto;
}

.timeline-scroll::-webkit-scrollbar {
  width: 6px;
}

.timeline-scroll::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(76, 149, 255, 0.25);
}

.panel-head {
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(61, 136, 160, 0.24);
}

.panel-head strong {
  display: block;
  color: #243b4a;
  font-size: 17px;
  font-weight: 700;
  line-height: 1.4;
}

.panel-head span {
  display: block;
  margin-top: 8px;
  color: #7a919f;
  font-size: 13px;
  line-height: 1.7;
}

.right-column {
  display: grid;
  gap: 18px;
}

.schedule-panel {
  padding: 24px;
  min-height: 520px;
}

.mini-list {
  display: grid;
  gap: 14px;
}

.mini-item {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
  padding: 18px 20px;
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 252, 255, 0.96));
  border: 1px solid rgba(61, 136, 160, 0.26);
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.4),
    0 10px 24px rgba(34, 93, 108, 0.06);
}

.mini-main strong {
  display: block;
  color: #203544;
  font-size: 16px;
  line-height: 1.45;
}

.mini-main p {
  margin: 8px 0 0;
  color: #6f8694;
  font-size: 14px;
  line-height: 1.7;
}

.mini-item span {
  flex-shrink: 0;
  color: #3794e0;
  font-size: 14px;
  font-weight: 700;
  line-height: 1.5;
}

.empty-state {
  padding: 32px 0 12px;
  color: #7a919f;
  text-align: center;
}

.timeline-panel ::v-deep .el-timeline {
  padding-left: 12px;
}

.timeline-panel ::v-deep .el-timeline-item {
  padding-bottom: 14px;
}

.timeline-panel ::v-deep .el-timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-panel ::v-deep .el-timeline-item__node {
  width: 14px;
  height: 14px;
  background: linear-gradient(180deg, #20d9b7, #57a6ff);
  border: 2px solid #f4fbfb;
  box-shadow: 0 0 0 7px rgba(32, 224, 182, 0.16);
}

.timeline-panel ::v-deep .el-timeline-item__timestamp {
  display: none;
}

.timeline-panel ::v-deep .el-timeline-item__content {
  color: #243b4a;
  line-height: 1.7;
}

.timeline-card {
  padding: 14px 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 251, 254, 0.96));
  border: 1px solid rgba(61, 136, 160, 0.26);
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.42),
    0 10px 22px rgba(34, 93, 108, 0.05);
}

.timeline-card strong {
  display: block;
  color: #243b4a;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.7;
}

.timeline-card span {
  display: block;
  margin-top: 8px;
  color: #6f8694;
  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 1400px) {
  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .chart-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .wide-card {
    grid-column: span 2;
  }

  .insight-layout {
    grid-template-columns: 1fr;
  }

  .schedule-panel {
    min-height: auto;
  }
}

@media (max-width: 900px) {
  .metric-grid,
  .chart-grid {
    grid-template-columns: 1fr;
  }

  .wide-card {
    grid-column: span 1;
  }

  .hero-card {
    padding: 24px;
  }

  .timeline-panel,
  .schedule-panel {
    padding: 20px;
  }

  .mini-item {
    flex-direction: column;
  }

  .mini-item span {
    margin-top: 4px;
  }
}
</style>
