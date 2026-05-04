<template>
  <div class="app-container edu-home">
    <section class="hero-grid">
      <el-card shadow="never" class="hero-card">
        <div class="hero-copy">
          <span class="hero-badge">平台总览</span>
          <h1>中小学智能课后服务平台</h1>
          <p>
            平台围绕课后课程、网课资源、AI 辅助、家长陪学与亲子任务构建统一工作台，
            为学校、教师、家长与学生提供高效、安全、可追踪的课后服务支持。
          </p>
          <div class="hero-actions">
            <el-button type="primary" icon="el-icon-data-analysis" @click="goPage('/edu/dashboard')">查看统计看板</el-button>
            <el-button plain icon="el-icon-reading" @click="goPage('/edu/course')">进入课程中心</el-button>
          </div>
        </div>
        <div class="hero-panel">
          <div class="panel-title">当前角色工作建议</div>
          <div class="role-stack">
            <div v-for="item in roleGuide" :key="item.title" class="role-card">
              <strong>{{ item.title }}</strong>
              <span>{{ item.description }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="spotlight-card">
        <div class="spotlight-title">核心能力</div>
        <div class="spotlight-list">
          <div class="spotlight-item">
            <strong>课程与报名</strong>
            <span>支持课程发布、家长报名、容量控制和上课信息统一管理。</span>
          </div>
          <div class="spotlight-item">
            <strong>学习与 AI 辅助</strong>
            <span>面向学生提供作业问答、课程推荐、网课资源推荐和学习过程跟踪。</span>
          </div>
          <div class="spotlight-item">
            <strong>家校协同</strong>
            <span>支持家长陪学建议、亲子任务、积分兑换和家庭教育资源沉淀。</span>
          </div>
          <div class="spotlight-item">
            <strong>运营与治理</strong>
            <span>管理员可查看通知、统计报表、AI 日志、亲子任务与平台用户数据。</span>
          </div>
        </div>
      </el-card>
    </section>

    <section class="metric-grid">
      <div v-for="card in cards" :key="card.label" class="metric-card">
        <span>{{ card.label }}</span>
        <strong>{{ card.value }}</strong>
      </div>
    </section>

    <section class="content-grid">
      <el-card shadow="never" class="entry-card">
        <div slot="header">业务入口</div>
        <div class="entry-grid">
          <button v-for="entry in quickEntries" :key="entry.path" type="button" class="entry-item" @click="goPage(entry.path)">
            <strong>{{ entry.title }}</strong>
            <span>{{ entry.desc }}</span>
          </button>
        </div>
      </el-card>

      <el-card shadow="never" class="service-card">
        <div slot="header">服务覆盖</div>
        <div class="service-list">
          <div v-for="item in serviceScopes" :key="item.title" class="service-item">
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="timeline-card">
        <div slot="header">近期平台动态</div>
        <el-timeline>
          <el-timeline-item v-for="item in timeline" :key="item.key" :timestamp="item.time">
            {{ item.text }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </section>
  </div>
</template>

<script>
import { parseTime } from '@/utils/qinghe'
import { getEduDashboard } from '@/api/edu/dashboard'

export default {
  name: 'Index',
  data() {
    return {
      dashboard: {},
      quickEntries: [
        { title: '平台看板', desc: '查看课程、报名、问答、AI 与亲子任务统计。', path: '/edu/dashboard' },
        { title: '课程中心', desc: '维护课程信息、查看课程安排与报名情况。', path: '/edu/course' },
        { title: '网课中心', desc: '管理拓展学习资源，支持学生获取在线课程推荐。', path: '/edu/onlineCourse' },
        { title: '学生档案', desc: '管理学生基础信息、兴趣方向与成长画像。', path: '/edu/student' },
        { title: '报名记录', desc: '跟踪课程报名进度与学习过程记录。', path: '/edu/enrollment' },
        { title: '作业问答', desc: '查看学生提问、AI 解答与辅导建议。', path: '/edu/question' },
        { title: '家长陪学', desc: '沉淀 AI 诊断建议、历史记录与家长课堂资源。', path: '/edu/parentCompanion' },
        { title: '亲子任务', desc: '管理家庭约定、完成证明、积分奖励与兑换记录。', path: '/edu/familyTask' },
        { title: '通知中心', desc: '发布课程通知、报名提醒和家校消息。', path: '/edu/platformNotice' }
      ],
      serviceScopes: [
        { title: '学生端', desc: '查看课程、参与报名、提交作业问题、完成亲子任务并兑换积分奖励。' },
        { title: '家长端', desc: '为孩子报名课程、获取陪学建议、发布亲子任务并确认积分兑换。' },
        { title: '教师端', desc: '发布课程、管理名单、处理问答、生成教学建议与通知内容。' },
        { title: '管理端', desc: '统一管理用户、课程、资源、通知、AI 日志、亲子任务及平台统计数据。' }
      ]
    }
  },
  computed: {
    roles() {
      return this.$store.getters.roles || []
    },
    currentRole() {
      if (this.roles.includes('edu_teacher')) return 'teacher'
      if (this.roles.includes('edu_parent')) return 'parent'
      if (this.roles.includes('edu_student')) return 'student'
      if (this.roles.includes('edu_admin') || this.roles.includes('admin')) return 'admin'
      return 'visitor'
    },
    cards() {
      return [
        { label: '课程总数', value: this.dashboard.totalCourses || 0 },
        { label: '报名总数', value: this.dashboard.totalEnrollments || 0 },
        { label: '问答总数', value: this.dashboard.totalQuestions || 0 },
        { label: 'AI 调用', value: this.dashboard.totalAiCalls || 0 },
        { label: '家长建议', value: this.dashboard.totalParentAdvices || 0 },
        { label: '亲子任务', value: this.dashboard.totalFamilyTasks || 0 },
        { label: '活跃学生', value: this.dashboard.activeStudents || 0 }
      ]
    },
    roleGuide() {
      const guides = {
        admin: [
          { title: '查看平台看板', description: '了解课程开设、报名数量、问答、AI 调用与亲子任务概览。' },
          { title: '进入通知中心', description: '发布课后服务通知，跟踪消息已读状态和触达情况。' },
          { title: '检查家校协同', description: '查看家长陪学建议与亲子任务完成情况，保障平台服务闭环。' }
        ],
        teacher: [
          { title: '维护课程信息', description: '新增课程、更新课表安排，并完善课程说明与教学建议。' },
          { title: '查看报名记录', description: '掌握学生报名名单与学习跟踪信息，做好班级组织。' },
          { title: '处理作业问答', description: '结合 AI 辅助结果，快速回应学生课后学习问题。' }
        ],
        parent: [
          { title: '查看学生档案', description: '了解孩子的年级班级、兴趣方向和参与课程情况。' },
          { title: '办理课程报名', description: '进入课程中心为孩子选择合适的课后服务课程。' },
          { title: '使用家长陪学', description: '结合孩子学习记录生成陪伴建议，并通过亲子任务落实家庭约定。' }
        ],
        student: [
          { title: '浏览课程安排', description: '查看当前开放的课后课程与兴趣班信息。' },
          { title: '提交作业问题', description: '把学习中遇到的问题提交给平台，获取 AI 辅助建议。' },
          { title: '完成亲子任务', description: '接收家庭约定、上传完成证明，积累积分兑换现实奖励。' }
        ],
        visitor: [
          { title: '进入课程中心', description: '从课程信息开始了解平台提供的课后服务内容。' },
          { title: '查看统计看板', description: '通过数据面板掌握平台当前服务开展情况。' },
          { title: '关注平台通知', description: '及时获取课程安排、活动提醒与服务公告。' }
        ]
      }
      return guides[this.currentRole]
    },
    timeline() {
      const items = []
      ;(this.dashboard.recentCourses || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'course-' + item.courseId,
          time: item.weekDay + ' ' + item.startTime,
          text: `课程《${item.courseName}》已由 ${item.teacherName} 发布。`
        })
      })
      ;(this.dashboard.recentQuestions || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'question-' + item.questionId,
          time: parseTime(item.createTime),
          text: `${item.studentName} 提交了问题《${item.questionTitle}》。`
        })
      })
      ;(this.dashboard.recentAiLogs || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'ai-' + item.logId,
          time: parseTime(item.createTime),
          text: `AI 完成了一次${this.formatBusinessType(item.businessType)}处理，状态为${this.formatAiStatus(item.status)}。`
        })
      })
      ;(this.dashboard.recentFamilyTasks || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'family-' + item.taskId,
          time: parseTime(item.createTime),
          text: `亲子任务《${item.taskTitle}》已更新为${this.formatTaskStatus(item.status)}。`
        })
      })
      return items.length ? items : [{ key: 'empty', time: '当前', text: '暂无最新平台动态。' }]
    }
  },
  created() {
    this.getDashboard()
  },
  methods: {
    getDashboard() {
      getEduDashboard().then(res => {
        this.dashboard = res.data || {}
      }).catch(() => {
        this.dashboard = {}
      })
    },
    goPage(path) {
      this.$router.push(path)
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
      return ({ success: '已生成', failed: '生成失败' })[status] || '已生成'
    },
    formatTaskStatus(status) {
      return ({ 0: '待完成', 1: '待确认', 2: '已完成', 3: '已退回' })[status] || '已更新'
    }
  }
}
</script>

