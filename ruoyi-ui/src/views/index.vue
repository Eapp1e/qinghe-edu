<template>
  <div class="app-container edu-home">
    <el-row :gutter="16" class="hero-row">
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="hero-card">
          <div class="hero-badge">毕业设计演示首页</div>
          <h1>中小学智能课后服务平台</h1>
          <p class="hero-text">
            基于 SpringBoot、Vue、MyBatis、JWT 与大模型能力，覆盖学生、家长、教师、管理员四类角色。
            登录后可以直接从下方入口进入课程报名、作业问答、AI 辅助教学和平台统计演示流程。
          </p>
          <div class="hero-tags">
            <el-tag size="small">SpringBoot</el-tag>
            <el-tag size="small" type="success">Vue 2</el-tag>
            <el-tag size="small" type="warning">MyBatis</el-tag>
            <el-tag size="small" type="danger">AI 问答</el-tag>
          </div>
          <div class="hero-actions">
            <el-button type="primary" icon="el-icon-data-analysis" @click="goPage('/edu/dashboard')">进入平台看板</el-button>
            <el-button plain icon="el-icon-reading" @click="goPage('/edu/course')">查看课程中心</el-button>
            <el-button plain icon="el-icon-chat-line-round" @click="goPage('/edu/question')">体验作业问答</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="guide-card">
          <div slot="header">当前角色建议流程</div>
          <el-steps direction="vertical" :active="activeStep">
            <el-step v-for="(item, index) in roleGuide" :key="index" :title="item.title" :description="item.description" />
          </el-steps>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="stats-row">
      <el-col :xs="12" :sm="8" :lg="4" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="demo-card">
          <div slot="header">演示账号</div>
          <el-table :data="demoAccounts" size="small" border>
            <el-table-column prop="role" label="角色" width="110" />
            <el-table-column prop="username" label="账号" width="120" />
            <el-table-column prop="password" label="密码" width="120" />
            <el-table-column prop="scene" label="建议演示场景" />
          </el-table>
          <div class="tip">导入 `sql/edu_after_school.sql` 与 `sql/edu_demo_data.sql` 后即可直接使用这些账号。</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="demo-card">
          <div slot="header">推荐演示入口</div>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12" :md="8" v-for="entry in quickEntries" :key="entry.path">
              <div class="quick-entry" @click="goPage(entry.path)">
                <div class="quick-title">{{ entry.title }}</div>
                <div class="quick-desc">{{ entry.desc }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        <el-card shadow="never" class="demo-card timeline-card">
          <div slot="header">最近平台动态</div>
          <el-timeline>
            <el-timeline-item v-for="item in timeline" :key="item.key" :timestamp="item.time">
              {{ item.text }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
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
        { role: '管理员', username: 'edu_admin', password: 'admin123', scene: '看板、用户与 AI 日志管理' },
        { role: '教师', username: 'edu_teacher', password: 'admin123', scene: '发布课程、查看报名、生成 AI 通知' },
        { role: '家长', username: 'edu_parent', password: 'admin123', scene: '为孩子报名、查看学习记录' },
        { role: '学生', username: 'edu_student', password: 'admin123', scene: '提交作业问题、查看 AI 解答' }
      ],
      quickEntries: [
        { title: '平台看板', desc: '查看课程、报名、AI 调用统计', path: '/edu/dashboard' },
        { title: '课程中心', desc: '教师发布课程，学生和家长报名', path: '/edu/course' },
        { title: '学生档案', desc: '家长维护孩子与兴趣标签', path: '/edu/student' },
        { title: '报名管理', desc: '教师记录学习表现与完成情况', path: '/edu/enrollment' },
        { title: '作业问答', desc: '提交作业问题并查看 AI 解答', path: '/edu/question' },
        { title: 'AI 日志', desc: '管理员查看模型调用与安全记录', path: '/edu/aiLog' }
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
          { title: '查看平台看板', description: '先确认课程、报名与 AI 调用整体情况。' },
          { title: '检查 AI 日志', description: '查看内容生成、问答记录和安全状态。' },
          { title: '回到业务菜单', description: '按需切换课程、报名和档案页面继续演示。' }
        ],
        teacher: [
          { title: '进入课程中心', description: '发布一门兴趣班课程或编辑已有课程。' },
          { title: '生成 AI 通知', description: '点击课程操作中的 AI 通知或 AI 建议。' },
          { title: '维护报名记录', description: '去报名管理里记录学生学习情况。' }
        ],
        parent: [
          { title: '先看学生档案', description: '确认孩子档案已经绑定到当前家长账号。' },
          { title: '进入课程中心报名', description: '给孩子报名感兴趣的课后课程。' },
          { title: '查看学习与 AI 历史', description: '在报名管理和 AI 日志中查看过程记录。' }
        ],
        student: [
          { title: '浏览课程中心', description: '先查看当前已开放的课后服务课程。' },
          { title: '进入作业问答', description: '提交问题并等待 AI 返回解题思路。' },
          { title: '查看个人记录', description: '在报名或 AI 日志中回看自己的学习过程。' }
        ],
        visitor: [
          { title: '导入演示数据', description: '先执行初始化 SQL 与演示数据 SQL。' },
          { title: '分配演示角色', description: '使用管理员账号给测试用户分配角色。' },
          { title: '重新登录体验', description: '按不同角色切换系统体验完整流程。' }
        ]
      }
      return guides[this.currentRole]
    },
    activeStep() {
      return this.roleGuide.length
    },
    timeline() {
      const items = []
      ;(this.dashboard.recentCourses || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'course-' + item.courseId,
          time: item.weekDay + ' ' + item.startTime,
          text: '课程《' + item.courseName + '》由 ' + item.teacherName + ' 发布'
        })
      })
      ;(this.dashboard.recentQuestions || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'question-' + item.questionId,
          time: this.parseTime(item.createTime),
          text: item.studentName + ' 提交了问题《' + item.questionTitle + '》'
        })
      })
      ;(this.dashboard.recentAiLogs || []).slice(0, 2).forEach(item => {
        items.push({
          key: 'ai-' + item.logId,
          time: this.parseTime(item.createTime),
          text: 'AI 完成一次 ' + item.businessType + ' 调用，状态为 ' + item.status
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
  background: linear-gradient(180deg, #f5f8ef 0%, #ffffff 35%);
}

.hero-row,
.stats-row {
  margin-bottom: 16px;
}

.hero-card {
  min-height: 280px;
  border-radius: 20px;
  border: 1px solid #e3ebd2;
  background: linear-gradient(135deg, #fffef6 0%, #eef7e8 100%);
}

.hero-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  background: #daeac0;
  color: #456b25;
  font-size: 12px;
  margin-bottom: 14px;
}

.hero-card h1 {
  margin: 0 0 14px;
  font-size: 34px;
  color: #29401a;
}

.hero-text {
  margin: 0 0 16px;
  line-height: 1.8;
  color: #55634c;
}

.hero-tags {
  margin-bottom: 16px;
}

.hero-tags .el-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.hero-actions .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.guide-card,
.demo-card {
  border-radius: 18px;
}

.stat-card {
  text-align: center;
  border-radius: 16px;
}

.stat-label {
  color: #7a7f7b;
  font-size: 13px;
}

.stat-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
  color: #1f2d1b;
}

.quick-entry {
  min-height: 104px;
  margin-bottom: 12px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  background: #fafcf7;
  cursor: pointer;
  transition: all .2s ease;
}

.quick-entry:hover {
  border-color: #b6d38f;
  transform: translateY(-2px);
}

.quick-title {
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #314523;
}

.quick-desc {
  color: #687566;
  line-height: 1.6;
  font-size: 13px;
}

.timeline-card {
  margin-top: 16px;
}

.tip {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
