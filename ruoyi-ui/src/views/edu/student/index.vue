<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['edu:student:add']" type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd">新增档案</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="studentList">
      <el-table-column label="学生ID" prop="studentUserId" width="100" />
      <el-table-column label="学生姓名" prop="studentName" width="120" />
      <el-table-column label="家长ID" prop="parentUserId" width="100" />
      <el-table-column label="家长姓名" prop="parentName" width="120" />
      <el-table-column label="年级" prop="gradeName" width="120" />
      <el-table-column label="班级" prop="className" width="120" />
      <el-table-column label="兴趣标签" prop="interestTags" min-width="180" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button v-hasPermi="['edu:student:edit']" type="text" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-hasPermi="['edu:student:remove']" type="text" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="660px">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="学生ID" prop="studentUserId"><el-input v-model="form.studentUserId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学生姓名" prop="studentName"><el-input v-model="form.studentName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="家长ID" prop="parentUserId"><el-input v-model="form.parentUserId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="家长姓名"><el-input v-model="form.parentName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="年级"><el-input v-model="form.gradeName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级"><el-input v-model="form.className" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio label="0">正常</el-radio><el-radio label="1">停用</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="兴趣标签"><el-input v-model="form.interestTags" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addStudent, delStudent, listStudent, updateStudent } from '@/api/edu/student'

export default {
  name: 'EduStudent',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      open: false,
      title: '',
      studentList: [],
      queryParams: { pageNum: 1, pageSize: 10 },
      form: {},
      rules: {
        studentUserId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
        studentName: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
        parentUserId: [{ required: true, message: '请输入家长ID', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listStudent(this.queryParams).then(res => {
        this.studentList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleAdd() {
      this.form = { status: '0' }
      this.title = '新增学生档案'
      this.open = true
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.title = '编辑学生档案'
      this.open = true
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.profileId ? updateStudent(this.form) : addStudent(this.form)
        request.then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该档案吗？').then(() => delStudent(row.profileId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    }
  }
}
</script>
