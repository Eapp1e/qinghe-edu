<template>
  <div class="app-container edu-home">
    <section class="hero-grid">
      <el-card shadow="never" class="hero-card">
        <div class="hero-copy">
          <span class="hero-badge">毕业设计演示系统</span>
          <h1>中小学智能课后服务平台</h1>
          <p>
            基于 Spring Boot、Vue、MyBatis、JWT 与 AI 能力构建，面向学生、家长、教师和管理员提供一体化课后服务协同体验。
          </p>
          <div class="hero-actions">
            <el-button type="primary" icon="el-icon-data-analysis" @click="goPage('/edu/dashboard')">进入平台看板</el-button>
            <el-button plain icon="el-icon-reading" @click="goPage('/edu/course')">查看课程中心</el-button>
          </div>
        </div>
        <div class="hero-panel">
          <div class="panel-title">推荐演示顺序</div>
          <div class="role-stack">
            <div v-for="item in roleGuide" :key="item.title" class="role-card">
              <strong>{{ item.title }}</strong>
              <span>{{ item.description }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="spotlight-card">
        <div class="spotlight-title">平台摘要</div>
        <div class="spotlight-list">
          <div class="spotlight-item">
            <strong>课程与报名</strong>
            <span>支持教师发课、家长代报、学生查看课程安排。</span>
          </div>
          <div class="spotlight-item">
            <strong>作业与 AI</strong>
            <span>围绕课后辅导场景提供提问、解答与日志追踪。</span>
          </div>
          <div class="spotlight-item">
            <strong>平台治理</strong>
            <span>管理员可统一查看通知、日志、统计和平台用户。</span>
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
      <el-card shadow="never" class="account-card">
        <div slot="header">演示账号</div>
        <div class="account-list">
          <div v-for="item in demoAccounts" :key="item.username" class="account-item">
            <strong>{{ item.role }}</strong>
            <span>{{ item.username }} / {{ item.password }}</span>
            <p>{{ item.scene }}</p>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="entry-card">
        <div slot="header">快捷入口</div>
        <div class="entry-grid">
          <button v-for="entry in quickEntries" :key="entry.path" type="button" class="entry-item" @click="goPage(entry.path)">
            <strong>{{ entry.title }}</strong>
            <span>{{ entry.desc }}</span>
          </button>
        </div>
      </el-card>

      <el-card shadow="never" class="timeline-card">
        <div slot="header">最近平台动态</div>
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
import { getEduDashboard } from '@/api/edu/dashboard'

export default {
  name: 'Index',
  data() {
    return {
      dashboard: {},
      demoAccounts: [
        { role: '管理员', username: 'edu_admin', password: 'admin123', scene: '查看看板、AI 日志与平台用户管理' },
        { role: '教师', username: 'edu_teacher', password: 'admin123', scene: '发布课程、管理报名并使用 AI 辅助通知' },
        { role: '家长', username: 'edu_parent', password: 'admin123', scene: '查看学生档案并为孩子报名课程' },
        { role: '学生', username: 'edu_student', password: 'admin123', scene: '浏览课程并提交作业问题获取 AI 解答' }
      ],
      quickEntries: [
        { title: '平台看板', desc: '查看课程、报名与 AI 调用统计', path: '/edu/dashboard' },
        { title: '课程中心', desc: '浏览课程、发布课程和参与报名', path: '/edu/course' },
        { title: '学生档案', desc: '维护学生画像和兴趣方向', path: '/edu/student' },
        { title: '报名记录', desc: '查看课程报名和学习过程记录', path: '/edu/enrollment' },
        { title: '作业问答', desc: '演示 AI 问答与作业辅导能力', path: '/edu/question' },
        { title: 'AI 日志', desc: '查看模型调用与平台安全记录', path: '/edu/aiLog' }
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
        { label: '作业问题', value: this.dashboard.totalQuestions || 0 },
        { label: 'AI 调用', value: this.dashboard.totalAiCalls || 0 },
        { label: '活跃学生', value: this.dashboard.activeStudents || 0 },
        { label: '活跃教师', value: this.dashboard.activeTeachers || 0 }
      ]
    },
    roleGuide() {
      const guides = {
        admin: [
          { title: '平台看板', description: '先查看课程、报名和 AI 调用全局统计。' },
          { title: '平台通知', description: '再展示通知发布、已读情况和平台治理能力。' },
          { title: 'AI 日志', description: '最后补充大模型调用记录和安全追踪。' }
        ],
        teacher: [
          { title: '课程中心', description: '展示新建课程、修改课程与课程说明。' },
          { title: '报名记录', description: '查看学生报名名单与学习过程信息。' },
          { title: 'AI 辅助', description: '结合课程页面演示 AI 通知或教学建议。' }
        ],
        parent: [
          { title: '学生档案', description: '先进入学生档案查看孩子信息与兴趣方向。' },
          { title: '课程报名', description: '再前往课程中心为孩子完成报名。' },
          { title: '过程查看', description: '最后通过报名记录查看学习过程。' }
        ],
        student: [
          { title: '课程浏览', description: '浏览当前开放的课后课程与兴趣班。' },
          { title: '作业问答', description: '提交作业问题，获取 AI 解题思路。' },
          { title: '学习回看', description: '通过记录回顾自己的参与情况。' }
        ],
        visitor: [
          { title: '导入数据', description: '先导入初始化 SQL 和演示数据。' },
          { title: '分配角色', description: '确保四类演示账号具备对应角色。' },
          { title: '重新登录', description: '切换不同身份依次体验完整流程。' }
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
          text: `课程《${item.courseName}》由 ${item.teacherName} 发布`
        })
      })
      ;(this.dashboard.recentQuestions || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'question-' + item.questionId,
          time: this.parseTime(item.createTime),
          text: `${item.studentName} 提交了问题《${item.questionTitle}》`
        })
      })
      ;(this.dashboard.recentAiLogs || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'ai-' + item.logId,
          time: this.parseTime(item.createTime),
          text: `AI 完成一次 ${item.businessType} 调用，状态为 ${item.status}`
        })
      })
      return items.length ? items : [{ key: 'empty', time: '当前', text: '导入演示数据后，这里会显示最近课程、提问和 AI 调用记录。' }]
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
    }
  }
}
</script>

