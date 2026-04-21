<template>
  <div class="app-container question-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Homework Q&amp;A</span>
        <h1>作业问答</h1>
        <p>{{ heroDescription }}</p>
      </div>
      <div class="hero-metrics">
        <div class="metric-card">
          <span>问题总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="metric-card">
          <span>已解答</span>
          <strong>{{ answeredCount }}</strong>
        </div>
      </div>
    </section>

    <section
      v-show="showSearch"
      class="toolbar-card search-toolbar"
    >
      <div class="search-toolbar-row">
        <el-form
          ref="queryForm"
          :model="queryParams"
          inline
          size="small"
          class="search-form"
        >
          <el-form-item label="课程" prop="courseName">
            <el-input
              v-model="queryParams.courseName"
              placeholder="请输入课程名称"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="学生" prop="studentName">
            <el-input
              v-model="queryParams.studentName"
              class="input-student"
              placeholder="请输入学生姓名"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="标题" prop="questionTitle">
            <el-input
              v-model="queryParams.questionTitle"
              placeholder="请输入问题标题"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="answerStatus">
            <el-select v-model="queryParams.answerStatus" class="select-status" placeholder="全部状态" clearable>
              <el-option label="待解答" value="0" />
              <el-option label="已解答" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-tooltip content="查询" placement="top">
              <el-button type="primary" size="small" class="toolbar-icon-btn" icon="el-icon-search" @click="handleQuery" />
            </el-tooltip>
            <el-tooltip content="重置筛选" placement="top">
              <el-button size="small" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetQuery" />
            </el-tooltip>
          </el-form-item>
        </el-form>
        <div class="toolbar-actions">
          <el-button
            v-if="canSubmitQuestion"
            type="primary"
            size="small"
            icon="el-icon-edit-outline"
            @click="handleAdd"
          >
            提交问题
          </el-button>
          <el-button
            v-if="canExportQuestion"
            size="small"
            icon="el-icon-download"
            @click="handleExport"
          >
            导出
          </el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" class="inline-right-toolbar" />
        </div>
      </div>
    </section>

    <el-table v-loading="loading" :data="questionList" class="content-table">
      <el-table-column label="课程" prop="courseName" width="150" />
      <el-table-column label="学生" prop="studentName" width="120" />
      <el-table-column label="问题标题" prop="questionTitle" min-width="180" />
      <el-table-column label="问题内容" prop="questionContent" min-width="220" show-overflow-tooltip />
      <el-table-column label="AI解答" prop="aiAnswer" min-width="260" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.answerStatus === '1' ? 'success' : 'warning'">{{ scope.row.answerStatus === '1' ? '已解答' : '待解答' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="handleViewAnswer(scope.row)">查看解答</el-button>
          <el-button
            v-if="canRegenerateAnswer"
            type="text"
            size="mini"
            @click="handleRegenerate(scope.row)"
          >
            重新解答
          </el-button>
          <el-button v-hasPermi="['edu:question:remove']" type="text" size="mini" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="提交作业问题" :visible.sync="open" width="700px">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程ID">
          <el-input v-model="form.courseId" placeholder="可选，填写课程 ID" />
        </el-form-item>
        <el-form-item label="学生ID">
          <el-input v-model="form.studentUserId" placeholder="家长代提交时填写" />
        </el-form-item>
        <el-form-item label="问题标题" prop="questionTitle">
          <el-input v-model="form.questionTitle" />
        </el-form-item>
        <el-form-item label="问题内容" prop="questionContent">
          <el-input v-model="form.questionContent" type="textarea" :rows="5" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">提交并生成 AI 解答</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="answerDialogTitle" :visible.sync="answerOpen" width="760px">
      <div class="answer-panel">
        <div class="answer-head">
          <strong>{{ currentQuestion.questionTitle || '作业解答详情' }}</strong>
          <el-tag :type="currentQuestion.answerStatus === '1' ? 'success' : 'warning'" size="mini">
            {{ currentQuestion.answerStatus === '1' ? '已解答' : '待解答' }}
          </el-tag>
        </div>
        <div class="answer-block">
          <span>问题内容</span>
          <p>{{ currentQuestion.questionContent || '暂无问题内容' }}</p>
        </div>
        <div class="answer-block answer-result">
          <span>AI 解答</span>
          <div
            v-if="regeneratingQuestionId === currentQuestion.questionId"
            class="answer-pending"
          >
            <i class="el-icon-loading" />
            <span>AI 生成中，请稍候...</span>
          </div>
          <div
            v-else
            class="answer-rich"
            v-html="renderAnswerHtml(currentQuestion.aiAnswer || '当前暂无解答内容，请稍后再查看。')"
          />
        </div>
      </div>
      <div slot="footer">
        <el-button @click="answerOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addQuestion, delQuestion, exportQuestion, listQuestion, regenerateQuestionAnswer } from '@/api/edu/question'
