<template>
  <div class="app-container">
    <el-row :gutter="16" class="mb16">
      <el-col :span="4" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card>
          <div slot="header">最近课程</div>
          <el-timeline>
            <el-timeline-item v-for="item in dashboard.recentCourses" :key="item.courseId" :timestamp="item.weekDay + ' ' + item.startTime">
              {{ item.courseName }} / {{ item.teacherName }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">最近提问</div>
          <el-timeline>
            <el-timeline-item v-for="item in dashboard.recentQuestions" :key="item.questionId" :timestamp="parseTime(item.createTime)">
              {{ item.questionTitle }} / {{ item.studentName }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">AI 调用记录</div>
          <el-timeline>
            <el-timeline-item v-for="item in dashboard.recentAiLogs" :key="item.logId" :timestamp="parseTime(item.createTime)">
              {{ item.businessType }} / {{ item.status }}
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
  name: 'EduDashboard',
  data() {
    return {
      dashboard: {
        recentCourses: [],
        recentQuestions: [],
        recentAiLogs: []
      }
    }
  },
  computed: {
    cards() {
      return [
        { label: '课程总数', value: this.dashboard.totalCourses || 0 },
        { label: '报名总数', value: this.dashboard.totalEnrollments || 0 },
        { label: '提问总数', value: this.dashboard.totalQuestions || 0 },
        { label: 'AI调用', value: this.dashboard.totalAiCalls || 0 },
        { label: '活跃学生', value: this.dashboard.activeStudents || 0 },
        { label: '活跃教师', value: this.dashboard.activeTeachers || 0 }
      ]
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      getEduDashboard().then(res => {
        this.dashboard = res.data || {}
      })
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-label {
  color: #909399;
  font-size: 14px;
}
.stat-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}
.mb16 {
  margin-bottom: 16px;
}
</style>
