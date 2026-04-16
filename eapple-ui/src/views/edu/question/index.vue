<template>
  <div class="app-container question-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Homework Q&A</span>
        <h1>作业问答</h1>
        <p>面向学生课后辅导场景，支持提交问题、查看 AI 解答，并在教师或管理员视角下统一追踪问答处理情况。</p>
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

    <section class="toolbar-panel">
      <div class="toolbar-copy">
        <strong>AI 课后辅导</strong>
        <span>学生可提交问题，教师可重新生成更合适的 AI 回答。</span>
      </div>
      <div class="toolbar-actions">
        <el-button v-hasPermi="['edu:question:add']" type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd">提交问题</el-button>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
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
          <el-button v-hasPermi="['edu:question:edit']" type="text" size="mini" @click="handleRegenerate(scope.row)">重新生成</el-button>
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
  </div>
</template>

<script>
import { addQuestion, delQuestion, listQuestion, regenerateQuestionAnswer } from '@/api/edu/question'

export default {
  name: 'EduQuestion',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      open: false,
      questionList: [],
      queryParams: { pageNum: 1, pageSize: 10 },
      form: {},
      rules: {
        questionTitle: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
        questionContent: [{ required: true, message: '请输入问题内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    answeredCount() {
      return this.questionList.filter(item => item.answerStatus === '1').length
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
        this.loading = false
      })
    },
    handleAdd() {
      this.form = {}
      this.open = true
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
    handleRegenerate(row) {
      regenerateQuestionAnswer(row.questionId).then(res => {
        this.$alert(res.data, 'AI 重新生成结果', { dangerouslyUseHTMLString: false })
        this.getList()
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

.toolbar-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(130, 112, 82, 0.1);
  border-radius: 22px;
  background: rgba(255, 252, 247, 0.84);
}

.toolbar-copy strong {
  display: block;
  color: #2d4038;
  font-size: 16px;
}

.toolbar-copy span {
  display: block;
  margin-top: 6px;
  color: #748078;
  font-size: 13px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.content-table {
  margin-bottom: 10px;
}

.danger-text {
  color: #d25d50;
}

@media (max-width: 900px) {
  .hero-panel {
    flex-direction: column;
  }

  .hero-metrics {
    min-width: 0;
  }
}
</style>
