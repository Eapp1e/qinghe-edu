<template>
  <div class="app-container student-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Student Profile</span>
        <h1>学生档案</h1>
        <p>统一维护学生基础信息、家长关联关系、年级班级与兴趣标签，支撑课后课程报名、成长记录与个性化服务推荐。</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>档案总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>正常档案</span>
          <strong>{{ activeCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <el-row :gutter="10" class="mb8 toolbar-row">
        <el-col :span="1.5">
          <el-button v-hasPermi="['edu:student:add']" type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd">新增档案</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </el-row>
    </section>

    <el-table v-loading="loading" :data="studentList" class="content-table">
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
  computed: {
    activeCount() {
      return this.studentList.filter(item => item.status === '0').length
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
      this.$modal.confirm(`确认删除该档案“${row.studentName}”吗？`).then(() => delStudent(row.profileId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    }
  }
}
</script>

<style scoped>
.student-page {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
}

.student-page::before,
.student-page::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(8px);
  pointer-events: none;
}

.student-page::before {
  top: -60px;
  right: 5%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(32, 231, 188, 0.18), transparent 68%);
}

.student-page::after {
  left: -70px;
  bottom: 12%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(92, 186, 255, 0.15), transparent 70%);
}

.hero-panel {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 28px;
  border: 1px solid rgba(95, 222, 214, 0.2);
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(67, 239, 189, 0.24), transparent 26%),
    radial-gradient(circle at bottom left, rgba(91, 188, 255, 0.18), transparent 24%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.96) 0%, rgba(234, 255, 249, 0.96) 56%, rgba(237, 247, 255, 0.96) 100%);
  box-shadow:
    0 24px 44px rgba(39, 133, 146, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.hero-copy {
  max-width: 760px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(31, 228, 190, 0.22), rgba(198, 246, 255, 0.74));
  color: #0b866f;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 10px 20px rgba(23, 188, 183, 0.12);
}

.hero-copy h1 {
  margin: 16px 0 12px;
  color: #163643;
  font-size: 34px;
  text-shadow: 0 10px 22px rgba(33, 188, 196, 0.12);
}

.hero-copy p {
  margin: 0;
  color: #617786;
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
  border: 1px solid rgba(112, 214, 220, 0.2);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(233, 250, 255, 0.82));
  box-shadow:
    0 16px 28px rgba(39, 131, 146, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

.stat-card span {
  color: #6f8794;
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 10px;
  color: #163948;
  font-size: 28px;
}

.toolbar-panel {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
  padding: 18px 20px 10px;
  border: 1px solid rgba(103, 216, 219, 0.18);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(239, 253, 255, 0.88));
  box-shadow:
    0 20px 34px rgba(41, 130, 141, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

.toolbar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.content-table {
  position: relative;
  z-index: 1;
  margin-bottom: 10px;
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

::v-deep .el-table tr {
  background-color: rgba(255, 255, 255, 0.9);
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: rgba(230, 255, 249, 0.7);
}

::v-deep .el-input__inner,
::v-deep .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 249, 255, 0.94));
  color: #355161;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

::v-deep .el-input__inner:focus {
  border-color: #25ddbf;
  box-shadow:
    0 0 0 4px rgba(37, 221, 191, 0.12),
    0 12px 22px rgba(39, 182, 194, 0.12);
}

::v-deep .el-form-item__label {
  color: #456270;
  font-weight: 600;
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
}
</style>