import { renderAiContentHtml } from '@/utils/aiContent'

export default {
  name: 'EduQuestion',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      open: false,
      answerOpen: false,
      questionList: [],
      currentQuestion: {},
      regeneratingQuestionId: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: undefined,
        studentName: undefined,
        questionTitle: undefined,
        answerStatus: undefined
      },
      form: {},
      rules: {
        questionTitle: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
        questionContent: [{ required: true, message: '请输入问题内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    roleKeys() {
      return this.$store.getters.roles || []
    },
    answeredCount() {
      return this.questionList.filter(item => item.answerStatus === '1').length
    },
    isTeacherRole() {
      return this.roleKeys.includes('edu_teacher')
    },
    isStudentOrParentRole() {
      return this.roleKeys.includes('edu_student') || this.roleKeys.includes('edu_parent')
    },
    isAdminSideRole() {
      return this.roleKeys.includes('admin') || this.roleKeys.includes('edu_admin')
    },
    canSubmitQuestion() {
      return this.isStudentOrParentRole
    },
    canRegenerateAnswer() {
      return this.isTeacherRole || this.isAdminSideRole
    },
    canExportQuestion() {
      return this.isTeacherRole || this.isAdminSideRole || this.isStudentOrParentRole
    },
    heroDescription() {
      if (this.isTeacherRole) {
        return '面向教师日常辅导场景，可集中查看学生提问，并重新生成更贴合课堂节奏的 AI 解答。'
      }
      if (this.isAdminSideRole) {
        return '面向平台管理场景，可统一查看课后问答处理情况，并对 AI 解答结果进行复核与优化。'
      }
      return '面向学生课后辅导场景，支持提交问题、查看 AI 解答，并与教师形成更高效的课后答疑闭环。'
    },
    answerDialogTitle() {
      return this.currentQuestion.studentName ? `${this.currentQuestion.studentName} 的作业解答` : '作业解答详情'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listQuestion(this.queryParams).then(res => {
        this.questionList = res.rows
        this.total = res.total
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.form = {}
      this.open = true
    },
    renderAnswerHtml(content) {
      return renderAiContentHtml(content)
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        addQuestion(this.form).then(() => {
          this.$modal.msgSuccess('提交成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleExport() {
      exportQuestion(this.queryParams)
    },
    handleViewAnswer(row) {
      this.currentQuestion = { ...row }
      this.answerOpen = true
    },
    handleRegenerate(row) {
      this.currentQuestion = {
        ...row,
        aiAnswer: row.aiAnswer || ''
      }
      this.regeneratingQuestionId = row.questionId
      this.answerOpen = true
      regenerateQuestionAnswer(row.questionId)
        .then(res => {
          const latestAnswer = res.data || ''
          const index = this.questionList.findIndex(item => item.questionId === row.questionId)
          if (index > -1) {
            this.$set(this.questionList, index, {
              ...this.questionList[index],
              aiAnswer: latestAnswer,
              answerStatus: '1'
            })
          }
          this.currentQuestion = {
            ...row,
            aiAnswer: latestAnswer,
            answerStatus: '1'
          }
          this.$message.success('AI 解答已更新')
        })
        .catch(() => {
          this.$message.error('重新解答失败，请稍后重试')
        })
        .finally(() => {
          this.regeneratingQuestionId = null
        })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该问题吗？').then(() => delQuestion(row.questionId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    }
  }
}
</script>

<style scoped>
.question-page {
  padding-top: 6px;
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 26px 28px;
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

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  min-width: 240px;
}

.metric-card {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow:
    0 16px 28px rgba(11, 32, 40, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.metric-card span {
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.metric-card strong {
  display: block;
  margin-top: 10px;
  color: #ffffff;
  font-size: 28px;
}

.toolbar-card {
  margin-bottom: 16px;
  padding: 18px 20px 4px;
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

.search-toolbar {
  display: block;
}

.search-toolbar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.search-form {
  flex: 1;
}

::v-deep .search-form .input-student .el-input__inner {
  width: 156px;
}

::v-deep .search-form .select-status .el-input__inner {
  width: 148px;
}

.inline-right-toolbar {
  flex-shrink: 0;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
  padding-top: 1px;
}

::v-deep .search-form .el-form-item {
  margin-bottom: 14px;
}

@media (max-width: 1400px) {
  .search-toolbar-row {
    flex-direction: column;
    align-items: stretch;
  }

  .inline-right-toolbar {
    display: flex;
    justify-content: flex-end;
  }
}

@media (max-width: 900px) {
  .inline-right-toolbar {
    justify-content: flex-start;
  }
}

.content-table {
  margin-bottom: 10px;
}

.danger-text {
  color: #d25d50;
}

.answer-panel {
  display: grid;
  gap: 16px;
}

.answer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(103, 216, 219, 0.18);
}

.answer-head strong {
  color: #203544;
  font-size: 18px;
}

.answer-block {
  padding: 18px 20px;
  border-radius: 18px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(242, 251, 255, 0.92));
}

.answer-block span {
  display: block;
  margin-bottom: 10px;
  color: #1f998c;
  font-size: 12px;
  font-weight: 700;
}

.answer-block p {
  margin: 0;
  color: #566b79;
  line-height: 1.8;
  white-space: pre-wrap;
}

.answer-rich {
  color: #46606f;
  line-height: 1.8;
}

.answer-pending {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 84px;
  color: #4a8f87;
  font-size: 15px;
  font-weight: 600;
}

.answer-pending i {
  font-size: 18px;
}

.answer-rich :deep(h2),
.answer-rich :deep(h3),
.answer-rich :deep(h4) {
  margin: 14px 0 10px;
  color: #173848;
  font-weight: 700;
}

.answer-rich :deep(p) {
  margin: 0 0 10px;
}

.answer-rich :deep(ul),
.answer-rich :deep(ol) {
  margin: 8px 0 10px 18px;
  padding: 0;
}

.answer-rich :deep(li) {
  margin-bottom: 6px;
}

::v-deep .el-table th {
  background: #d1d5db !important;
  color: #374151 !important;
  border-bottom: 2px solid #bcc3cc !important;
  box-shadow: none !important;
}

::v-deep .el-table tr {
  background-color: #ffffff !important;
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: #eef1f4 !important;
}

::v-deep .el-table th:first-child .cell,
::v-deep .el-table td:first-child .cell {
  padding-left: 18px;
}

::v-deep .toolbar-card .el-form-item__label {
  color: #456270;
  font-weight: 600;
}

::v-deep .toolbar-card .el-input__inner,
::v-deep .toolbar-card .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 249, 255, 0.94));
  color: #355161;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

::v-deep .toolbar-card .el-input__inner:focus {
  border-color: #25ddbf;
  box-shadow:
    0 0 0 4px rgba(37, 221, 191, 0.12),
    0 12px 22px rgba(39, 182, 194, 0.12);
}

::v-deep .toolbar-card .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  box-shadow: 0 16px 30px rgba(20, 175, 183, 0.22);
}

::v-deep .toolbar-card .el-button--primary:hover,
::v-deep .toolbar-card .el-button--primary:focus {
  filter: saturate(108%);
  box-shadow: 0 18px 34px rgba(20, 175, 183, 0.28);
}

::v-deep .toolbar-card .el-button--primary.is-plain {
  border: 1px solid rgba(49, 212, 198, 0.34);
  color: #147f71;
  background: rgba(236, 255, 251, 0.9);
  box-shadow: none;
}

@media (max-width: 900px) {
  .hero-panel {
    flex-direction: column;
  }

  .hero-metrics {
    min-width: 0;
  }

  .submit-highlight-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .submit-highlight-btn {
    width: 100%;
  }
}
</style>
