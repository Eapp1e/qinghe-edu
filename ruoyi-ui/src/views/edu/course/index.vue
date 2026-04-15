<template>
  <div class="app-container course-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Course Center</span>
        <h1>课程中心</h1>
        <p>集中展示课后课程、兴趣班和教师课程安排，支持课程发布、课程报名以及 AI 辅助通知与教学建议生成。</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>课程总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>开放课程</span>
          <strong>{{ openCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <el-form :inline="true" :model="queryParams" size="small" class="query-form" v-show="showSearch">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="queryParams.category" placeholder="请输入课程分类" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search" @click="getList">搜索</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar-actions">
        <el-button v-hasPermi="['edu:course:add']" type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd">发布课程</el-button>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>
    </section>

    <el-table v-loading="loading" :data="courseList" class="content-table">
      <el-table-column label="课程" prop="courseName" min-width="150" />
      <el-table-column label="分类" prop="category" width="120" />
      <el-table-column label="教师" prop="teacherName" width="120" />
      <el-table-column label="时间" width="180">
        <template slot-scope="scope">{{ scope.row.weekDay }} {{ scope.row.startTime }}-{{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column label="地点" prop="campus" width="140" />
      <el-table-column label="容量" width="100">
        <template slot-scope="scope">{{ scope.row.currentCapacity }}/{{ scope.row.maxCapacity }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '开放中' : '已关闭' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="320">
        <template slot-scope="scope">
          <el-button v-hasPermi="['edu:course:edit']" size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-hasPermi="['edu:course:enroll']" size="mini" type="text" @click="handleEnroll(scope.row)">报名</el-button>
          <el-button v-hasPermi="['edu:course:edit']" size="mini" type="text" @click="handleNotice(scope.row)">AI 通知</el-button>
          <el-button v-hasPermi="['edu:course:edit']" size="mini" type="text" @click="handleSuggestion(scope.row)">AI 建议</el-button>
          <el-button v-hasPermi="['edu:course:remove']" size="mini" type="text" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
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
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio label="0">开放</el-radio><el-radio label="1">关闭</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="课程简介"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item></el-col>
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
import { addCourse, delCourse, enrollCourse, generateCourseNotice, generateTeachingSuggestion, listCourse, updateCourse } from '@/api/edu/course'
import { listMyChildren } from '@/api/edu/student'

export default {
  name: 'EduCourse',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      courseList: [],
      childrenList: [],
      open: false,
      title: '',
      queryParams: { pageNum: 1, pageSize: 10, courseName: undefined, category: undefined },
      form: {},
      rules: {
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        category: [{ required: true, message: '请输入课程分类', trigger: 'blur' }]
      }
    }
  },
  computed: {
    openCount() {
      return this.courseList.filter(item => item.status === '0').length
    }
  },
  created() {
    this.getList()
    this.getChildren()
  },
  methods: {
    getList() {
      this.loading = true
      listCourse(this.queryParams).then(res => {
        this.courseList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    getChildren() {
      if (!this.$auth.hasRole('edu_parent')) {
        return
      }
      listMyChildren().then(res => {
        this.childrenList = res.data || []
      })
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, courseName: undefined, category: undefined }
      this.getList()
    },
    reset() {
      this.form = { courseId: undefined, maxCapacity: 30, status: '0' }
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
          this.$modal.msgSuccess('操作成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除课程“' + row.courseName + '”吗？').then(() => delCourse(row.courseId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    },
    handleEnroll(row) {
      let studentUserId
      if (this.$auth.hasRole('edu_parent')) {
        if (this.childrenList.length === 0) {
          this.$modal.msgError('请先维护孩子档案后再报名')
          return
        }
        if (this.childrenList.length > 1) {
          this.$modal.msgError('当前账号关联了多个孩子，请先保留一个孩子档案或按需扩展选择功能')
          return
        }
        studentUserId = this.childrenList[0].studentUserId
      }
      enrollCourse(row.courseId, studentUserId).then(() => {
        this.$modal.msgSuccess('报名成功')
        this.getList()
      })
    },
    handleNotice(row) {
      generateCourseNotice(row.courseId).then(res => {
        this.$alert(res.data, 'AI 生成通知', { dangerouslyUseHTMLString: false })
      })
    },
    handleSuggestion(row) {
      generateTeachingSuggestion(row.courseId).then(res => {
        this.$alert(res.data, 'AI 教学建议', { dangerouslyUseHTMLString: false })
      })
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
  border: 1px solid rgba(94, 222, 213, 0.2);
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
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(233, 250, 255, 0.82));
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
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(103, 216, 219, 0.18);
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(239, 253, 255, 0.88));
  box-shadow:
    0 20px 34px rgba(41, 130, 141, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

.query-form {
  flex: 1;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.content-table {
  position: relative;
  z-index: 1;
  margin-bottom: 10px;
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

::v-deep .el-dialog {
  border-radius: 28px;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 252, 255, 0.96));
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
