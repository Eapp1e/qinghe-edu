<template>
  <div class="app-container dashboard-page">
    <section class="module-hero dashboard-hero">
      <div>
        <span class="hero-badge">Platform Overview</span>
        <h1>平台首页</h1>
        <p>{{ dashboardScopeDescription }}</p>
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
        <div slot="header">家校服务结构</div>
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
              <span>聚合公告通知、课程、问答与亲子任务等平台动态</span>
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
        totalParentAdvices: 0,
        totalFamilyTasks: 0,
        completedFamilyTasks: 0,
        activeStudents: 0,
        activeTeachers: 0,
        recentCourses: [],
        recentQuestions: [],
        recentAiLogs: [],
        recentFamilyTasks: [],
        recentNotices: [],
        popularCourses: []
      },
      charts: []
    }
  },
  computed: {
    cards() {
      return [
        { label: '课程总数', value: this.dashboard.totalCourses || 0 },
        { label: '提问总数', value: this.dashboard.totalQuestions || 0 },
        { label: 'AI 调用', value: this.dashboard.totalAiCalls || 0 },
        { label: '家长建议', value: this.dashboard.totalParentAdvices || 0 },
        { label: '亲子任务', value: this.dashboard.totalFamilyTasks || 0 },
        { label: '完成任务', value: this.dashboard.completedFamilyTasks || 0 },
        { label: '活跃学生', value: this.dashboard.activeStudents || 0 },
        { label: '活跃教师', value: this.dashboard.activeTeachers || 0 }
      ]
    },
    roleKeys() {
      return this.$store.getters.roles || []
    },
    isAdminView() {
      return this.roleKeys.includes('admin') || this.roleKeys.includes('edu_admin')
    },
    dashboardScopeLabel() {
      if (this.isAdminView) return '全平台数据'
      if (this.roleKeys.includes('edu_teacher')) return '我的课程数据'
      if (this.roleKeys.includes('edu_parent')) return '我的孩子数据'
      if (this.roleKeys.includes('edu_student')) return '我的学习数据'
      return '个人可见数据'
    },
    dashboardScopeDescription() {
      if (this.isAdminView) {
        return '集中查看全平台课程运行、报名情况、作业问答、家长陪学、亲子任务与整体活跃趋势。'
      }
      if (this.roleKeys.includes('edu_teacher')) {
        return '集中查看本人课程相关的报名、问答、AI 调用与近期课程运行情况。'
      }
      if (this.roleKeys.includes('edu_parent')) {
        return '集中查看已绑定孩子相关的课程报名、作业问答、家长陪学建议与亲子任务情况。'
      }
      if (this.roleKeys.includes('edu_student')) {
        return '集中查看本人课程、问答、学习动态与亲子任务完成情况。'
      }
      return '根据当前账号权限展示可查看的课后服务数据。'
    },
    timeline() {
      const items = []
      const now = Date.now()
      const pushRecentItem = item => {
        if (!item.rawTime || item.rawTime > now) return
        items.push(item)
      }
      ;(this.dashboard.recentCourses || []).slice(0, 5).forEach(item => {
        pushRecentItem({
          key: `course-${item.courseId}`,
          time: this.formatDisplayTime(item.createTime),
          text: `课程《${item.courseName}》已发布`,
          rawTime: this.getTimeValue(item.createTime)
        })
      })
      ;(this.dashboard.recentNotices || []).slice(0, 5).forEach(item => {
        const noticeType = item.noticeType === '2' ? '公告' : '通知'
        pushRecentItem({
          key: `notice-${item.noticeId}`,
          time: this.formatDisplayTime(item.createTime),
          text: `${noticeType}《${item.noticeTitle}》已发布`,
          rawTime: this.getTimeValue(item.createTime)
        })
      })
      ;(this.dashboard.recentQuestions || []).slice(0, 5).forEach(item => {
        pushRecentItem({
          key: `question-${item.questionId}`,
          time: this.formatDisplayTime(item.createTime),
          text: `收到问题《${item.questionTitle}》`,
          rawTime: this.getTimeValue(item.createTime)
        })
      })
      ;(this.dashboard.recentFamilyTasks || []).slice(0, 5).forEach(item => {
        const eventTime = item.updateTime || item.createTime
        pushRecentItem({
          key: `family-${item.taskId}`,
          time: this.formatDisplayTime(eventTime),
          text: `亲子任务《${item.taskTitle}》${this.formatTaskStatus(item.status)}`,
          rawTime: this.getTimeValue(eventTime)
        })
      })
      return items
        .sort((a, b) => (b.rawTime || 0) - (a.rawTime || 0))
        .slice(0, 5)
        .map(({ rawTime, ...item }) => item)
        .concat(items.length ? [] : [{ key: 'empty', time: '当前', text: '暂无最新平台动态' }])
    },
    recentCourseCards() {
      return (this.dashboard.recentCourses || [])
        .map(item => ({ course: item, next: this.getNextClassSession(item) }))
        .filter(item => item.next)
        .sort((a, b) => a.next.start - b.next.start)
        .slice(0, 5)
        .map(({ course, next }) => ({
          key: `${course.courseId}-${next.start.getTime()}`,
          title: course.courseName,
          desc: `授课教师：${course.teacherName || '待安排'} · 容量 ${course.maxCapacity || course.capacity || 0} 人`,
          meta: this.formatClassTime(next)
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
    formatClassTime(session) {
      return `${parseTime(session.start, '{y}-{m}-{d}')} ${session.slot.startTime}`
    },
    parseCourseSlots(course) {
      const text = String(course.weekDay || '')
      const slots = []
      const pattern = /(周[一二三四五六日天])\s*(\d{1,2}:\d{2})?\s*-?\s*(\d{1,2}:\d{2})?/g
      let match = pattern.exec(text)
      while (match) {
        slots.push({
          weekDay: match[1] === '周天' ? '周日' : match[1],
          startTime: match[2] || course.startTime || '16:00',
          endTime: match[3] || course.endTime || '17:30'
        })
        match = pattern.exec(text)
      }
      return slots.length ? slots : [{ weekDay: '周一', startTime: course.startTime || '16:00', endTime: course.endTime || '17:30' }]
    },
    parseCourseDate(value) {
      const date = this.parseDate(value)
      if (!date) return null
      return new Date(date.getFullYear(), date.getMonth(), date.getDate())
    },
    parseClassDateTime(dateValue, timeValue) {
      const date = this.parseCourseDate(dateValue)
      if (!date) return null
      const parts = String(timeValue || '00:00').split(':')
      date.setHours(Number(parts[0] || 0), Number(parts[1] || 0), 0, 0)
      return date
    },
    getWeekDayNumber(weekDay) {
      return ({ 周日: 0, 周一: 1, 周二: 2, 周三: 3, 周四: 4, 周五: 5, 周六: 6 })[weekDay]
    },
    getNextClassSession(course) {
      if (course.status && course.status !== '0') return null
      const start = this.parseCourseDate(course.startDate)
      const end = this.parseCourseDate(course.endDate)
      const slots = this.parseCourseSlots(course)
      if (!start || !end || !slots.length) return null
      const now = new Date()
      const cursor = new Date(Math.max(start.getTime(), new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()))
      while (cursor <= end) {
        for (const slot of slots) {
          if (this.getWeekDayNumber(slot.weekDay) === cursor.getDay()) {
            const sessionStart = this.parseClassDateTime(cursor, slot.startTime)
            const sessionEnd = this.parseClassDateTime(cursor, slot.endTime)
            if (sessionEnd && sessionEnd >= now) {
              return { start: sessionStart, end: sessionEnd, slot }
            }
          }
        }
        cursor.setDate(cursor.getDate() + 1)
      }
      return null
    },
    formatCourseName(name) {
      if (!name) return '未命名课程'
      return name.length > 12 ? `${name.slice(0, 12)}...` : name
    },
    formatBusinessType(type) {
      const map = {
        homework_answer: '作业问答',
        course_notice: '课程通知',
        course_recommendation: '课程推荐',
        online_resource_recommendation: '网课推荐',
        parent_diagnosis: '家长陪学建议'
      }
      return map[type] || '智能辅助'
    },
    formatAiStatus(status) {
      return ({ success: '生成', failed: '失败' })[status] || '生成'
    },
    formatTaskStatus(status) {
      return ({ 0: '待完成', 1: '待家长确认', 2: '已完成', 3: '已退回' })[status] || '已更新'
    },
    getTimeValue(value) {
      const date = this.parseDate(value)
      return date ? date.getTime() : 0
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
          data: ['课程', '问答', '家长建议', '亲子任务', '学生']
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
            this.dashboard.totalQuestions || 0,
            this.dashboard.totalParentAdvices || 0,
            this.dashboard.totalFamilyTasks || 0,
            this.dashboard.activeStudents || 0
          ]
        }]
      })
      this.charts.push(chart)
    },
    renderStructureChart() {
      const chart = echarts.init(this.$refs.structureChart, 'macarons')
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: {
          bottom: 0,
          icon: 'circle',
          itemWidth: 9,
          itemHeight: 9
        },
        series: [{
          name: '服务结构',
          type: 'pie',
          radius: ['42%', '68%'],
          center: ['50%', '44%'],
          avoidLabelOverlap: true,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 4 },
          label: {
            color: '#405868',
            formatter: '{b}\n{d}%'
          },
          data: [
            { value: this.dashboard.totalEnrollments || 0, name: '课程报名' },
            { value: this.dashboard.totalQuestions || 0, name: '作业问答' },
            { value: this.dashboard.totalParentAdvices || 0, name: '家长建议' },
            { value: this.dashboard.completedFamilyTasks || 0, name: '完成任务' }
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
        const date = this.parseDate(item.createTime || item.startDate)
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
      const familyTaskMap = {}
      ;(this.dashboard.recentFamilyTasks || []).forEach(item => {
        const date = this.parseDate(item.updateTime || item.createTime)
        if (!date) return
        if (date > today || date < windowStart) return
        const key = parseTime(date, '{y}-{m}-{d}')
        familyTaskMap[key] = (familyTaskMap[key] || 0) + 1
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
          },
          {
            name: '亲子任务',
            type: 'line',
            smooth: true,
            areaStyle: { color: 'rgba(255, 177, 73, 0.12)' },
            data: categoryKeys.map(label => familyTaskMap[label] || 0)
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
  gap: 18px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(8, minmax(0, 1fr));
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
  background: #ffffff;
  box-shadow: 0 18px 36px rgba(31, 107, 127, 0.08);
}

.timeline-panel {
  padding: 24px 20px 18px 26px;
  min-height: 520px;
  display: flex;
  flex-direction: column;
}

.timeline-scroll {
  flex: 1;
  margin-top: 2px;
  padding: 4px 8px 0 4px;
  overflow-y: auto;
}

.timeline-scroll::-webkit-scrollbar {
  width: 6px;
}

.timeline-scroll::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(95, 143, 78, 0.22);
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
  background: #ffffff;
  border: 1px solid rgba(61, 136, 160, 0.26);
  box-shadow: 0 10px 24px rgba(34, 93, 108, 0.06);
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
  color: #4f7867;
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
  padding-left: 8px;
}

.timeline-panel ::v-deep .el-timeline-item {
  display: flex;
  align-items: center;
  padding-bottom: 14px;
}

.timeline-panel ::v-deep .el-timeline-item__tail {
  left: 6px;
  top: 50%;
  height: calc(100% + 14px);
  border-left-color: rgba(61, 136, 160, 0.2);
}

.timeline-panel ::v-deep .el-timeline-item:last-child .el-timeline-item__tail {
  display: none;
}

.timeline-panel ::v-deep .el-timeline-item__wrapper {
  width: 100%;
  padding-left: 34px;
}

.timeline-panel ::v-deep .el-timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-panel ::v-deep .el-timeline-item__node {
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  background: linear-gradient(180deg, #5ebf8e, #7fb08e);
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
  background: #ffffff;
  border: 1px solid rgba(61, 136, 160, 0.26);
  box-shadow: 0 10px 22px rgba(34, 93, 108, 0.05);
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

  .module-hero {
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