<style scoped>
.edu-home {
  padding-top: 6px;
}

.hero-grid {
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
  background:
    radial-gradient(circle at top right, rgba(214, 230, 182, 0.55), transparent 28%),
    linear-gradient(135deg, #fffaf1 0%, #f3f6eb 100%);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: #e4efd1;
  color: #456b25;
  font-size: 12px;
  font-weight: 700;
}

.hero-copy h1 {
  margin: 18px 0 14px;
  color: #243a24;
  font-size: 40px;
  line-height: 1.15;
}

.hero-copy p {
  margin: 0;
  color: #67736a;
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
  border: 1px solid rgba(130, 112, 82, 0.12);
  border-radius: 22px;
  background: rgba(36, 58, 36, 0.94);
}

.panel-title {
  color: #d8ebba;
  font-size: 13px;
  letter-spacing: 1px;
}

.role-stack {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.role-card {
  padding: 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.07);
}

.role-card strong {
  display: block;
  margin-bottom: 8px;
  color: #fff7ea;
  font-size: 16px;
}

.role-card span {
  color: rgba(255, 247, 234, 0.72);
  line-height: 1.7;
  font-size: 13px;
}

.spotlight-card {
  background:
    radial-gradient(circle at top left, rgba(244, 214, 160, 0.24), transparent 28%),
    linear-gradient(180deg, #fffdf7 0%, #f7f2e8 100%);
}

.spotlight-title {
  color: #43563f;
  font-size: 15px;
  font-weight: 700;
}

.spotlight-list {
  display: grid;
  gap: 14px;
  margin-top: 16px;
}

.spotlight-item {
  padding: 16px;
  border: 1px solid rgba(130, 112, 82, 0.1);
  border-radius: 18px;
  background: rgba(255, 252, 246, 0.9);
}

.spotlight-item strong {
  display: block;
  margin-bottom: 8px;
  color: #32452b;
}

.spotlight-item span {
  color: #778176;
  line-height: 1.7;
  font-size: 13px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.metric-card {
  padding: 18px 16px;
  border: 1px solid rgba(130, 112, 82, 0.12);
  border-radius: 20px;
  background: rgba(255, 251, 244, 0.92);
  box-shadow: 0 10px 20px rgba(55, 62, 47, 0.05);
}

.metric-card span {
  color: #7b8478;
  font-size: 13px;
}

.metric-card strong {
  display: block;
  margin-top: 10px;
  color: #203021;
  font-size: 30px;
  line-height: 1;
}

.content-grid {
  display: grid;
  grid-template-columns: 1.05fr 1.2fr 1fr;
  gap: 18px;
}

.account-list,
.entry-grid {
  display: grid;
  gap: 12px;
}

.account-item,
.entry-item {
  padding: 16px;
  border: 1px solid rgba(130, 112, 82, 0.1);
  border-radius: 18px;
  background: rgba(255, 252, 247, 0.92);
}

.account-item strong,
.entry-item strong {
  display: block;
  color: #2f4029;
  font-size: 16px;
}

.account-item span,
.entry-item span {
  display: block;
  margin-top: 8px;
  color: #788174;
  font-size: 13px;
}

.account-item p {
  margin: 10px 0 0;
  color: #6d766a;
  line-height: 1.7;
  font-size: 13px;
}

.entry-item {
  text-align: left;
  cursor: pointer;
  transition: all 0.22s ease;
}

.entry-item:hover {
  transform: translateY(-2px);
  border-color: rgba(95, 143, 78, 0.36);
  box-shadow: 0 12px 24px rgba(95, 143, 78, 0.1);
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
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .hero-copy h1 {
    font-size: 32px;
  }
}
</style>