<style scoped>
.edu-home {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
}

.edu-home::before,
.edu-home::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(6px);
  pointer-events: none;
}

.edu-home::before {
  top: -60px;
  right: 4%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(49, 230, 187, 0.18), transparent 68%);
}

.edu-home::after {
  left: -70px;
  bottom: 10%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(88, 186, 255, 0.15), transparent 70%);
}

.hero-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1.6fr 0.9fr;
  gap: 18px;
  margin-bottom: 18px;
}

.hero-card {
  display: grid;
  grid-template-columns: 1.2fr 0.95fr;
  gap: 22px;
  min-height: 320px;
  border: 1px solid rgba(97, 223, 207, 0.2);
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(76, 240, 189, 0.24), transparent 30%),
    radial-gradient(circle at bottom left, rgba(95, 194, 255, 0.18), transparent 26%),
    linear-gradient(135deg, rgba(253, 255, 255, 0.96) 0%, rgba(236, 255, 249, 0.96) 52%, rgba(238, 247, 255, 0.96) 100%);
  box-shadow:
    0 24px 44px rgba(34, 131, 145, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.85);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(39, 228, 188, 0.2), rgba(197, 244, 255, 0.72));
  color: #0a8a73;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 10px 22px rgba(27, 193, 182, 0.14);
}

