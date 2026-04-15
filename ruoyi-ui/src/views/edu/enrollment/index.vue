<template>
  <div class="app-container enrollment-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Enrollment Tracker</span>
        <h1>报名记录</h1>
        <p>集中查看学生报名状态、课程归属、教师安排与学习记录，帮助教师、家长和管理员快速跟踪课后服务参与情况。</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>报名总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>已完成</span>
          <strong>{{ finishedCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <el-form :inline="true" :model="queryParams" size="small" v-show="showSearch" class="query-form">
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
            <el-option label="已报名" value="0" />
            <el-option label="已完成" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search" @click="getList">搜索</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </section>

    <el-table v-loading="loading" :data="enrollmentList" class="content-table">
      <el-table-column label="课程" prop="courseName" min-width="150" />
      <el-table-column label="学生" prop="studentName" width="120" />
      <el-table-column label="家长" prop="parentName" width="120" />
      <el-table-column label="教师" prop="teacherName" width="120" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'warning'">{{ scope.row.status === '1' ? '已完成' : '已报名' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学习记录" prop="learningRecord" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button v-hasPermi="['edu:enrollment:edit']" size="mini" type="text" @click="handleUpdate(scope.row)">维护</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="维护学习记录" :visible.sync="open" width="620px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">已报名</el-radio>
            <el-radio label="1">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学习记录">
          <el-input v-model="form.learningRecord" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="互动总结">
          <el-input v-model="form.interactionSummary" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEnrollment, updateEnrollment } from '@/api/edu/enrollment'

export default {
  name: 'EduEnrollment',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      open: false,
      enrollmentList: [],
      queryParams: { pageNum: 1, pageSize: 10, studentName: undefined, status: undefined },
      form: {}
    }
  },
  computed: {
    finishedCount() {
      return this.enrollmentList.filter(item => item.status === '1').length
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listEnrollment(this.queryParams).then(res => {
        this.enrollmentList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, studentName: undefined, status: undefined }
      this.getList()
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.open = true
    },
    submitForm() {
      updateEnrollment(this.form).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.open = false
        this.getList()
      })
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(103, 216, 219, 0.18);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(239, 253, 255, 0.88));
  box-shadow:
    0 20px 34px rgba(41, 130, 141, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

.query-form {
  flex: 1;
}

.content-table {
  position: relative;
  z-index: 1;
  margin-bottom: 10px;
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

  .toolbar-panel {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
