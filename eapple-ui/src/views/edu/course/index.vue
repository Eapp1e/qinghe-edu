<template>
  <div class="app-container course-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Course Center</span>
        <h1>{{ pageTitle }}</h1>
        <p>{{ pageDescription }}</p>
      </div>
      <div :class="['hero-stats', { 'hero-stats--compact': !isTeacherView }]">
        <div class="stat-card">
          <span>{{ isTeacherView ? '我的课程' : '课程总数' }}</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>{{ isTeacherView ? '已启用课程' : '开放课程' }}</span>
          <strong>{{ openCount }}</strong>
        </div>
        <div v-if="isTeacherView" class="stat-card">
          <span>已停开课程</span>
          <strong>{{ closedCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <div v-if="isTeacherView" class="teacher-cta-strip">
        <el-button
          size="medium"
          icon="el-icon-plus"
          class="teacher-cta-button"
          @click="handleAdd"
        >
          发布新课程
        </el-button>
      </div>

      <div class="toolbar-main">
        <el-form v-show="showSearch" :inline="true" :model="queryParams" size="small" class="query-form">
          <el-form-item label="课程名称">
            <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="queryParams.category" placeholder="请输入课程分类" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item v-if="canManageCourse" label="状态">
            <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
              <el-option label="启用中" value="0" />
              <el-option label="已停开" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="toolbar-actions">
          <el-button
            v-if="canManageCourse && !isTeacherView"
            type="primary"
            plain
            size="mini"
            icon="el-icon-plus"
            @click="handleAdd"
          >
            发布课程
          </el-button>
          <right-toolbar @queryTable="getList" />
        </div>
      </div>
    </section>

    <el-table v-loading="loading" :data="courseList" class="content-table">
      <el-table-column label="课程" min-width="180">
        <template slot-scope="scope">
          <div class="course-name-cell">
            <strong>{{ scope.row.courseName }}</strong>
            <span>{{ scope.row.description || '暂无课程简介' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分类" prop="category" width="120" />
      <el-table-column label="教师" prop="teacherName" width="120" />
      <el-table-column label="上课时间" width="180">
        <template slot-scope="scope">{{ scope.row.weekDay }} {{ scope.row.startTime }}-{{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column label="地点" prop="campus" width="140" />
      <el-table-column label="容量" width="100">
        <template slot-scope="scope">{{ scope.row.currentCapacity }}/{{ scope.row.maxCapacity }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? (isTeacherView ? '已启用' : '开放中') : (isTeacherView ? '已停开' : '已关闭') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="isTeacherView ? '教师操作' : '操作'" min-width="320" fixed="right">
        <template slot-scope="scope">
          <template v-if="canEnrollCourse">
            <el-button size="mini" type="text" @click="handleEnroll(scope.row)">
              {{ scope.row.enrolled === 'Y' ? '取消报名' : '报名' }}
            </el-button>
          </template>
          <template v-if="canManageCourse">
            <el-button size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleToggleStatus(scope.row)">
              {{ scope.row.status === '0' ? '停开课程' : '启用课程' }}
            </el-button>
            <el-button
              size="mini"
              type="text"
              :loading="isAiGenerating(scope.row, 'notice')"
              :disabled="isAnyAiGenerating(scope.row)"
              @click="handleNotice(scope.row)"
            >
              {{ isAiGenerating(scope.row, 'notice') ? 'AI 生成中...' : 'AI 通知' }}
            </el-button>
            <el-button
              size="mini"
              type="text"
              :loading="isAiGenerating(scope.row, 'suggestion')"
              :disabled="isAnyAiGenerating(scope.row)"
              @click="handleSuggestion(scope.row)"
            >
              {{ isAiGenerating(scope.row, 'suggestion') ? 'AI 生成中...' : 'AI 建议' }}
            </el-button>
            <el-button v-if="isAdminView" size="mini" type="text" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="760px">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课程分类" prop="category"><el-input v-model="form.category" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="校区" prop="campus"><el-input v-model="form.campus" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="上课星期" prop="weekDay"><el-input v-model="form.weekDay" placeholder="如：周三" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="开始时间" prop="startTime"><el-input v-model="form.startTime" placeholder="15:30" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束时间" prop="endTime"><el-input v-model="form.endTime" placeholder="17:00" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="最大容量"><el-input-number v-model="form.maxCapacity" :min="1" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio label="0">启用</el-radio><el-radio label="1">停开</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="课程简介"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="aiPreviewTitle" :visible.sync="aiPreviewOpen" width="760px" class="ai-preview-dialog">
      <div v-loading="aiGenerating" element-loading-text="AI 正在生成内容，请稍候..." class="ai-preview-panel">
        <div class="ai-preview-head">
          <div>
            <span class="ai-preview-badge">{{ aiPreviewType }}</span>
            <h3>{{ aiPreviewCourseName }}</h3>
            <p>生成结果已同步保存到课程，可直接复制使用。</p>
          </div>
          <el-button size="mini" type="primary" plain icon="el-icon-document-copy" @click="copyAiPreview">复制内容</el-button>
        </div>
        <div class="ai-preview-body">{{ aiPreviewContent }}</div>
      </div>
      <div slot="footer">
        <el-button @click="aiPreviewOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addCourse,
  cancelEnrollCourse,
  delCourse,
  enrollCourse,
  generateCourseNotice,
  generateTeachingSuggestion,
  listCourse,
  updateCourse
} from '@/api/edu/course'
import { listMyChildren } from '@/api/edu/student'
import { getCurrentAiModel, listAiModels } from '@/api/edu/ai'

export default {
  name: 'EduCourse',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      courseList: [],
      childrenList: [],
      aiModelOptions: [],
      currentAiModel: '',
      open: false,
      aiGenerating: false,
      aiGeneratingCourseId: undefined,
      aiGeneratingType: '',
      aiPreviewOpen: false,
      aiPreviewTitle: '',
      aiPreviewType: '',
      aiPreviewCourseName: '',
      aiPreviewContent: '',
      title: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: undefined,
        category: undefined,
        status: undefined
      },
      form: {},
      rules: {
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        category: [{ required: true, message: '请输入课程分类', trigger: 'blur' }],
        campus: [{ required: true, message: '请输入上课地点', trigger: 'blur' }],
        weekDay: [{ required: true, message: '请输入上课星期', trigger: 'blur' }]
      }
    }
  },
  computed: {
    roleKeys() {
      return this.$store.getters.roles || []
    },
    isTeacherView() {
      return this.roleKeys.includes('edu_teacher')
    },
    isAdminView() {
      return this.roleKeys.includes('admin') || this.roleKeys.includes('edu_admin')
    },
    canManageCourse() {
      return this.isTeacherView || this.isAdminView
    },
    canEnrollCourse() {
      return this.roleKeys.includes('edu_student') || this.roleKeys.includes('edu_parent')
    },
    openCount() {
      return this.courseList.filter(item => item.status === '0').length
    },
    closedCount() {
      return this.courseList.filter(item => item.status === '1').length
    },
    pageTitle() {
      if (this.isTeacherView) {
        return '我的课程'
      }
      if (this.isAdminView) {
        return '课程中心'
      }
      return '课程中心'
    },
    pageDescription() {
      if (this.isTeacherView) {
        return '教师可发布课后课程，集中查看自己发布的课程，并按教学安排选择启用或停开课程。'
      }
      if (this.isAdminView) {
        return '管理员可查看全部课程，统一掌握课程开设情况、教师安排和课程状态。'
      }
      return '集中展示课后课程、兴趣班和课程安排，支持课程报名、取消报名以及查看课程信息。'
    },
    currentAiModelLabel() {
      const current = this.aiModelOptions.find(item => item.modelName === this.currentAiModel)
      return current ? current.displayName : this.currentAiModel
    }
  },
  created() {
    this.getList()
    this.getChildren()
    this.getAiModelConfig()
  },
  methods: {
    getList() {
      this.loading = true
      listCourse(this.queryParams).then(res => {
        this.courseList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    getChildren() {
      if (!this.roleKeys.includes('edu_parent')) {
        return
      }
      listMyChildren().then(res => {
        this.childrenList = res.data || []
      })
    },
    getAiModelConfig() {
      if (!this.canManageCourse) {
        return
      }
      listAiModels().then(res => {
        this.aiModelOptions = res.data || []
      })
      getCurrentAiModel().then(res => {
        this.currentAiModel = res.data || ''
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        courseName: undefined,
        category: undefined,
        status: undefined
      }
      this.getList()
    },
    reset() {
      this.form = {
        courseId: undefined,
        maxCapacity: 30,
        status: '0',
        currentCapacity: 0
      }
    },
    handleAdd() {
      this.reset()
      this.title = '发布课程'
      this.open = true
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.title = '编辑课程'
      this.open = true
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.courseId ? updateCourse(this.form) : addCourse(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.courseId ? '课程已更新' : '课程发布成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleToggleStatus(row) {
      const nextStatus = row.status === '0' ? '1' : '0'
      const actionText = nextStatus === '0' ? '启用' : '停开'
      this.$modal.confirm(`确认${actionText}课程“${row.courseName}”吗？`).then(() => {
        return updateCourse({ ...row, status: nextStatus })
      }).then(() => {
        this.$modal.msgSuccess(`课程已${actionText}`)
        this.getList()
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除课程“${row.courseName}”吗？`).then(() => delCourse(row.courseId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    isAiGenerating(row, type) {
      return this.aiGenerating && this.aiGeneratingCourseId === row.courseId && this.aiGeneratingType === type
    },
    isAnyAiGenerating(row) {
      return this.aiGenerating && this.aiGeneratingCourseId === row.courseId
    },
    startAiGenerating(row, type, title, previewType) {
      this.aiGenerating = true
      this.aiGeneratingCourseId = row.courseId
      this.aiGeneratingType = type
      this.aiPreviewTitle = title
      this.aiPreviewType = previewType
      this.aiPreviewCourseName = row.courseName
      this.aiPreviewContent = ''
      this.aiPreviewOpen = true
    },
    stopAiGenerating() {
      this.aiGenerating = false
      this.aiGeneratingCourseId = undefined
      this.aiGeneratingType = ''
    },
    normalizeAiContent(content) {
      if (!content) {
        return ''
      }
      return content
        .replace(/\r\n/g, '\n')
        .replace(/\*\*(.*?)\*\*/g, '$1')
        .replace(/__(.*?)__/g, '$1')
        .replace(/`([^`]+)`/g, '$1')
        .replace(/^#{1,6}\s*/gm, '')
        .replace(/^[*-]\s+/gm, '')
        .replace(/\n{3,}/g, '\n\n')
        .trim()
    },
    resolveStudentUserId() {
      if (!this.roleKeys.includes('edu_parent')) {
        return undefined
      }
      if (this.childrenList.length === 0) {
        this.$modal.msgError('请先维护孩子档案后再报名')
        return null
      }
      if (this.childrenList.length > 1) {
        this.$modal.msgError('当前账号关联了多个孩子，请先保留一个孩子档案或按需扩展选择功能')
        return null
      }
      return this.childrenList[0].studentUserId
    },
    handleEnroll(row) {
      const studentUserId = this.resolveStudentUserId()
      if (studentUserId === null) {
        return
      }
      if (row.enrolled === 'Y') {
        this.$modal.confirm('确认取消该课程报名吗？').then(() => {
          return cancelEnrollCourse(row.courseId, studentUserId)
        }).then(() => {
          this.$modal.msgSuccess('已取消报名')
          this.getList()
        }).catch(() => {})
        return
      }
      enrollCourse(row.courseId, studentUserId).then(() => {
        this.$modal.msgSuccess('报名成功')
        this.getList()
      }).catch(() => {})
    },
    handleNotice(row) {
      this.startAiGenerating(row, 'notice', 'AI 生成通知', '课程通知')
      generateCourseNotice(row.courseId).then(res => {
        const content = res.data || row.aiNotice || '当前暂无生成内容，请稍后重试。'
        this.openAiPreview('AI 生成通知', '课程通知', row, content)
        this.getList()
      }).catch(() => {
        if (row.aiNotice) {
          this.$modal.msgWarning('AI 通知生成超时，已为你展示上一次成功生成的内容')
          this.openAiPreview('AI 生成通知', '课程通知', row, row.aiNotice)
        } else {
          this.aiPreviewOpen = false
          this.$modal.msgError('AI 通知生成超时，请稍后重试')
        }
      }).finally(() => {
        this.stopAiGenerating()
      })
    },
    handleSuggestion(row) {
      this.startAiGenerating(row, 'suggestion', 'AI 教学建议', '教学建议')
      generateTeachingSuggestion(row.courseId).then(res => {
        const content = res.data || row.aiSuggestion || '当前暂无生成内容，请稍后重试。'
        this.openAiPreview('AI 教学建议', '教学建议', row, content)
        this.getList()
      }).catch(() => {
        if (row.aiSuggestion) {
          this.$modal.msgWarning('AI 教学建议生成超时，已为你展示上一次成功生成的内容')
          this.openAiPreview('AI 教学建议', '教学建议', row, row.aiSuggestion)
        } else {
          this.aiPreviewOpen = false
          this.$modal.msgError('AI 教学建议生成超时，请稍后重试')
        }
      }).finally(() => {
        this.stopAiGenerating()
      })
    },
    openAiPreview(title, type, row, content) {
      this.aiPreviewTitle = title
      this.aiPreviewType = type
      this.aiPreviewCourseName = row.courseName
      this.aiPreviewContent = this.normalizeAiContent(content) || '暂无生成内容'
      this.aiPreviewOpen = true
    },
    copyAiPreview() {
      const text = this.aiPreviewContent
      if (!text) {
        this.$modal.msgError('当前没有可复制的内容')
        return
      }
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(() => {
          this.$modal.msgSuccess('内容已复制')
        }).catch(() => {
          this.fallbackCopy(text)
        })
        return
      }
      this.fallbackCopy(text)
    },
    fallbackCopy(text) {
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.setAttribute('readonly', 'readonly')
      textarea.style.position = 'absolute'
      textarea.style.left = '-9999px'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      this.$modal.msgSuccess('内容已复制')
    }
  }
}
</script>

<style scoped>
.course-page {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
}

.course-page::before,
.course-page::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(8px);
  pointer-events: none;
}

.course-page::before {
  top: -50px;
  right: 6%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(38, 232, 183, 0.18), transparent 68%);
}

.course-page::after {
  left: -70px;
  bottom: 12%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(91, 187, 255, 0.14), transparent 70%);
}

.hero-panel {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 28px;
  border: 1px solid rgba(103, 224, 214, 0.18);
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(63, 229, 190, 0.2), transparent 28%),
    radial-gradient(circle at bottom left, rgba(72, 153, 255, 0.16), transparent 24%),
    linear-gradient(135deg, rgba(21, 34, 46, 0.96), rgba(29, 73, 82, 0.92));
  box-shadow:
    0 24px 44px rgba(15, 35, 46, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

.hero-copy {
  max-width: 760px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(32, 224, 182, 0.14);
  color: #8ff6dc;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
  box-shadow: 0 10px 20px rgba(12, 34, 40, 0.2);
}

.hero-copy h1 {
  margin: 16px 0 12px;
  color: #ffffff;
  font-size: 34px;
}

.hero-copy p {
  margin: 0;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.9;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  min-width: 380px;
  margin-left: auto;
}

.hero-stats--compact {
  grid-template-columns: repeat(2, 1fr);
  min-width: 250px;
}

.stat-card {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16), inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.stat-card span {
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 10px;
  color: #ffffff;
  font-size: 28px;
}

.toolbar-panel {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  padding: 20px 22px 6px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow: 0 22px 40px rgba(41, 130, 141, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
}

.toolbar-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.teacher-cta-strip {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0;
}

.teacher-cta-button {
  min-width: 176px;
  height: 44px;
  padding: 0 20px;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.01em;
  border-radius: 14px;
  border: 1px solid rgba(68, 196, 181, 0.3);
  background: linear-gradient(180deg, rgba(228, 248, 239, 0.98), rgba(214, 242, 231, 0.96));
  color: #116f64;
  box-shadow: 0 8px 16px rgba(41, 130, 141, 0.1);
}

::v-deep .teacher-cta-button:hover,
::v-deep .teacher-cta-button:focus {
  background: linear-gradient(180deg, rgba(222, 245, 235, 0.98), rgba(206, 237, 225, 0.96));
  border-color: rgba(68, 196, 181, 0.36);
  color: #0f665d;
  box-shadow: 0 10px 18px rgba(41, 130, 141, 0.12);
}

::v-deep .teacher-cta-button i {
  font-size: 14px;
  font-weight: 700;
}

.query-form {
  flex: 1;
  margin-bottom: 0;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
}

.content-table {
  position: relative;
  z-index: 1;
  margin-bottom: 10px;
}

.course-name-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.course-name-cell strong {
  color: #173746;
}

.course-name-cell span {
  color: #6f8792;
  font-size: 12px;
  line-height: 1.6;
}

.danger-text {
  color: #ef5753;
}

::v-deep .el-form-item__label {
  color: #456270;
  font-weight: 600;
}

::v-deep .el-input__inner,
::v-deep .el-textarea__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 249, 255, 0.94));
  color: #355161;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

::v-deep .el-input__inner:focus,
::v-deep .el-textarea__inner:focus {
  border-color: #25ddbf;
  box-shadow: 0 0 0 4px rgba(37, 221, 191, 0.12), 0 12px 22px rgba(39, 182, 194, 0.12);
}

::v-deep .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  box-shadow: 0 16px 30px rgba(20, 175, 183, 0.22);
}

::v-deep .el-button--primary:hover,
::v-deep .el-button--primary:focus {
  filter: saturate(108%);
  box-shadow: 0 18px 34px rgba(20, 175, 183, 0.28);
}

::v-deep .el-button--primary.is-plain {
  border: 1px solid rgba(49, 212, 198, 0.34);
  color: #147f71;
  background: rgba(236, 255, 251, 0.9);
  box-shadow: none;
}

::v-deep .el-table {
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

::v-deep .el-table th {
  background: linear-gradient(180deg, rgba(235, 251, 255, 0.96), rgba(229, 255, 249, 0.92));
  color: #34505f;
}

::v-deep .el-table th:first-child .cell,
::v-deep .el-table td:first-child .cell {
  padding-left: 18px;
}

::v-deep .el-table tr {
  background-color: rgba(255, 255, 255, 0.9);
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: rgba(230, 255, 249, 0.7);
}

::v-deep .el-dialog {
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 252, 255, 0.96));
  box-shadow: 0 30px 60px rgba(25, 112, 133, 0.22);
}

::v-deep .el-dialog__header {
  border-bottom: 1px solid rgba(111, 217, 217, 0.16);
}

::v-deep .el-dialog__title {
  color: #18394a;
  font-weight: 700;
}

.ai-preview-panel {
  display: grid;
  gap: 18px;
}

.ai-preview-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 22px;
  border: 1px solid rgba(105, 214, 220, 0.18);
  background: linear-gradient(180deg, rgba(244, 255, 252, 0.96), rgba(235, 249, 255, 0.92));
}

.ai-preview-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(22, 207, 181, 0.12);
  color: #11897e;
  font-size: 12px;
  font-weight: 700;
}

.ai-preview-head h3 {
  margin: 14px 0 8px;
  color: #173746;
  font-size: 22px;
}

.ai-preview-head p {
  margin: 0;
  color: #6d8794;
  line-height: 1.8;
}

.ai-preview-body {
  min-height: 240px;
  padding: 22px 24px;
  border-radius: 22px;
  border: 1px solid rgba(105, 214, 220, 0.18);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(242, 252, 255, 0.95));
  color: #284959;
  line-height: 1.95;
  white-space: pre-wrap;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

::v-deep .el-pagination .btn-next,
::v-deep .el-pagination .btn-prev,
::v-deep .el-pagination .el-pager li {
  border-radius: 12px;
}

@media (max-width: 900px) {
  .hero-panel {
    flex-direction: column;
  }

  .hero-stats {
    min-width: 0;
    grid-template-columns: repeat(2, 1fr);
  }

  .toolbar-main {
    flex-direction: column;
    align-items: stretch;
  }

  .teacher-cta-strip {
    justify-content: stretch;
  }

  .teacher-cta-button {
    width: 100%;
  }

  .toolbar-actions {
    width: 100%;
    flex-wrap: wrap;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .hero-stats {
    grid-template-columns: 1fr;
  }

}
</style>