.hero-copy h1 {
  margin: 18px 0 14px;
  color: #163743;
  font-size: 40px;
  line-height: 1.15;
  text-shadow: 0 10px 26px rgba(37, 184, 194, 0.12);
}

.hero-copy p {
  margin: 0;
  color: #5f7684;
  line-height: 1.9;
  font-size: 15px;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.hero-panel {
  padding: 18px;
  border-radius: 24px;
  border: 1px solid rgba(112, 220, 224, 0.22);
  background:
    linear-gradient(160deg, rgba(255, 255, 255, 0.82), rgba(229, 255, 248, 0.66)),
    rgba(255, 255, 255, 0.7);
  box-shadow:
    0 20px 36px rgba(44, 135, 149, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.panel-title,
.spotlight-title {
  margin-bottom: 14px;
  color: #1e4250;
  font-size: 18px;
  font-weight: 700;
}

.role-stack,
.spotlight-list,
.service-list,
.entry-grid {
  display: grid;
  gap: 12px;
}

.role-card,
.spotlight-item,
.service-item,
.entry-item,
.metric-card {
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(234, 251, 255, 0.82));
  border: 1px solid rgba(120, 219, 222, 0.22);
  box-shadow: 0 16px 28px rgba(44, 135, 149, 0.08);
}

.role-card,
.spotlight-item,
.service-item {
  padding: 16px 18px;
}

.role-card strong,
.spotlight-item strong,
.service-item strong,
.entry-item strong {
  display: block;
  color: #1c4150;
  margin-bottom: 8px;
}

.role-card span,
.spotlight-item span,
.entry-item span,
.service-item p {
  color: #688090;
  line-height: 1.8;
  font-size: 14px;
  margin: 0;
}

.spotlight-card,
.entry-card,
.service-card,
.timeline-card {
  border: 1px solid rgba(109, 220, 216, 0.18);
  border-radius: 26px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(238, 253, 255, 0.96));
  box-shadow:
    0 22px 40px rgba(41, 130, 141, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.78);
}

.metric-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.metric-card {
  padding: 18px;
  position: relative;
  overflow: hidden;
}

.metric-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.36), transparent 56%);
  pointer-events: none;
}

.metric-card span {
  display: block;
  color: #6f8593;
  font-size: 13px;
}

.metric-card strong {
  display: block;
  margin-top: 10px;
  color: #163745;
  font-size: 30px;
}

.content-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: 18px;
}

.entry-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.entry-item {
  padding: 18px;
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.entry-item:hover {
  transform: translateY(-4px);
  border-color: rgba(49, 224, 194, 0.34);
  box-shadow: 0 18px 30px rgba(39, 173, 180, 0.16);
}

::v-deep .el-card {
  overflow: hidden;
}

::v-deep .el-card__body {
  position: relative;
}

::v-deep .el-card__header {
  border-bottom: 1px solid rgba(104, 213, 214, 0.16);
  color: #1f4252;
  font-weight: 700;
}

::v-deep .el-timeline-item__timestamp {
  color: #6f8492;
}

::v-deep .el-timeline-item__node {
  background-color: #20dcb7;
  box-shadow: 0 0 0 6px rgba(32, 220, 183, 0.1);
}

::v-deep .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #14e0a9 0%, #0fc9c3 52%, #2898ff 100%);
  box-shadow: 0 16px 30px rgba(25, 179, 180, 0.24);
}

::v-deep .el-button--primary:hover,
::v-deep .el-button--primary:focus {
  filter: saturate(108%);
  box-shadow: 0 18px 34px rgba(25, 179, 180, 0.3);
}

::v-deep .el-button.is-plain {
  border-color: rgba(52, 207, 196, 0.32);
  color: #19806e;
  background: rgba(238, 255, 251, 0.92);
}

@media (max-width: 1200px) {
  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .content-grid,
  .hero-grid,
  .hero-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .metric-grid,
  .entry-grid {
    grid-template-columns: 1fr;
  }

  .hero-copy h1 {
    font-size: 30px;
  }
}
</style>
