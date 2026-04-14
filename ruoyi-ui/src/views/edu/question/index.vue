<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['edu:question:add']" type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd">提交问题</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="questionList">
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
          <el-button v-hasPermi="['edu:question:remove']" type="text" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="提交作业问题" :visible.sync="open" width="680px">
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
        <el-button type="primary" @click="submitForm">提交并生成AI解答</el-button>
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
        this.$alert(res.data, 'AI重新生成结果', { dangerouslyUseHTMLString: false })
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
