<template>
  <div class="app-container enrollment-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">{{ pageBadge }}</span>
        <h1>{{ pageTitle }}</h1>
        <p>{{ pageDescription }}</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>{{ isCourseRecordView ? '课程总数' : '报名总数' }}</span>
          <strong>{{ displayTotal }}</strong>
        </div>
        <div class="stat-card">
          <span>{{ secondStatLabel }}</span>
          <strong>{{ displayFinishedCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <div class="toolbar-main">
        <el-form :inline="true" :model="queryParams" size="small" v-show="showSearch" class="query-form">
          <el-form-item :label="'\u5b66\u751f'">
            <el-input v-model="queryParams.studentName" :placeholder="'\u8bf7\u8f93\u5165\u5b66\u751f\u59d3\u540d'" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item :label="'\u8bfe\u7a0b'">
            <el-input v-model="queryParams.courseName" :placeholder="'\u8bf7\u8f93\u5165\u8bfe\u7a0b\u540d\u79f0'" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item :label="'\u6559\u5e08'">
            <el-input v-model="queryParams.teacherName" :placeholder="'\u8bf7\u8f93\u5165\u6559\u5e08\u59d3\u540d'" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item :label="'\u72b6\u6001'" class="status-select">
            <el-select v-model="queryParams.runtimeStatus" clearable :placeholder="'\u8bf7\u9009\u62e9\u72b6\u6001'">
              <el-option label="开课中" value="active" />
              <el-option label="已结课" value="finished" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-tooltip content="查询" placement="top">
              <el-button type="primary" size="mini" class="toolbar-icon-btn" icon="el-icon-search" @click="getList" />
            </el-tooltip>
            <el-tooltip content="重置筛选" placement="top">
              <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetQuery" />
            </el-tooltip>
          </el-form-item>
        </el-form>

        <div class="toolbar-actions">
          <el-button size="mini" icon="el-icon-download" @click="handleExport">导出</el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="refreshList" />
        </div>
      </div>
    </section>

    <template v-if="isCourseRecordView">
      <section class="record-module">
        <div class="module-title">
          <h3>课中记录</h3>
          <span>按课程和课次维护本节课反馈，反馈会同步给本节课所有报名学生。</span>
        </div>
        <div v-if="activeTeacherCourseSections.length" class="teacher-course-grid">
          <article v-for="course in activePagedTeacherCourseSections" :key="course.courseId" class="teacher-course-card">
            <header>
              <div>
                <strong>{{ course.courseName }}</strong>
                <span>{{ formatCourseRange(course) }} · {{ formatGradeScope(course.gradeScope) }}</span>
              </div>
              <el-tag size="small" :type="recordRuntimeStatus(course).tag">{{ recordRuntimeStatus(course).text }}</el-tag>
            </header>
            <div class="teacher-course-actions">
              <span>已上课 {{ getPastSessions(course).length }} 次，未反馈 {{ getTeacherMissingFeedbackCount(course) }} 次</span>
              <div class="teacher-action-buttons">
                <el-button size="mini" class="toolbar-plain-btn" @click="handleTeacherRoster(course)">学生名单</el-button>
                <el-button v-if="isAdminView" size="mini" class="toolbar-plain-btn" @click="handleCourseRecordView(course, false)">查看记录</el-button>
                <el-button v-if="isTeacherView" size="mini" class="toolbar-plain-btn" @click="handleTeacherCourse(course)">填写课次反馈</el-button>
              </div>
            </div>
          </article>
        </div>
        <div v-else class="record-empty">暂无上课记录。</div>
        <pagination v-show="activeTeacherCourseSections.length > 0" :total="activeTeacherCourseSections.length" :page.sync="activeTeacherPageNum" :limit.sync="activeTeacherPageSize" @pagination="handleActiveTeacherPagination" />
      </section>

      <section class="record-module">
        <div class="module-title">
          <h3>结课总结</h3>
          <span>课程全部课次结束后，在这里统一填写课程结课反馈。</span>
        </div>
        <div v-if="finishedTeacherCourseSections.length" class="teacher-course-grid">
          <article v-for="course in finishedPagedTeacherCourseSections" :key="course.courseId" class="teacher-course-card">
            <header>
              <div>
                <strong>{{ course.courseName }}</strong>
                <span>{{ formatCourseRange(course) }} · {{ formatGradeScope(course.gradeScope) }}</span>
              </div>
              <el-tag size="small" type="success">已结课</el-tag>
            </header>
            <div class="teacher-course-actions">
              <span>已上课 {{ getPastSessions(course).length }} 次，结课反馈{{ getTeacherFinalFeedback(course) ? '已填写' : '未填写' }}</span>
              <div class="teacher-action-buttons">
                <el-button size="mini" class="toolbar-plain-btn" @click="handleTeacherRoster(course)">学生名单</el-button>
                <el-button v-if="isAdminView" size="mini" class="toolbar-plain-btn" @click="handleCourseRecordView(course, true)">查看记录</el-button>
                <el-button v-if="isTeacherView" size="mini" class="toolbar-plain-btn" @click="handleTeacherFinal(course)">
                  {{ getTeacherFinalFeedback(course) ? '修改结课反馈' : '填写结课反馈' }}
                </el-button>
              </div>
            </div>
          </article>
        </div>
        <div v-else class="record-empty">暂无已结课课程。</div>
        <pagination v-show="finishedTeacherCourseSections.length > 0" :total="finishedTeacherCourseSections.length" :page.sync="finishedTeacherPageNum" :limit.sync="finishedTeacherPageSize" @pagination="handleFinishedTeacherPagination" />
      </section>
    </template>

    <template v-else>
    <section class="table-section-card">
      <div class="module-title table-module-title">
        <h3>课中记录</h3>
      </div>
    <el-table v-loading="loading" :data="activePagedEnrollmentList" class="content-table">
      <el-table-column :label="'\u8bfe\u7a0b'" prop="courseName" min-width="150" />
      <el-table-column :label="'\u5b66\u751f'" prop="studentName" width="120" />
      <el-table-column :label="'\u5bb6\u957f'" prop="parentName" width="120" />
      <el-table-column :label="'\u6559\u5e08'" prop="teacherName" width="120" />
      <el-table-column :label="'\u72b6\u6001'" width="100">
        <template slot-scope="scope">
          <el-tag :type="recordRuntimeStatus(scope.row).tag">{{ recordRuntimeStatus(scope.row).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="课次记录" min-width="170">
        <template slot-scope="scope">
          <div class="record-progress">
            <strong>{{ getFilledSummary(scope.row) }}</strong>
            <span>{{ getMissingSummary(scope.row) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="最近学习记录" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">{{ getLatestSessionText(scope.row, 'student') }}</template>
      </el-table-column>
      <el-table-column label="最近教师反馈" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">{{ getLatestSessionText(scope.row, 'teacher') }}</template>
      </el-table-column>
      <el-table-column v-if="showActionColumn" :label="'\u64cd\u4f5c'" width="250">
        <template slot-scope="scope">
          <div class="table-actions">
            <el-button v-if="canSubmitEnrollment" size="mini" type="text" @click="handleUpdate(scope.row)">{{ actionButtonText }}</el-button>
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">查看详情</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="activeEnrollmentList.length > 0" :total="activeEnrollmentList.length" :page.sync="activePageNum" :limit.sync="activePageSize" @pagination="handleActivePagination" />
    </section>

    <section class="table-section-card">
      <div class="module-title table-module-title">
        <h3>结课总结</h3>
      </div>
    <el-table v-loading="loading" :data="finishedPagedEnrollmentList" class="content-table">
      <el-table-column :label="'\u8bfe\u7a0b'" prop="courseName" min-width="150" />
      <el-table-column :label="'\u5b66\u751f'" prop="studentName" width="120" />
      <el-table-column :label="'\u5bb6\u957f'" prop="parentName" width="120" />
      <el-table-column :label="'\u6559\u5e08'" prop="teacherName" width="120" />
      <el-table-column :label="'\u72b6\u6001'" width="100">
        <template slot-scope="scope">
          <el-tag type="success">已结课</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结课记录" min-width="170">
        <template slot-scope="scope">
          <div class="record-progress">
            <strong>{{ getFinalRecordSummary(scope.row) }}</strong>
            <span>{{ getFinalRecordHint(scope.row) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="学生课程总结" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">{{ getCourseSummary(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="教师结课反馈" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">{{ getFinalFeedback(scope.row) }}</template>
      </el-table-column>
      <el-table-column v-if="showActionColumn" :label="'\u64cd\u4f5c'" width="250">
        <template slot-scope="scope">
          <div class="table-actions">
            <el-button v-if="canSubmitEnrollment && isStudentView" size="mini" type="text" @click="handleCourseSummary(scope.row)">填写课程总结</el-button>
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">查看详情</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="finishedEnrollmentList.length > 0" :total="finishedEnrollmentList.length" :page.sync="finishedPageNum" :limit.sync="finishedPageSize" @pagination="handleFinishedPagination" />
    </section>
    </template>

    <el-dialog :visible.sync="open" width="760px">
      <template slot="title">
        <div class="record-dialog-title">
          <strong>{{ dialogTitle }}{{ form.courseName ? `：${form.courseName}` : '' }}</strong>
          <span v-if="!isCourseSummaryMode && !isStudentView">{{ missingSessionCount }} 次未提交</span>
        </div>
      </template>
      <el-form :model="form" label-width="96px" class="record-form">
        <template v-if="!isCourseSummaryMode">
        <el-form-item label="未填写课次" class="missing-session-item">
          <div class="missing-tip">
            <span>{{ missingSessionText }}</span>
          </div>
        </el-form-item>
        <el-form-item label="上课时间">
          <el-select v-model="selectedSessionKey" class="session-select" placeholder="请选择已上课次">
            <el-option
              v-for="item in availableSessions"
              :key="item.key"
              :label="`${item.label} ${item.time}`"
              :value="item.key"
            >
              <span>{{ item.label }} {{ item.time }}</span>
              <span class="session-option-status">{{ getSessionFillState(item) }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        </template>
        <el-form-item v-if="isStudentView">
          <template slot="label">
            <span class="single-line-label">{{ isCourseSummaryMode ? '课程小结' : '学生学习记录' }}</span>
          </template>
          <el-input
            v-model="sessionDraft.student"
            :readonly="!isStudentView"
            type="textarea"
            :rows="4"
            :placeholder="isCourseSummaryMode ? '填写你对整门课程的收获、困难和下一步计划' : '学生填写本节课收获、练习完成情况和疑问'"
          />
        </el-form-item>
        <el-form-item v-if="isTeacherView">
          <template slot="label">
            <span class="single-line-label">教师本次反馈</span>
          </template>
          <el-input
            v-model="sessionDraft.teacher"
            :readonly="!isTeacherView"
            type="textarea"
            :rows="4"
            placeholder="教师填写本节课课堂表现、任务完成和改进建议"
          />
        </el-form-item>
        <el-form-item v-if="isCourseFinished(form) && isTeacherView" label="结课反馈">
          <el-input
            v-model="finalFeedbackDraft"
            :readonly="!isTeacherView"
            type="textarea"
            :rows="3"
            placeholder="课程结课后，教师填写整体表现、成长变化和后续建议"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button v-if="canSubmitEnrollment" type="primary" @click="submitForm">&#30830;&#23450;</el-button>
        <el-button @click="open = false">&#21462;&#28040;</el-button>
      </div>
    </el-dialog>

    <el-dialog title="课次记录详情" :visible.sync="detailOpen" width="820px">
      <div v-if="detailRow.enrollmentId" class="detail-summary">
        <strong>{{ detailRow.courseName }}</strong>
        <span>{{ detailRow.studentName }} · {{ detailRow.teacherName }}</span>
        <el-tag size="small" :type="recordRuntimeStatus(detailRow).tag">{{ recordRuntimeStatus(detailRow).text }}</el-tag>
      </div>
      <div v-if="detailSessions.length" class="session-detail-list">
        <article v-for="item in detailSessions" :key="item.key" class="session-detail-card">
          <header>
            <strong>{{ item.label }}</strong>
            <span>{{ item.time }}</span>
          </header>
          <div class="session-detail-grid">
            <section>
              <label>学生学习记录</label>
              <p>{{ getSessionRecordText(detailRow, item, 'student') }}</p>
            </section>
            <section>
              <label>教师本次反馈</label>
              <p>{{ getSessionRecordText(detailRow, item, 'teacher') }}</p>
            </section>
          </div>
        </article>
      </div>
      <div v-else class="record-empty">暂无已上课次，开课后可查看每次记录。</div>
      <div v-if="isCourseFinished(detailRow)" class="detail-final-feedback">
        <label>课程总结</label>
        <p>{{ getCourseSummary(detailRow) }}</p>
      </div>
      <div v-if="isCourseFinished(detailRow)" class="detail-final-feedback">
        <label>结课反馈</label>
        <p>{{ getFinalFeedback(detailRow) }}</p>
      </div>
      <div slot="footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="teacherSessionTitle" :visible.sync="teacherSessionOpen" width="720px">
      <div class="detail-summary">
        <strong>{{ teacherSessionCourse.courseName }}</strong>
        <span>{{ getTeacherMissingFeedbackCount(teacherSessionCourse) }} 次未提交</span>
      </div>
      <el-select v-model="teacherSessionKey" class="session-select teacher-session-select" placeholder="请选择课次" @change="handleTeacherSessionChange">
        <el-option
          v-for="session in getPastSessions(teacherSessionCourse)"
          :key="session.key"
          :value="session.key"
        >
          <span>{{ session.label }} {{ session.time }}</span>
          <span class="session-option-status">{{ getCourseSessionFeedback(teacherSessionCourse, session) ? '已填写' : '未填写' }}</span>
        </el-option>
      </el-select>
      <el-input v-model="teacherSessionFeedback" type="textarea" :rows="5" placeholder="填写本节课课堂表现、任务完成情况和下次课建议，将同步给本节课所有报名学生。" />
      <div slot="footer">
        <el-button @click="teacherSessionOpen = false">取消</el-button>
        <el-button type="primary" @click="submitTeacherSessionFeedback">保存反馈</el-button>
      </div>
    </el-dialog>

    <el-dialog title="填写结课反馈" :visible.sync="teacherFinalOpen" width="720px">
      <div class="detail-summary">
        <strong>{{ teacherFinalCourse.courseName }}</strong>
      </div>
      <el-input v-model="teacherFinalFeedback" type="textarea" :rows="5" placeholder="填写整门课程的整体表现、成长变化和后续建议，将同步给本课程所有报名学生。" />
      <div slot="footer">
        <el-button @click="teacherFinalOpen = false">取消</el-button>
        <el-button type="primary" @click="submitTeacherFinalFeedback">保存结课反馈</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="teacherRosterTitle" :visible.sync="teacherRosterOpen" width="760px">
      <el-table :data="teacherRosterRows" class="content-table">
        <el-table-column label="学生" prop="studentName" min-width="120" />
        <el-table-column label="家长" prop="parentName" min-width="120" />
        <el-table-column label="报名来源" min-width="110">
          <template slot-scope="scope">{{ scope.row.signupSource === 'parent' ? '家长报名' : '学生报名' }}</template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="teacherRosterOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="courseRecordDialogTitle" :visible.sync="courseRecordOpen" width="860px">
      <div class="detail-summary">
        <strong>{{ courseRecordCourse.courseName }}</strong>
        <span>{{ formatCourseRange(courseRecordCourse) }}</span>
      </div>
      <div class="course-record-filters">
        <el-select v-if="!courseRecordFinalMode" v-model="courseRecordSessionKey" class="session-select" placeholder="请选择课次">
          <el-option
            v-for="session in courseRecordSessions"
            :key="session.key"
            :label="`${session.label} ${session.time}`"
            :value="session.key"
          />
        </el-select>
        <el-select v-model="courseRecordPersonKey" class="session-select" placeholder="请选择学生或教师">
          <el-option
            v-for="person in courseRecordPeople"
            :key="person.key"
            :label="person.label"
            :value="person.key"
          />
        </el-select>
      </div>
      <div v-if="currentCourseRecordPerson" class="session-detail-card course-record-card">
        <header>
          <strong>{{ currentCourseRecordPerson.label }}</strong>
          <span>{{ courseRecordFinalMode ? '结课归档' : (currentCourseRecordSession ? `${currentCourseRecordSession.label} ${currentCourseRecordSession.time}` : '未选择课次') }}</span>
        </header>
        <div v-if="courseRecordFinalMode" class="session-detail-grid">
          <section v-if="isCourseRecordStudent">
            <label>学生课程总结</label>
            <p>{{ getCourseRecordFinalText('student') }}</p>
          </section>
          <section v-if="isCourseRecordTeacher">
            <label>教师结课反馈</label>
            <p>{{ getCourseRecordFinalText('teacher') }}</p>
          </section>
        </div>
        <div v-else class="session-detail-grid">
          <section v-if="isCourseRecordStudent">
            <label>学生学习记录</label>
            <p>{{ getCourseRecordText('student') }}</p>
          </section>
          <section v-if="isCourseRecordTeacher">
            <label>教师本次反馈</label>
            <p>{{ getCourseRecordText('teacher') }}</p>
          </section>
        </div>
      </div>
      <div v-else class="record-empty">请选择课次和查看对象。</div>
      <div slot="footer">
        <el-button @click="courseRecordOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { exportEnrollment, listEnrollment, updateEnrollment } from '@/api/edu/enrollment'
import { listCourse } from '@/api/edu/course'

export default {
  name: 'EduEnrollment',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      activePageNum: 1,
      activePageSize: 10,
      finishedPageNum: 1,
      finishedPageSize: 10,
      activeTeacherPageNum: 1,
      activeTeacherPageSize: 6,
      finishedTeacherPageNum: 1,
      finishedTeacherPageSize: 6,
      open: false,
      enrollmentList: [],
      courseList: [],
      queryParams: { pageNum: 1, pageSize: 10, studentName: undefined, courseName: undefined, teacherName: undefined, runtimeStatus: undefined },
      form: {},
      recordEditMode: 'session',
      selectedSessionKey: '',
      sessionDraft: { student: '', teacher: '' },
      finalFeedbackDraft: '',
      courseSummaryDraft: '',
      detailOpen: false,
      detailRow: {},
      now: new Date(),
      teacherSessionOpen: false,
      teacherSessionCourse: {},
      teacherSession: {},
      teacherSessionKey: '',
      teacherSessionFeedback: '',
      teacherFinalOpen: false,
      teacherFinalCourse: {},
      teacherFinalFeedback: '',
      teacherRosterOpen: false,
      teacherRosterTitle: '学生名单',
      teacherRosterRows: [],
      courseRecordOpen: false,
      courseRecordCourse: {},
      courseRecordSessionKey: '',
      courseRecordPersonKey: '',
      courseRecordFinalMode: false
    }
  },
  computed: {
    roleKeys() {
      return this.$store.getters.roles || []
    },
    isStudentView() {
      return this.roleKeys.includes('edu_student')
    },
    isParentView() {
      return this.roleKeys.includes('edu_parent')
    },
    isTeacherView() {
      return this.roleKeys.includes('edu_teacher')
    },
    isAdminView() {
      return this.roleKeys.includes('admin') || this.roleKeys.includes('edu_admin')
    },
    isCourseRecordView() {
      return this.isTeacherView || this.isAdminView
    },
    isStudentOrParent() {
      return this.isStudentView || this.isParentView
    },
    pageTitle() {
      if (this.isStudentOrParent) return '\u5b66\u4e60\u8bb0\u5f55'
      if (this.isTeacherView || this.isAdminView) return '上课记录'
      return '\u62a5\u540d\u8bb0\u5f55'
    },
    pageBadge() {
      if (this.isStudentOrParent) return 'Learning Record'
      if (this.isTeacherView || this.isAdminView) return 'Class Record'
      return 'Enrollment Tracker'
    },
    pageDescription() {
      if (this.isStudentOrParent) {
        return '集中查看每次课学习记录、教师反馈与结课总结，支持学生补充课中记录，家长同步了解成长进展。'
      }
      if (this.isTeacherView) {
        return '按课程维护每次课反馈、查看学生名单，并在课程结束后统一填写结课反馈。'
      }
      if (this.isAdminView) {
        return '集中查看全平台学生课中记录、教师反馈与结课总结，掌握课程过程留痕与学习反馈情况。'
      }
      return '集中查看平台报名状态、课程归属、教师安排与学习记录，快速跟踪课后服务参与情况。'
    },
    finishedCount() {
      return this.enrollmentList.filter(item => this.isCourseFinished(item)).length
    },
    displayTotal() {
      return this.isCourseRecordView ? this.teacherCourseSections.length : this.total
    },
    displayFinishedCount() {
      return this.isCourseRecordView ? this.finishedTeacherCourseSections.length : this.finishedCount
    },
    secondStatLabel() {
      if (this.isCourseRecordView) return '已结课课程'
      if (this.isStudentView) return '已结课'
      return '已完成'
    },
    activeEnrollmentList() {
      return this.sortMidCourseRecords(this.filteredEnrollmentList.filter(item => this.recordRuntimeStatus(item).code !== 'closed'))
    },
    activePagedEnrollmentList() {
      return this.paginateRows(this.activeEnrollmentList, this.activePageNum, this.activePageSize)
    },
    finishedEnrollmentList() {
      return this.filteredEnrollmentList.filter(item => this.recordRuntimeStatus(item).code !== 'closed' && this.isCourseFinished(item))
    },
    finishedPagedEnrollmentList() {
      return this.paginateRows(this.finishedEnrollmentList, this.finishedPageNum, this.finishedPageSize)
    },
    filteredEnrollmentList() {
      if (!this.queryParams.runtimeStatus) return this.enrollmentList
      return this.enrollmentList.filter(item => this.recordRuntimeStatus(item).code === this.queryParams.runtimeStatus)
    },
    teacherCourseSections() {
      const map = new Map()
      this.enrollmentList.forEach(row => {
        const key = this.getCourseKey(row)
        if (!map.has(key)) {
          map.set(key, [])
        }
        map.get(key).push(row)
      })
      const baseCourses = this.courseList.length
        ? this.courseList
        : Array.from(map.entries()).map(([key, rows]) => ({ ...(rows[0] || {}), courseId: key }))
      const sections = baseCourses
        .filter(course => !this.queryParams.studentName || map.has(this.getCourseKey(course)))
        .map(course => ({
          ...course,
          rows: map.get(this.getCourseKey(course)) || [],
          sessions: this.enumerateClassSessions(course)
        }))
        .filter(course => this.recordRuntimeStatus(course).code !== 'closed')
        .filter(course => !this.queryParams.runtimeStatus || this.recordRuntimeStatus(course).code === this.queryParams.runtimeStatus)
      return sections.sort((a, b) => {
        const aFinished = this.isCourseFinished(a) ? 1 : 0
        const bFinished = this.isCourseFinished(b) ? 1 : 0
        if (aFinished !== bFinished) return aFinished - bFinished
        const aTime = a.sessions[0] ? a.sessions[0].start.getTime() : 0
        const bTime = b.sessions[0] ? b.sessions[0].start.getTime() : 0
        return aTime - bTime
      })
    },
    activeTeacherCourseSections() {
      return this.teacherCourseSections.filter(item => this.recordRuntimeStatus(item).code !== 'closed')
    },
    activePagedTeacherCourseSections() {
      return this.paginateRows(this.activeTeacherCourseSections, this.activeTeacherPageNum, this.activeTeacherPageSize)
    },
    finishedTeacherCourseSections() {
      return this.teacherCourseSections.filter(item => this.recordRuntimeStatus(item).code !== 'closed' && this.isCourseFinished(item))
    },
    finishedPagedTeacherCourseSections() {
      return this.paginateRows(this.finishedTeacherCourseSections, this.finishedTeacherPageNum, this.finishedTeacherPageSize)
    },
    showActionColumn() {
      return true
    },
    actionButtonText() {
      if (this.isStudentView) {
        return '填写学习记录'
      }
      if (this.isTeacherView) {
        return '填写反馈'
      }
      return '查看'
    },
    canSubmitEnrollment() {
      return this.isStudentView || this.isTeacherView
    },
    isCourseSummaryMode() {
      return this.isStudentView && this.recordEditMode === 'summary'
    },
    dialogTitle() {
      if (this.isCourseSummaryMode) return '填写课程总结'
      if (this.isStudentView) return '填写学习记录'
      if (this.isTeacherView) return '填写上课记录与反馈'
      return '查看学习记录与教师反馈'
    },
    availableSessions() {
      return this.enumerateClassSessions(this.form).filter(item => item.end <= new Date())
    },
    detailSessions() {
      return this.enumerateClassSessions(this.detailRow).filter(item => item.end <= new Date())
    },
    selectedSession() {
      return this.availableSessions.find(item => item.key === this.selectedSessionKey) || this.availableSessions[0]
    },
    missingSessionText() {
      if (!this.missingSessionCount) return '已上课次均已填写'
      return `${this.missingSessionCount} 次待补充`
    },
    missingSessionCount() {
      const records = this.parseSessionRecords(this.form)
      const missing = this.availableSessions.filter(item => {
        const record = records[item.key] || {}
        if (this.isStudentView) return !record.student
        if (this.isTeacherView) return !record.teacher
        return !record.student || !record.teacher
      })
      return missing.length
    },
    teacherSessionTitle() {
      return this.teacherSession && this.teacherSession.label ? '填写本节课反馈' : '填写反馈'
    },
    courseRecordDialogTitle() {
      return this.courseRecordFinalMode ? '查看结课总结' : '查看课次记录'
    },
    courseRecordSessions() {
      return this.getPastSessions(this.courseRecordCourse)
    },
    courseRecordRows() {
      return this.courseRecordCourse.rows || []
    },
    courseRecordPeople() {
      const teacherName = this.courseRecordCourse.teacherName || (this.courseRecordRows[0] && this.courseRecordRows[0].teacherName) || '授课教师'
      return [{ key: 'teacher', label: `教师：${teacherName}` }].concat(this.courseRecordRows.map(row => ({
        key: `student-${row.enrollmentId}`,
        label: `学生：${row.studentName}`,
        row
      })))
    },
    currentCourseRecordSession() {
      return this.courseRecordSessions.find(item => item.key === this.courseRecordSessionKey)
    },
    currentCourseRecordPerson() {
      return this.courseRecordPeople.find(item => item.key === this.courseRecordPersonKey)
    },
    isCourseRecordTeacher() {
      return this.currentCourseRecordPerson && this.currentCourseRecordPerson.key === 'teacher'
    },
    isCourseRecordStudent() {
      return this.currentCourseRecordPerson && this.currentCourseRecordPerson.key !== 'teacher'
    }
  },
  created() {
    if (this.$route.query && this.$route.query.courseName) {
      this.queryParams.courseName = this.$route.query.courseName
    }
    this.getList()
  },
  watch: {
    selectedSessionKey() {
      this.loadSelectedSessionDraft()
    }
  },
  methods: {
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        pageNum: 1,
        pageSize: 1000,
        status: undefined
      }
      const courseParams = {
        pageNum: 1,
        pageSize: 1000,
        courseName: this.queryParams.courseName,
        teacherName: this.queryParams.teacherName
      }
      Promise.all([
        listEnrollment(params),
        this.isCourseRecordView ? listCourse(courseParams) : Promise.resolve({ rows: [] })
      ]).then(([enrollmentRes, courseRes]) => {
        this.enrollmentList = enrollmentRes.rows || []
        this.total = enrollmentRes.total || 0
        this.courseList = courseRes.rows || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    refreshList() {
      this.activePageNum = 1
      this.finishedPageNum = 1
      this.activeTeacherPageNum = 1
      this.finishedTeacherPageNum = 1
      this.teacherSessionOpen = false
      this.teacherRosterOpen = false
      this.courseRecordOpen = false
      this.detailOpen = false
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, studentName: undefined, courseName: undefined, teacherName: undefined, runtimeStatus: undefined }
      this.activePageNum = 1
      this.finishedPageNum = 1
      this.activeTeacherPageNum = 1
      this.finishedTeacherPageNum = 1
      this.getList()
    },
    handleActivePagination() {
      this.activePageNum = Math.max(1, this.activePageNum)
    },
    handleFinishedPagination() {
      this.finishedPageNum = Math.max(1, this.finishedPageNum)
    },
    handleActiveTeacherPagination() {
      this.activeTeacherPageNum = Math.max(1, this.activeTeacherPageNum)
    },
    handleFinishedTeacherPagination() {
      this.finishedTeacherPageNum = Math.max(1, this.finishedTeacherPageNum)
    },
    paginateRows(rows, pageNum, pageSize) {
      const start = (Number(pageNum || 1) - 1) * Number(pageSize || 10)
      return rows.slice(start, start + Number(pageSize || 10))
    },
    sortMidCourseRecords(rows) {
      return (rows || []).slice().sort((a, b) => {
        const aFinished = this.isCourseFinished(a) ? 1 : 0
        const bFinished = this.isCourseFinished(b) ? 1 : 0
        if (aFinished !== bFinished) return aFinished - bFinished
        const aTime = this.enumerateClassSessions(a)[0]
        const bTime = this.enumerateClassSessions(b)[0]
        return (aTime ? aTime.start.getTime() : 0) - (bTime ? bTime.start.getTime() : 0)
      })
    },
    getCourseKey(row) {
      return String((row && (row.courseId || row.courseName)) || '')
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.recordEditMode = 'session'
      const sessions = this.enumerateClassSessions(this.form).filter(item => item.end <= new Date())
      this.selectedSessionKey = sessions.length ? sessions[sessions.length - 1].key : ''
      this.loadSelectedSessionDraft()
      this.finalFeedbackDraft = this.getFinalFeedback(this.form) === '暂未填写' ? '' : this.getFinalFeedback(this.form)
      this.courseSummaryDraft = this.getCourseSummary(this.form) === '暂未填写' ? '' : this.getCourseSummary(this.form)
      this.open = true
    },
    handleCourseSummary(row) {
      this.form = { ...row }
      this.recordEditMode = 'summary'
      this.selectedSessionKey = ''
      this.sessionDraft = { student: this.getCourseSummary(this.form) === '暂未填写' ? '' : this.getCourseSummary(this.form), teacher: '' }
      this.finalFeedbackDraft = this.getFinalFeedback(this.form) === '暂未填写' ? '' : this.getFinalFeedback(this.form)
      this.courseSummaryDraft = this.sessionDraft.student
      this.open = true
    },
    handleDetail(row) {
      this.detailRow = { ...row }
      this.detailOpen = true
    },
    submitForm() {
      if (!this.canSubmitEnrollment) {
        this.open = false
        return
      }
      const records = this.parseSessionRecords(this.form)
      if (this.isCourseSummaryMode) {
        const learningPayload = this.stringifyRecordPayload(records, this.form.learningRecord, {
          courseSummary: this.sessionDraft.student
        })
        updateEnrollment({ enrollmentId: this.form.enrollmentId, learningRecord: learningPayload }).then(() => {
          this.$modal.msgSuccess('\u4fdd\u5b58\u6210\u529f')
          this.open = false
          this.getList()
        })
        return
      }
      if (this.selectedSessionKey) {
        records[this.selectedSessionKey] = {
          ...(records[this.selectedSessionKey] || {}),
          label: this.selectedSession ? this.selectedSession.label : '',
          time: this.selectedSession ? this.selectedSession.time : '',
          student: this.isStudentView ? this.sessionDraft.student : (records[this.selectedSessionKey] || {}).student,
          teacher: this.isTeacherView ? this.sessionDraft.teacher : (records[this.selectedSessionKey] || {}).teacher
        }
      }
      const learningPayload = this.stringifyRecordPayload(records, this.form.learningRecord, {
        courseSummary: this.isCourseFinished(this.form) ? this.courseSummaryDraft : undefined
      })
      const teacherPayload = this.stringifyRecordPayload(records, this.form.interactionSummary)
      const payload = this.isStudentView
        ? { enrollmentId: this.form.enrollmentId, learningRecord: learningPayload }
        : {
            enrollmentId: this.form.enrollmentId,
            interactionSummary: teacherPayload,
            remark: this.isCourseFinished(this.form) ? this.finalFeedbackDraft : undefined
          }
      updateEnrollment(payload).then(() => {
        this.$modal.msgSuccess('\u4fdd\u5b58\u6210\u529f')
        this.open = false
        this.getList()
      })
    },
    handleExport() {
      exportEnrollment(this.queryParams)
    },
    loadSelectedSessionDraft() {
      const records = this.parseSessionRecords(this.form)
      const record = records[this.selectedSessionKey] || {}
      this.sessionDraft = {
        student: record.student || '',
        teacher: record.teacher || ''
      }
    },
    parseSessionRecords(row) {
      const merged = {}
      ;[row.learningRecord, row.interactionSummary].forEach(value => {
        try {
          const parsed = JSON.parse(value || '{}')
          if (parsed && parsed.version === 1 && parsed.sessions) {
            Object.keys(parsed.sessions).forEach(key => {
              merged[key] = { ...(merged[key] || {}), ...parsed.sessions[key] }
            })
          }
        } catch (error) {}
      })
      const sessions = this.enumerateClassSessions(row).filter(item => item.end <= new Date())
      const latestSession = sessions[sessions.length - 1]
      if (latestSession) {
        if (row.learningRecord && !this.isStructuredRecord(row.learningRecord)) {
          merged[latestSession.key] = { ...(merged[latestSession.key] || {}), label: latestSession.label, time: latestSession.time, student: row.learningRecord }
        }
        if (row.interactionSummary && !this.isStructuredRecord(row.interactionSummary)) {
          merged[latestSession.key] = { ...(merged[latestSession.key] || {}), label: latestSession.label, time: latestSession.time, teacher: row.interactionSummary }
        }
      }
      return merged
    },
    stringifyRecordPayload(records, fallbackText, extra = {}) {
      const payload = {
        version: 1,
        fallback: this.isStructuredRecord(fallbackText) ? '' : (fallbackText || ''),
        sessions: records
      }
      Object.keys(extra || {}).forEach(key => {
        if (extra[key] !== undefined) {
          payload[key] = extra[key]
        }
      })
      return JSON.stringify(payload)
    },
    isStructuredRecord(value) {
      try {
        const parsed = JSON.parse(value || '{}')
        return parsed && parsed.version === 1 && parsed.sessions
      } catch (error) {
        return false
      }
    },
    getFilledSummary(row) {
      const sessions = this.enumerateClassSessions(row).filter(item => item.end <= new Date())
      const records = this.parseSessionRecords(row)
      const studentCount = sessions.filter(item => records[item.key] && records[item.key].student).length
      const teacherCount = sessions.filter(item => records[item.key] && records[item.key].teacher).length
      return `学生 ${studentCount}/${sessions.length} · 教师 ${teacherCount}/${sessions.length}`
    },
    getMissingSummary(row) {
      const sessions = this.enumerateClassSessions(row).filter(item => item.end <= new Date())
      if (!sessions.length) return '暂无已上课次'
      const records = this.parseSessionRecords(row)
      const missing = sessions.filter(item => !records[item.key] || !records[item.key].student || !records[item.key].teacher)
      return missing.length ? `${missing.length} 节课待补充` : '已上课次记录完整'
    },
    getFinalRecordSummary(row) {
      const studentDone = this.getCourseSummary(row) !== '暂未填写'
      const teacherDone = this.getFinalFeedback(row) !== '暂未填写' && this.getFinalFeedback(row) !== '课程未结课'
      return `学生总结${studentDone ? '已提交' : '未提交'} · 教师反馈${teacherDone ? '已填写' : '未填写'}`
    },
    getFinalRecordHint(row) {
      const sessions = this.enumerateClassSessions(row)
      const total = sessions.length
      if (!total) return '暂无课程课次'
      return `共 ${total} 次课，结课后统一归档`
    },
    getLatestSessionText(row, type) {
      const sessions = this.enumerateClassSessions(row).filter(item => item.end <= new Date()).reverse()
      const records = this.parseSessionRecords(row)
      const key = type === 'student' ? 'student' : 'teacher'
      const hit = sessions.find(item => records[item.key] && records[item.key][key])
      if (hit) return records[hit.key][key]
      const fallback = type === 'student' ? row.learningRecord : row.interactionSummary
      if (this.isStructuredRecord(fallback)) return '暂未填写'
      return fallback || '暂未填写'
    },
    getFinalFeedback(row) {
      if (!this.isCourseFinished(row)) return '课程未结课'
      if (!row.remark) return '暂未填写'
      return row.remark
    },
    getCourseSummary(row) {
      try {
        const parsed = JSON.parse(row.learningRecord || '{}')
        if (parsed && parsed.version === 1 && parsed.courseSummary) {
          return parsed.courseSummary
        }
      } catch (error) {}
      return '暂未填写'
    },
    getSessionRecordText(row, session, type) {
      const records = this.parseSessionRecords(row)
      const record = records[session.key] || {}
      const content = type === 'student' ? record.student : record.teacher
      return content || '暂未填写'
    },
    recordRuntimeStatus(row) {
      const courseStatus = row.courseStatus !== undefined && row.courseStatus !== null ? row.courseStatus : row.status
      if (courseStatus !== undefined && courseStatus !== null && String(courseStatus) !== '0') {
        return { code: 'closed', text: '已停开', tag: 'info' }
      }
      const sessions = this.enumerateClassSessions(row)
      const now = new Date()
      if (!sessions.length) {
        return { code: 'pending', text: '待开课', tag: 'warning' }
      }
      if (now < sessions[0].start) {
        return { code: 'pending', text: '待开课', tag: 'warning' }
      }
      if (now > sessions[sessions.length - 1].end) {
        return { code: 'finished', text: '已结课', tag: 'success' }
      }
      return { code: 'active', text: '开课中', tag: 'primary' }
    },
    handleTeacherCourse(course) {
      this.teacherSessionCourse = course
      const sessions = this.getPastSessions(course)
      this.teacherSession = sessions[sessions.length - 1] || {}
      this.teacherSessionKey = this.teacherSession.key || ''
      this.teacherSessionFeedback = this.teacherSessionKey ? this.getCourseSessionFeedback(course, this.teacherSession) : ''
      this.teacherSessionOpen = true
    },
    handleTeacherSessionChange(key) {
      const session = this.getPastSessions(this.teacherSessionCourse).find(item => item.key === key) || {}
      this.teacherSession = session
      this.teacherSessionFeedback = session.key ? this.getCourseSessionFeedback(this.teacherSessionCourse, session) : ''
    },
    getPastSessions(course) {
      return (course.sessions || []).filter(session => session.end <= this.now)
    },
    getTeacherFeedbackCount(course) {
      return this.getPastSessions(course).filter(session => this.getCourseSessionFeedback(course, session)).length
    },
    getTeacherMissingFeedbackCount(course) {
      return Math.max(this.getPastSessions(course).length - this.getTeacherFeedbackCount(course), 0)
    },
    getCourseSessionFeedback(course, session) {
      const firstRow = course.rows && course.rows[0] ? course.rows[0] : course
      const records = this.parseSessionRecords(firstRow)
      return (records[session.key] && records[session.key].teacher) || ''
    },
    submitTeacherSessionFeedback() {
      const course = this.teacherSessionCourse
      const session = this.teacherSession
      if (!session || !session.key) {
        this.$modal.msgWarning('请先选择课次')
        return
      }
      const rows = course.rows || []
      const requests = rows.map(row => {
        const records = this.parseSessionRecords(row)
        records[session.key] = {
          ...(records[session.key] || {}),
          label: session.label,
          time: session.time,
          student: (records[session.key] || {}).student,
          teacher: this.teacherSessionFeedback
        }
        return updateEnrollment({
          enrollmentId: row.enrollmentId,
          interactionSummary: this.stringifyRecordPayload(records, row.interactionSummary)
        })
      })
      Promise.all(requests).then(() => {
        this.$modal.msgSuccess('本节课反馈已同步给报名学生')
        this.teacherSessionOpen = false
        this.getList()
      })
    },
    handleTeacherFinal(course) {
      this.teacherFinalCourse = course
      this.teacherFinalFeedback = this.getTeacherFinalFeedback(course)
      this.teacherFinalOpen = true
    },
    handleTeacherRoster(course) {
      this.teacherRosterTitle = `《${course.courseName}》学生名单`
      this.teacherRosterRows = course.rows || []
      this.teacherRosterOpen = true
    },
    handleCourseRecordView(course, finalMode = false) {
      this.courseRecordCourse = course
      const sessions = this.getPastSessions(course)
      this.courseRecordFinalMode = finalMode
      this.courseRecordSessionKey = sessions.length ? sessions[sessions.length - 1].key : ''
      this.courseRecordPersonKey = 'teacher'
      this.courseRecordOpen = true
    },
    getCourseRecordFinalText(type) {
      const person = this.currentCourseRecordPerson
      if (!person) {
        return '暂未选择'
      }
      const targetRow = person.key === 'teacher'
        ? (this.courseRecordRows[0] || this.courseRecordCourse)
        : person.row
      if (!targetRow) {
        return '暂未填写'
      }
      if (type === 'student' && person.key === 'teacher') {
        return '请选择具体学生查看课程总结'
      }
      if (type === 'teacher') {
        return this.getFinalFeedback(targetRow)
      }
      return this.getCourseSummary(targetRow)
    },
    getCourseRecordText(type) {
      const session = this.currentCourseRecordSession
      const person = this.currentCourseRecordPerson
      if (!session || !person) {
        return '暂未选择'
      }
      const targetRow = person.key === 'teacher'
        ? (this.courseRecordRows[0] || this.courseRecordCourse)
        : person.row
      if (!targetRow) {
        return '暂未填写'
      }
      if (type === 'student' && person.key === 'teacher') {
        return '请选择具体学生查看学习记录'
      }
      return this.getSessionRecordText(targetRow, session, type)
    },
    getTeacherFinalFeedback(course) {
      const rows = course.rows || []
      const hit = rows.find(item => item.remark)
      return hit ? hit.remark : ''
    },
    submitTeacherFinalFeedback() {
      const rows = this.teacherFinalCourse.rows || []
      const requests = rows.map(row => updateEnrollment({
        enrollmentId: row.enrollmentId,
        remark: this.teacherFinalFeedback
      }))
      Promise.all(requests).then(() => {
        this.$modal.msgSuccess('结课反馈已同步给本课程学生')
        this.teacherFinalOpen = false
        this.getList()
      })
    },
    getSessionFillState(session) {
      const records = this.parseSessionRecords(this.form)
      const record = records[session.key] || {}
      if (record.student && record.teacher) return '已完整'
      if (record.student || record.teacher) return '部分填写'
      return '未填写'
    },
    parseDate(value) {
      if (!value) return null
      const match = String(value).match(/^(\d{4})-(\d{1,2})-(\d{1,2})/)
      if (match) return new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]))
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
    parseCourseSlots(row) {
      const text = String(row.weekDay || '')
      const slots = []
      const pattern = /(周[一二三四五六日天])\s*(\d{1,2}:\d{2})?\s*-?\s*(\d{1,2}:\d{2})?/g
      let match = pattern.exec(text)
      while (match) {
        slots.push({
          weekDay: match[1] === '周天' ? '周日' : match[1],
          startTime: match[2] || row.startTime || '16:00',
          endTime: match[3] || row.endTime || '17:30'
        })
        match = pattern.exec(text)
      }
      return slots.length ? slots : [{ weekDay: '周一', startTime: row.startTime || '16:00', endTime: row.endTime || '17:30' }]
    },
    getWeekDayNumber(weekDay) {
      return { 周日: 0, 周一: 1, 周二: 2, 周三: 3, 周四: 4, 周五: 5, 周六: 6 }[weekDay]
    },
    enumerateClassSessions(row) {
      const start = this.parseDate(row.startDate)
      const end = this.parseDate(row.endDate)
      const slots = this.parseCourseSlots(row)
      if (!start || !end || !slots.length) return []
      const sessions = []
      const cursor = new Date(start)
      let index = 1
      while (cursor <= end) {
        slots.forEach(slot => {
          if (this.getWeekDayNumber(slot.weekDay) === cursor.getDay()) {
            const classStart = this.parseDateTime(cursor, slot.startTime)
            sessions.push({
              key: `${this.formatDateKey(classStart)}-${slot.startTime}`,
              label: `第 ${index++} 次课 · ${this.formatSessionDate(classStart)}`,
              time: `${slot.startTime}-${slot.endTime}`,
              start: classStart,
              end: this.parseDateTime(cursor, slot.endTime),
              slot
            })
          }
        })
        cursor.setDate(cursor.getDate() + 1)
      }
      return sessions.sort((a, b) => a.start - b.start)
    },
    isCourseFinished(row) {
      const sessions = this.enumerateClassSessions(row)
      return sessions.length > 0 && sessions[sessions.length - 1].end < new Date()
    },
    formatDateKey(date) {
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${date.getFullYear()}-${month}-${day}`
    },
    formatSessionDate(date) {
      const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${this.formatDateKey(date)} ${weekMap[date.getDay()]}`
    },
    formatCourseRange(row) {
      const start = this.parseDate(row.startDate)
      const end = this.parseDate(row.endDate)
      if (!start || !end) return '未设置周期'
      return `${this.formatDateKey(start)} 至 ${this.formatDateKey(end)}`
    },
    formatGradeScope(scope) {
      const grades = ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级']
      const list = String(scope || '').replace(/，/g, ',').split(',').map(item => item.trim()).filter(Boolean)
      if (!list.length) return '全年级'
      const indexes = list.map(item => grades.indexOf(item)).filter(index => index >= 0).sort((a, b) => a - b)
      if (indexes.length === list.length && indexes.every((index, order) => order === 0 || index === indexes[order - 1] + 1)) {
        const start = grades[indexes[0]]
        const end = grades[indexes[indexes.length - 1]]
        return start === end ? start : `${start.replace('年级', '')}至${end}`
      }
      return list.join('、')
    }
  }
}
</script>

<style scoped>
.enrollment-page {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
}

.enrollment-page::before,
.enrollment-page::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(8px);
  pointer-events: none;
}

.enrollment-page::before {
  top: -50px;
  right: 6%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(38, 232, 183, 0.18), transparent 68%);
}

.enrollment-page::after {
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
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  min-width: 250px;
}

.stat-card {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow:
    0 16px 28px rgba(11, 32, 40, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
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
  margin-bottom: 16px;
  padding: 20px 22px 6px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow:
    0 22px 40px rgba(41, 130, 141, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
}

.toolbar-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
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

.enrollment-page .table-section-card {
  margin-bottom: 16px;
  padding: 18px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
}

.enrollment-page .table-section-card .content-table {
  overflow: hidden;
  border-radius: 20px;
}

.enrollment-page .table-section-card :deep(.pagination-container) {
  margin: 12px -18px -18px;
}

.record-module :deep(.pagination-container) {
  margin: 14px -18px -18px;
}

.record-module {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
  padding: 18px;
  border: 1px solid rgba(157, 232, 233, 0.36);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 34px rgba(41, 130, 141, 0.08);
}

.module-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.module-title h3 {
  margin: 0 0 8px;
  color: #173746;
  font-size: 24px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
  letter-spacing: 0.01em;
}

.module-title span {
  color: #6f8792;
  font-size: 13px;
}

.table-module-title {
  margin: 0 0 12px;
}

.teacher-course-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.teacher-course-card {
  padding: 16px;
  border: 1px solid rgba(134, 214, 222, 0.24);
  border-radius: 20px;
  background: #ffffff;
}

.teacher-course-card header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.teacher-course-card header strong {
  display: block;
  color: #173746;
  font-size: 16px;
}

.teacher-course-card header span,
.course-student-line {
  display: block;
  margin-top: 6px;
  color: #6f8792;
  font-size: 12px;
  line-height: 1.7;
}

.teacher-session-list {
  display: grid;
  gap: 10px;
  margin-top: 14px;
}

.teacher-course-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
  padding: 12px;
  border-radius: 14px;
  background: #f7fbfa;
}

.teacher-course-actions span {
  color: #6f8792;
}

.teacher-action-buttons,
.table-actions {
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  white-space: nowrap;
}

.table-actions .el-button + .el-button,
.teacher-action-buttons .el-button + .el-button {
  margin-left: 0;
}

.toolbar-plain-btn {
  height: 32px;
  padding: 0 14px;
  border-radius: 12px;
  border-color: rgba(188, 222, 227, 0.8);
  background: #ffffff;
  color: #526b77;
  font-weight: 700;
}

.teacher-session-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 11px 12px;
  border-radius: 14px;
  background: #f7fbfa;
}

.teacher-session-item strong {
  display: block;
  color: #315766;
}

.teacher-session-item span,
.course-final-preview {
  color: #6f8792;
  line-height: 1.7;
}

.course-final-preview {
  min-height: 48px;
  margin: 14px 0;
  padding: 12px;
  border-radius: 14px;
  background: #f7fbfa;
}

.record-progress {
  display: grid;
  gap: 4px;
}

.record-progress strong {
  color: #173746;
  font-size: 13px;
}

.record-progress span {
  color: #7b929c;
  font-size: 12px;
}

.record-form .session-select {
  width: 100%;
}

.record-dialog-title {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.record-dialog-title strong {
  color: #173746;
  font-size: 18px;
  font-weight: 700;
}

.record-dialog-title span {
  padding: 3px 9px;
  border-radius: 999px;
  background: #eef8f7;
  color: #4d837b;
  font-size: 12px;
  font-weight: 600;
}

.teacher-session-select {
  width: 100%;
  margin-bottom: 14px;
}

.session-option-status {
  float: right;
  color: #8aa0a8;
  font-size: 12px;
}

.single-line-label {
  white-space: nowrap;
}

.missing-tip,
.final-feedback-lock {
  padding: 9px 14px;
  border-radius: 16px;
  background: #f6fbfb;
  color: #6f8792;
  line-height: 22px;
}

.missing-session-item {
  margin-bottom: 16px;
}

.missing-session-item .missing-tip {
  min-height: 40px;
  box-sizing: border-box;
}

.record-form ::v-deep .missing-session-item .el-form-item__label {
  line-height: 40px;
}

.detail-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 14px 16px;
  border: 1px solid rgba(134, 214, 222, 0.26);
  border-radius: 18px;
  background: #f6fbfb;
}

.detail-summary strong {
  color: #173746;
  font-size: 16px;
}

.detail-summary span {
  color: #6f8792;
}

.session-detail-list {
  display: grid;
  gap: 12px;
  max-height: 460px;
  overflow-y: auto;
  padding-right: 4px;
}

.course-record-filters {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.course-record-card {
  margin-top: 8px;
}

.session-detail-card {
  padding: 16px;
  border: 1px solid rgba(134, 214, 222, 0.24);
  border-radius: 18px;
  background: #ffffff;
}

.session-detail-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.session-detail-card header strong {
  color: #173746;
}

.session-detail-card header span {
  color: #6f8792;
  font-size: 12px;
}

.session-detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.session-detail-grid section,
.detail-final-feedback {
  padding: 12px;
  border-radius: 14px;
  background: #f7fbfa;
}

.session-detail-grid label,
.detail-final-feedback label {
  display: block;
  margin-bottom: 8px;
  color: #315766;
  font-weight: 700;
}

.session-detail-grid p,
.detail-final-feedback p {
  margin: 0;
  color: #536f7a;
  line-height: 1.8;
  white-space: pre-wrap;
}

.record-empty {
  padding: 30px 12px;
  border-radius: 18px;
  background: #f7fbfa;
  color: #8aa0a8;
  text-align: center;
}

.detail-final-feedback {
  margin-top: 14px;
}

::v-deep .el-form-item__label {
  color: #456270;
  font-weight: 600;
}

::v-deep .el-input__inner,
::v-deep .el-textarea__inner,
::v-deep .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: #ffffff;
  color: #355161;
  box-shadow: none;
}

::v-deep .el-input__inner:focus,
::v-deep .el-textarea__inner:focus {
  border-color: #25ddbf;
  box-shadow:
    0 0 0 4px rgba(37, 221, 191, 0.12),
    0 12px 22px rgba(39, 182, 194, 0.12);
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

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
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
  }

  .toolbar-main {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-actions {
    justify-content: flex-start;
  }
}
</style>

