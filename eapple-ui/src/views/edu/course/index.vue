<template>
  <div class="app-container course-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Course Center</span>
        <h1>{{ pageTitle }}</h1>
        <p>{{ pageDescription }}</p>
      </div>
      <div class="hero-stats hero-stats--compact">
        <div class="stat-card">
          <span>{{ isTeacherView ? '我的课程' : '课程总数' }}</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>{{ isTeacherView ? '开设中课程' : (isStudentView ? '已报课程' : '开放课程') }}</span>
          <strong>{{ openCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <div class="toolbar-main">
        <el-form v-show="showSearch" :inline="true" :model="queryParams" size="small" class="query-form">
          <el-form-item label="课程名称">
            <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item v-if="!isTeacherView" label="分类">
            <el-select v-model="queryParams.category" placeholder="全部分类" clearable>
              <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="!canEnrollCourse" label="开课范围">
            <el-select v-model="queryParams.gradeScope" placeholder="全部年级" clearable>
              <el-option v-for="item in gradeOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="!isTeacherView" label="教师">
            <el-input v-model="queryParams.teacherName" placeholder="请输入教师姓名" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="queryParams.campus" placeholder="请输入上课地点" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item label="上课日">
            <el-select v-model="queryParams.weekDay" placeholder="全部星期" clearable>
              <el-option v-for="item in weekOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="canEnrollCourse || canManageCourse" label="课程状态" class="status-select">
            <el-select v-model="queryParams.runtimeStatus" placeholder="全部状态" clearable>
              <el-option label="待开课" value="pending" />
              <el-option label="开设中" value="active" />
              <el-option label="已结课" value="finished" />
              <el-option label="已停开" value="closed" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="canEnrollCourse" label="报名状态" class="status-select">
            <el-select v-model="queryParams.enrolled" placeholder="全部报名" clearable>
              <el-option label="已报名" value="Y" />
              <el-option label="未报名" value="N" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-tooltip content="查询" placement="top">
              <el-button type="primary" size="mini" class="toolbar-icon-btn" icon="el-icon-search" @click="handleQuery" />
            </el-tooltip>
            <el-tooltip content="重置筛选" placement="top">
              <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetQuery" />
            </el-tooltip>
          </el-form-item>
        </el-form>

        <div class="toolbar-actions">
          <el-button
            v-if="isTeacherView"
            type="primary"
            size="mini"
            icon="el-icon-plus"
            class="toolbar-gradient-btn"
            @click="handleAdd"
          >
            发布课程
          </el-button>
          <el-button
            v-if="canManageCourse && !isTeacherView"
            type="primary"
            plain
            size="mini"
            icon="el-icon-plus"
            class="toolbar-gradient-btn"
            @click="handleAdd"
          >
            发布课程
          </el-button>
          <el-button size="mini" icon="el-icon-download" @click="handleExport">导出</el-button>
          <right-toolbar @queryTable="getList" />
        </div>
      </div>
    </section>

    <section v-if="isTeacherView" class="teacher-profile-card">
      <div>
        <h3>教师身份</h3>
      </div>
      <div class="teacher-profile-meta">
        <span>{{ $store.getters.nickName || $store.getters.name || '当前教师' }}</span>
        <span>教学类型：{{ teacherTypeLabel }}</span>
        <span>课程分类：{{ teacherCategoryLabel }}</span>
        <span>共开设 {{ teacherCourseCount }} 门课</span>
      </div>
    </section>

    <section class="reminder-panel">
      <div class="reminder-head">
        <div>
          <h3>上课提醒</h3>
        </div>
        <span>{{ currentDateText }}</span>
      </div>
      <div class="reminder-grid">
        <div class="reminder-card">
          <strong>今日课程</strong>
          <div v-if="todayCourseReminders.length" class="reminder-list">
            <div v-for="item in todayCourseReminders" :key="item.key" class="reminder-item">
              <span>{{ item.courseName }}</span>
              <em>{{ item.time }} · {{ item.campus || '未设置地点' }}</em>
            </div>
          </div>
          <div v-else class="reminder-empty">今日暂无课程安排</div>
        </div>
        <div class="reminder-card">
          <strong>后续课程</strong>
          <div v-if="nextCourseReminders.length" class="reminder-list">
            <div v-for="item in nextCourseReminders" :key="item.key" class="reminder-item">
              <span>{{ item.courseName }}</span>
              <em>{{ item.date }} {{ item.time }} · {{ item.campus || '未设置地点' }}</em>
            </div>
          </div>
          <div v-else class="reminder-empty">暂无后续上课提醒</div>
        </div>
      </div>
    </section>

    <section class="table-section-card">
    <el-table v-loading="loading" :data="courseList" class="content-table">
      <el-table-column label="课程编号" min-width="120" align="center">
        <template slot-scope="scope">
          <span class="course-code-pill">{{ scope.row.courseCode || buildFallbackCourseCode(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程" min-width="180">
        <template slot-scope="scope">
          <div class="course-name-cell">
            <strong>{{ scope.row.courseName }}</strong>
            <span>{{ scope.row.description || '暂无课程简介' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分类" prop="category" min-width="120" align="center" show-overflow-tooltip />
      <el-table-column label="开课范围" min-width="160" align="center" show-overflow-tooltip>
        <template slot-scope="scope">{{ formatGradeScope(scope.row.gradeScope) }}</template>
      </el-table-column>
      <el-table-column label="教师" prop="teacherName" min-width="130" align="center" show-overflow-tooltip />
      <el-table-column label="上课周期" min-width="220" align="center">
        <template slot-scope="scope">
          <div class="schedule-cell">
            <strong>{{ formatCourseRange(scope.row) }}</strong>
            <span>{{ formatWeeklyPlan(scope.row) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="最近一次上课" min-width="170" align="center">
        <template slot-scope="scope">
          <div class="next-class-cell">
            <strong>{{ getNextClassText(scope.row) }}</strong>
            <span>{{ getCourseProgressText(scope.row) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="地点" prop="campus" min-width="140" align="center" show-overflow-tooltip />
      <el-table-column label="人数" width="110" align="center">
        <template slot-scope="scope">{{ getCourseStudentCount(scope.row) }}/{{ scope.row.maxCapacity }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="courseRuntimeStatus(scope.row).tag">
            {{ courseRuntimeStatus(scope.row).text }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="isTeacherView ? '教师操作' : '操作'" min-width="340" fixed="right" align="center">
        <template slot-scope="scope">
          <template v-if="canEnrollCourse">
            <el-button size="mini" type="text" @click="handleEnroll(scope.row)">
              {{ getEnrollActionText(scope.row) }}
            </el-button>
          </template>
          <template v-if="canManageCourse">
            <el-button size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button v-if="isTeacherView" size="mini" type="text" @click="handleRoster(scope.row)">报名名单</el-button>
            <el-button size="mini" type="text" @click="handleToggleStatus(scope.row)">
              {{ scope.row.status === '0' ? '停开课程' : '开设课程' }}
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
    </section>

    <el-dialog :title="title" :visible.sync="open" width="760px">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" /></el-form-item></el-col>
          <el-col v-if="!isTeacherView" :span="12">
            <el-form-item label="课程分类" prop="category">
              <el-select v-model="form.category" class="full-width" placeholder="请选择课程分类">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-else :span="12">
            <el-form-item label="课程分类" prop="category">
              <el-input :value="defaultTeacherCategory()" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开课范围" prop="gradeScopeList">
              <el-select v-model="form.gradeScopeList" class="full-width" multiple collapse-tags placeholder="请选择适用年级">
                <el-option v-for="item in gradeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="上课地点" prop="campus"><el-input v-model="form.campus" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="每周几次" prop="weeklyFrequency">
              <el-select v-model="form.weeklyFrequency" placeholder="请选择每周次数" @change="syncScheduleSlots">
                <el-option v-for="item in [1, 2, 3, 4, 5]" :key="item" :label="`每周 ${item} 次`" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="最大人数" prop="maxCapacity"><el-input-number v-model="form.maxCapacity" :min="1" /></el-form-item></el-col>
          <el-col :span="24">
            <el-form-item label="上课安排" prop="weekDay">
              <div class="schedule-editor">
                <div v-for="(slot, index) in form.scheduleSlots" :key="index" class="schedule-slot-row">
                  <el-select v-model="slot.weekDay" placeholder="选择星期">
                    <el-option v-for="item in weekOptions" :key="item" :label="item" :value="item" />
                  </el-select>
                  <el-time-picker v-model="slot.startTime" value-format="HH:mm" format="HH:mm" placeholder="开始时间" />
                  <span class="schedule-separator">至</span>
                  <el-time-picker v-model="slot.endTime" value-format="HH:mm" format="HH:mm" placeholder="结束时间" />
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="课程简介"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="rosterTitle" :visible.sync="rosterOpen" width="760px">
      <el-table :data="rosterList" class="content-table">
        <el-table-column label="学生" prop="studentName" min-width="120" />
        <el-table-column label="家长" prop="parentName" min-width="120" />
        <el-table-column label="报名来源" min-width="110">
          <template slot-scope="scope">{{ scope.row.signupSource === 'parent' ? '家长报名' : '学生报名' }}</template>
        </el-table-column>
        <el-table-column label="最近学习记录" min-width="220" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.learningRecord || '暂未填写' }}</template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="rosterOpen = false">关闭</el-button>
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
  exportCourse,
  generateCourseNotice,
  generateTeachingSuggestion,
  listCourse,
  updateCourse
} from '@/api/edu/course'
import { listMyChildren } from '@/api/edu/student'
import { getCurrentAiModel, listAiModels } from '@/api/edu/ai'
import { listEnrollment } from '@/api/edu/enrollment'

export default {
  name: 'EduCourse',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      courseList: [],
      statsCourseList: [],
      childrenList: [],
      aiModelOptions: [],
      currentAiModel: '',
      weekOptions: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      gradeOptions: ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级'],
      categoryOptions: [
        { value: '理科拓展', label: '理科拓展' },
        { value: '文科阅读', label: '文科阅读' },
        { value: '美育实践', label: '美育实践' },
        { value: '体育健康', label: '体育健康' },
        { value: '计算机编程', label: '计算机编程' },
        { value: '综合素养', label: '综合素养' }
      ],
      open: false,
      aiGenerating: false,
      aiGeneratingCourseId: undefined,
      aiGeneratingType: '',
      aiPreviewOpen: false,
      aiPreviewTitle: '',
      aiPreviewType: '',
      aiPreviewCourseName: '',
      aiPreviewContent: '',
      rosterOpen: false,
      rosterTitle: '报名学生名单',
      rosterList: [],
      title: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: undefined,
        category: undefined,
        gradeScope: undefined,
        teacherName: undefined,
        campus: undefined,
        weekDay: undefined,
        runtimeStatus: undefined,
        enrolled: undefined
      },
      form: {},
      rules: {
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        category: [{ required: true, message: '请输入课程分类', trigger: 'blur' }],
        gradeScopeList: [{ type: 'array', required: true, message: '请选择开课范围', trigger: 'change' }],
        campus: [{ required: true, message: '请输入上课地点', trigger: 'blur' }],
        maxCapacity: [{ required: true, message: '请设置最大人数', trigger: 'change' }],
        weeklyFrequency: [{ required: true, message: '请选择每周上课次数', trigger: 'change' }],
        weekDay: [{ required: true, message: '请完善上课安排', trigger: 'change' }],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
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
    isStudentView() {
      return this.roleKeys.includes('edu_student')
    },
    canManageCourse() {
      return this.isTeacherView || this.isAdminView
    },
    canEnrollCourse() {
      return this.roleKeys.includes('edu_student') || this.roleKeys.includes('edu_parent')
    },
    openCount() {
      if (this.isStudentView) {
        return this.statsCourseList.filter(item => item.enrolled === 'Y').length
      }
      return this.statsCourseList.filter(item => this.courseRuntimeStatus(item).canEnroll).length
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
        return '教师可发布课后课程，集中查看自己发布的课程，并按教学安排选择开设或停开课程。'
      }
      if (this.isAdminView) {
        return '管理员可查看全部课程，统一掌握课程开设情况、教师安排和课程状态。'
      }
      return '集中展示课后课程、兴趣班和课程安排，支持课程报名、取消报名以及查看课程信息。'
    },
    teacherTypeLabel() {
      return this.formatTeacherType(this.$store.getters.teacherType)
    },
    teacherCategoryLabel() {
      return this.defaultTeacherCategory()
    },
    teacherCourseCount() {
      return this.statsCourseList.length
    },
    currentAiModelLabel() {
      const current = this.aiModelOptions.find(item => item.modelName === this.currentAiModel)
      return current ? current.displayName : this.currentAiModel
    },
    currentDateText() {
      return this.formatDate(new Date())
    },
    todayCourseReminders() {
      const todayKey = this.formatDate(new Date()).slice(0, 10)
      return this.reminderCourseList.flatMap(course => this.enumerateClassSessions(course)
        .filter(session => this.formatDate(session.start).slice(0, 10) === todayKey)
        .map(session => ({
          key: `${course.courseId}-${session.start.getTime()}`,
          courseName: course.courseName,
          campus: course.campus,
          time: `${session.slot.startTime}-${session.slot.endTime}`,
          start: session.start
        })))
        .sort((a, b) => a.start - b.start)
        .slice(0, 5)
    },
    nextCourseReminders() {
      const tomorrow = new Date()
      tomorrow.setHours(0, 0, 0, 0)
      tomorrow.setDate(tomorrow.getDate() + 1)
      return this.reminderCourseList.map(course => {
        const session = this.enumerateClassSessions(course).find(item => item.start >= tomorrow)
        if (!session) return null
        return {
          key: `${course.courseId}-${session.start.getTime()}`,
          courseName: course.courseName,
          campus: course.campus,
          date: this.formatDate(session.start),
          time: `${session.slot.startTime}-${session.slot.endTime}`,
          start: session.start
        }
      }).filter(Boolean).sort((a, b) => a.start - b.start).slice(0, 5)
    },
    reminderCourseList() {
      const availableCourses = this.statsCourseList.filter(course => this.courseRuntimeStatus(course).code !== 'closed')
      if (this.canEnrollCourse) {
        return availableCourses.filter(course => course.enrolled === 'Y')
      }
      return availableCourses
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
      const statsParams = { ...this.queryParams, pageNum: 1, pageSize: 1000 }
      const listParams = this.canEnrollCourse ? statsParams : this.queryParams
      Promise.all([listCourse(listParams), listCourse(statsParams)]).then(([res, statsRes]) => {
        const rows = this.uniqueCourses(res.rows || [])
        this.courseList = this.canEnrollCourse ? this.paginateRows(rows, this.queryParams.pageNum, this.queryParams.pageSize) : rows
        this.total = this.canEnrollCourse ? rows.length : (res.total || rows.length)
        this.statsCourseList = this.uniqueCourses(statsRes.rows || [])
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
        gradeScope: undefined,
        teacherName: undefined,
        campus: undefined,
        weekDay: undefined,
        runtimeStatus: undefined,
        enrolled: undefined
      }
      this.getList()
    },
    uniqueCourses(rows) {
      const seen = new Set()
      return (rows || []).filter(row => {
        const key = row.courseCode || row.courseId
        if (seen.has(key)) {
          return false
        }
        seen.add(key)
        return true
      })
    },
    paginateRows(rows, pageNum, pageSize) {
      const size = Number(pageSize || 10)
      const start = (Number(pageNum || 1) - 1) * size
      return rows.slice(start, start + size)
    },
    handleExport() {
      exportCourse(this.queryParams)
    },
    reset() {
      this.form = {
        courseId: undefined,
        maxCapacity: 30,
        status: '0',
        currentCapacity: 0,
        category: this.defaultTeacherCategory(),
        gradeScope: '一年级,二年级,三年级,四年级,五年级,六年级,七年级,八年级,九年级',
        gradeScopeList: ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级'],
        weeklyFrequency: 1,
        weekDay: '周一 16:00-17:30',
        scheduleSlots: [{ weekDay: '周一', startTime: '16:00', endTime: '17:30' }],
        startTime: '16:00',
        endTime: '17:30'
      }
    },
    handleAdd() {
      this.reset()
      this.title = '发布课程'
      this.open = true
    },
    handleUpdate(row) {
      const scheduleSlots = this.parseCourseSlots(row)
      this.form = {
        ...row,
        gradeScopeList: this.parseGradeScope(row.gradeScope),
        weeklyFrequency: scheduleSlots.length || 1,
        scheduleSlots
      }
      this.title = '编辑课程'
      this.open = true
    },
    submitForm() {
      if (this.isTeacherView) {
        this.form.category = this.defaultTeacherCategory()
      }
      this.prepareCourseSchedule()
      this.prepareGradeScope()
      if (!this.hasValidSchedule()) {
        this.$modal.msgWarning('请完善每次课的星期与起止时间')
        return
      }
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
      const actionText = nextStatus === '0' ? '开设' : '停开'
      this.$modal.confirm(`确认${actionText}课程“${row.courseName}”吗？`).then(() => {
        return updateCourse({ ...row, status: nextStatus })
      }).then(() => {
        this.$modal.msgSuccess(`课程已${actionText}`)
        this.getList()
      }).catch(() => {})
    },
    hasValidSchedule() {
      const slots = this.form.scheduleSlots || []
      return slots.length > 0 && slots.every(item => item.weekDay && item.startTime && item.endTime && item.startTime < item.endTime)
    },
    handleDelete(row) {
      if (row.status !== '1') {
        this.$modal.msgWarning('请先停开课程，再执行删除操作')
        return
      }
      this.$modal.confirm(`确认删除课程“${row.courseName}”吗？`).then(() => delCourse(row.courseId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleRoster(row) {
      this.rosterTitle = `《${row.courseName}》报名学生名单`
      this.rosterOpen = true
      listEnrollment({ courseId: row.courseId, pageNum: 1, pageSize: 200 }).then(res => {
        this.rosterList = res.rows || []
      })
    },
    getCourseStudentCount(row) {
      return row.enrollCount !== undefined && row.enrollCount !== null ? row.enrollCount : (row.currentCapacity || 0)
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
      const runtime = this.courseRuntimeStatus(row)
      if (row.enrolled === 'Y' && runtime.code === 'finished') {
        this.$router.push({
          path: '/edu/enrollment',
          query: { courseName: row.courseName }
        })
        return
      }
      if (row.enrolled !== 'Y' && !runtime.canEnroll) {
        this.$modal.msgError(`当前课程${runtime.text}，暂不可报名`)
        return
      }
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
    getEnrollActionText(row) {
      const runtime = this.courseRuntimeStatus(row)
      if (row.enrolled === 'Y' && runtime.code === 'finished') {
        return '查看记录'
      }
      return row.enrolled === 'Y' ? '取消报名' : '报名'
    },
    handleNotice(row) {
      this.startAiGenerating(row, 'notice', 'AI 生成通知', '课程通知')
      generateCourseNotice(row.courseId).then(res => {
        const content = res.data || row.aiNotice || '当前暂无生成内容，请稍后重试。'
        this.openAiPreview('AI 生成通知', '课程通知', row, content)
        this.getList()
      }).catch(() => {
        this.handleAiFailure(row, 'AI 生成通知', '课程通知', row.aiNotice)
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
        this.handleAiFailure(row, 'AI 教学建议', '教学建议', row.aiSuggestion)
      }).finally(() => {
        this.stopAiGenerating()
      })
    },
    handleAiFailure(row, title, type, fallbackContent) {
      if (fallbackContent) {
        this.$modal.msgWarning(`${type}生成较慢，已回退展示上一次成功内容`)
        this.openAiPreview(title, type, row, fallbackContent)
        return
      }
      this.aiPreviewOpen = false
      this.$modal.msgWarning(`${type}暂时未生成成功，请稍后重试`)
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
    },
    syncScheduleSlots() {
      const count = Number(this.form.weeklyFrequency || 1)
      const slots = Array.isArray(this.form.scheduleSlots) ? this.form.scheduleSlots.slice(0, count) : []
      while (slots.length < count) {
        const weekDay = this.weekOptions[slots.length % this.weekOptions.length]
        slots.push({ weekDay, startTime: this.form.startTime || '16:00', endTime: this.form.endTime || '17:30' })
      }
      this.$set(this.form, 'scheduleSlots', slots)
      this.prepareCourseSchedule()
    },
    prepareCourseSchedule() {
      const slots = (this.form.scheduleSlots || []).filter(item => item.weekDay && item.startTime && item.endTime)
      if (!slots.length) {
        return
      }
      this.form.weekDay = slots.map(item => `${item.weekDay} ${item.startTime}-${item.endTime}`).join('；')
      this.form.startTime = slots[0].startTime
      this.form.endTime = slots[0].endTime
    },
    prepareGradeScope() {
      const selected = Array.isArray(this.form.gradeScopeList) ? this.form.gradeScopeList : []
      this.form.gradeScope = selected.join(',')
    },
    parseGradeScope(scope) {
      const text = String(scope || '').replace(/，/g, ',')
      const list = text.split(',').map(item => item.trim()).filter(Boolean)
      return list.length ? list : this.gradeOptions.slice()
    },
    formatGradeScope(scope) {
      const list = this.parseGradeScope(scope)
      const indexes = list.map(item => this.gradeOptions.indexOf(item)).filter(index => index >= 0).sort((a, b) => a - b)
      if (indexes.length && indexes.every((index, order) => order === 0 || index === indexes[order - 1] + 1)) {
        const start = this.gradeOptions[indexes[0]]
        const end = this.gradeOptions[indexes[indexes.length - 1]]
        return start === end ? start : `${start.replace('年级', '')}至${end}`
      }
      return list.join('、') || '未设置'
    },
    formatTeacherType(value) {
      const map = {
        science: '理科',
        humanities: '文科',
        art: '美育',
        sports: '体育',
        computer: '计算机',
        general: '综合实践'
      }
      return map[String(value || '').trim()] || '未设置'
    },
    defaultTeacherCategory() {
      const map = {
        science: '理科拓展',
        humanities: '文科阅读',
        art: '美育实践',
        sports: '体育健康',
        computer: '计算机编程',
        general: '综合素养'
      }
      const teacherType = String(this.$store.getters.teacherType || 'general').trim()
      return map[teacherType] || '综合素养'
    },
    buildFallbackCourseCode(row) {
      return row && row.courseId ? `QH-C${String(row.courseId).padStart(4, '0')}` : '未编号'
    },
    parseCourseSlots(course) {
      const text = String(course.weekDay || '')
      const slots = []
      const pattern = /(周[一二三四五六日天])\s*(\d{1,2}:\d{2})?\s*-?\s*(\d{1,2}:\d{2})?/g
      let match = pattern.exec(text)
      while (match) {
        const weekDay = match[1] === '周天' ? '周日' : match[1]
        slots.push({
          weekDay,
          startTime: match[2] || course.startTime || '16:00',
          endTime: match[3] || course.endTime || '17:30'
        })
        match = pattern.exec(text)
      }
      if (slots.length) {
        return slots
      }
      const fallbackDays = this.weekOptions.filter(item => text.includes(item))
      if (fallbackDays.length) {
        return fallbackDays.map(day => ({ weekDay: day, startTime: course.startTime || '16:00', endTime: course.endTime || '17:30' }))
      }
      return [{ weekDay: '周一', startTime: course.startTime || '16:00', endTime: course.endTime || '17:30' }]
    },
    parseDate(value) {
      if (!value) return null
      if (value instanceof Date) {
        return new Date(value.getFullYear(), value.getMonth(), value.getDate())
      }
      const match = String(value).match(/^(\d{4})-(\d{1,2})-(\d{1,2})/)
      if (match) {
        return new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]))
      }
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) return null
      return new Date(date.getFullYear(), date.getMonth(), date.getDate())
    },
    parseDateTime(dateValue, timeValue) {
      const date = this.parseDate(dateValue)
      if (!date) return null
      const time = String(timeValue || '00:00').split(':')
      date.setHours(Number(time[0] || 0), Number(time[1] || 0), 0, 0)
      return date
    },
    getWeekDayNumber(weekDay) {
      const map = { 周日: 0, 周一: 1, 周二: 2, 周三: 3, 周四: 4, 周五: 5, 周六: 6 }
      return map[weekDay]
    },
    getWeekDays(course) {
      return this.parseCourseSlots(course)
        .map(slot => this.getWeekDayNumber(slot.weekDay))
        .filter(item => item !== undefined)
    },
    enumerateClassSessions(course) {
      const start = this.parseDate(course.startDate)
      const end = this.parseDate(course.endDate)
      const slots = this.parseCourseSlots(course)
      if (!start || !end || !slots.length) return []
      const sessions = []
      const cursor = new Date(start)
      while (cursor <= end) {
        slots.forEach(slot => {
          if (this.getWeekDayNumber(slot.weekDay) === cursor.getDay()) {
            sessions.push({
              start: this.parseDateTime(cursor, slot.startTime),
              end: this.parseDateTime(cursor, slot.endTime),
              slot
            })
          }
        })
        cursor.setDate(cursor.getDate() + 1)
      }
      return sessions.sort((a, b) => a.start - b.start)
    },
    enumerateClassDates(course) {
      return this.enumerateClassSessions(course).map(item => item.start)
    },
    getNextClassSession(course) {
      const now = new Date()
      return this.enumerateClassSessions(course).find(session => session.end >= now)
    },
    formatDate(date) {
      if (!date) return ''
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${year}-${month}-${day} ${weekMap[date.getDay()]}`
    },
    formatCourseRange(course) {
      const start = this.parseDate(course.startDate)
      const end = this.parseDate(course.endDate)
      if (!start || !end) return '未设置周期'
      return `${this.formatDate(start).slice(0, 10)} 至 ${this.formatDate(end).slice(0, 10)}`
    },
    formatWeeklyPlan(course) {
      const slots = this.parseCourseSlots(course)
      const frequency = slots.length ? `每周 ${slots.length} 次` : '未设置频次'
      const detail = slots.map(item => `${item.weekDay} ${item.startTime}-${item.endTime}`).join('；')
      return `${detail || '未设置星期'} · ${frequency}`
    },
    getNextClassText(course) {
      const runtime = this.courseRuntimeStatus(course)
      if (runtime.code === 'closed') return '已停开'
      if (runtime.code === 'finished') return '全部课程已结束'
      const next = this.getNextClassSession(course)
      return next ? `${this.formatDate(next.start)} ${next.slot.startTime}` : '暂无后续课次'
    },
    getCourseProgressText(course) {
      const sessions = this.enumerateClassSessions(course)
      if (!sessions.length) return '未设置课次'
      const now = new Date()
      const finished = sessions.filter(session => session.end < now).length
      return `第 ${Math.min(finished + 1, sessions.length)} / ${sessions.length} 次`
    },
    getReminderInfo(course) {
      const runtime = this.courseRuntimeStatus(course)
      if (runtime.code === 'closed' || runtime.code === 'finished') {
        return { text: runtime.text, tag: 'info' }
      }
      if (runtime.code === 'inClass') {
        return { text: '正在上课', tag: 'success' }
      }
      if (runtime.code === 'today') {
        return { text: '今日上课', tag: 'primary' }
      }
      const next = this.getNextClassSession(course)
      if (!next) {
        return { text: '暂无提醒', tag: 'info' }
      }
      const hours = (next.start.getTime() - Date.now()) / 3600000
      if (hours > 0 && hours <= 24) {
        return { text: '24小时内', tag: 'warning' }
      }
      return { text: `下次 ${this.formatDate(next.start).slice(5)}`, tag: 'info' }
    },
    courseRuntimeStatus(course) {
      if (course.status !== '0') {
        return { code: 'closed', text: this.isTeacherView ? '已停开' : '已关闭', tag: 'info', canEnroll: false }
      }
      const now = new Date()
      const sessions = this.enumerateClassSessions(course)
      if (!sessions.length) {
        return { code: 'active', text: '开设中', tag: 'success', canEnroll: true }
      }
      const first = sessions[0]
      const last = sessions[sessions.length - 1]
      if (now < first.start) {
        return { code: 'pending', text: '待开课', tag: 'warning', canEnroll: true }
      }
      if (now > last.end) {
        return { code: 'finished', text: '已结课', tag: 'info', canEnroll: false }
      }
      const current = sessions.find(session => session.start <= now && session.end >= now)
      if (current) {
        return { code: 'inClass', text: '上课中', tag: 'success', canEnroll: true }
      }
      const todayKey = this.formatDate(now).slice(0, 10)
      const todaySession = sessions.find(session => this.formatDate(session.start).slice(0, 10) === todayKey)
      if (todaySession) {
        return todaySession.start > now
          ? { code: 'today', text: '今日待上', tag: 'primary', canEnroll: true }
          : { code: 'todayDone', text: '今日已过', tag: 'warning', canEnroll: true }
      }
      return { code: 'active', text: '开设中', tag: 'success', canEnroll: true }
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
  padding-top: 0;
}

.teacher-profile-card {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(157, 232, 233, 0.36);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 34px rgba(41, 130, 141, 0.08);
}

.teacher-profile-card h3 {
  margin: 0 0 8px;
  color: #173746;
  font-size: 24px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
  letter-spacing: 0.01em;
}

.teacher-profile-card p {
  margin: 0;
  color: #6f8792;
  font-size: 13px;
}

.teacher-profile-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.teacher-profile-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 14px;
  border: 1px solid rgba(103, 186, 178, 0.22);
  border-radius: 999px;
  background: #f7fbfa;
  color: #315c62;
  font-size: 13px;
  font-weight: 700;
}

.reminder-panel {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
  padding: 18px;
  border: 1px solid rgba(157, 232, 233, 0.36);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 34px rgba(41, 130, 141, 0.08);
}

.reminder-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.reminder-head h3 {
  margin: 0 0 8px;
  color: #173746;
  font-size: 24px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
  letter-spacing: 0.01em;
}

.reminder-head span {
  margin: 0;
  color: #6f8792;
  font-size: 13px;
  line-height: 1.7;
}

.reminder-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.reminder-card {
  min-height: 140px;
  padding: 18px;
  border-radius: 22px;
  border: 1px solid rgba(157, 232, 233, 0.36);
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 16px 30px rgba(41, 130, 141, 0.07);
  text-align: center;
}

.reminder-card > strong {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 26px;
  margin: 0 auto 14px;
  padding: 0 14px;
  border: 1px solid rgba(102, 188, 190, 0.22);
  border-radius: 999px;
  background: #eef9f6;
  color: #173746;
  text-align: center;
}

.reminder-list {
  display: grid;
  gap: 10px;
}

.reminder-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid rgba(202, 224, 226, 0.72);
  border-radius: 14px;
  background: #ffffff;
}

.reminder-item span {
  color: #23495a;
  font-weight: 700;
}

.reminder-item em {
  color: #6f8792;
  font-style: normal;
  font-size: 12px;
}

.reminder-empty {
  padding: 24px 12px;
  border-radius: 16px;
  background: #f8fbfb;
  color: #8aa0a8;
  text-align: center;
}

::v-deep .query-form .el-form-item {
  display: inline-flex;
  align-items: center;
  width: auto !important;
  min-height: 38px;
  margin: 0 !important;
}

::v-deep .query-form .el-form-item:last-child {
  margin-right: 0;
}

::v-deep .status-select .el-input__inner {
  width: 100% !important;
}

::v-deep .query-form .el-input,
::v-deep .query-form .el-select {
  width: 190px !important;
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

.course-code-pill {
  display: inline-flex;
  justify-content: center;
  min-width: 86px;
  padding: 3px 9px;
  border-radius: 999px;
  background: #eef8f7;
  color: #2b7772;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.course-name-cell span {
  color: #6f8792;
  font-size: 12px;
  line-height: 1.6;
}

.schedule-cell,
.next-class-cell {
  display: grid;
  gap: 6px;
}

.schedule-cell strong,
.next-class-cell strong {
  color: #173746;
  font-size: 14px;
}

.schedule-cell span,
.next-class-cell span {
  color: #6f8792;
  font-size: 12px;
  line-height: 1.5;
}

.schedule-editor {
  display: grid;
  gap: 10px;
  width: 100%;
}

.schedule-slot-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.schedule-slot-row .el-select {
  width: 120px;
}

.schedule-slot-row .el-date-editor.el-input {
  width: 140px;
}

.schedule-separator {
  color: #6f8792;
  font-weight: 600;
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
  background: #ffffff;
  color: #355161;
  box-shadow: none;
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
  background: var(--table-header-bg, #d6dbd4) !important;
  color: var(--table-header-text, #3f4a42) !important;
}

::v-deep .el-table th:first-child .cell,
::v-deep .el-table td:first-child .cell {
  padding-left: 18px;
}

::v-deep .el-table tr {
  background-color: rgba(255, 255, 255, 0.9);
}

::v-deep .el-table .el-table__body tr > td,
::v-deep .el-table .el-table__fixed-body-wrapper tr > td {
  background: #ffffff !important;
}

::v-deep .el-table .el-table__body tr.hover-row > td,
::v-deep .el-table .el-table__fixed-body-wrapper tr.hover-row > td,
::v-deep .el-table .el-table__body tr.current-row > td,
::v-deep .el-table .el-table__fixed-body-wrapper tr.current-row > td {
  background: #ffffff !important;
}

::v-deep .el-table .el-table__body tr.hover-row:hover > td,
::v-deep .el-table .el-table__fixed-body-wrapper tr.hover-row:hover > td,
::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: #f2f4ef !important;
}

::v-deep .el-table--enable-row-hover .el-table__fixed-body-wrapper tr:hover > td {
  background: #f2f4ef !important;
}

::v-deep .el-table .el-table__body tr.hover-row > td,
::v-deep .el-table .el-table__fixed tr.hover-row > td,
::v-deep .el-table .el-table__fixed-right tr.hover-row > td,
::v-deep .el-table .el-table__fixed-body-wrapper tr.hover-row > td {
  background: #f2f4ef !important;
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

  .reminder-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-stats {
    grid-template-columns: 1fr;
  }

}
</style>
