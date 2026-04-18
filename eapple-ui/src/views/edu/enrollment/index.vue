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
          <span>&#25253;&#21517;&#24635;&#25968;</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>&#24050;&#23436;&#25104;</span>
          <strong>{{ finishedCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <el-form :inline="true" :model="queryParams" size="small" v-show="showSearch" class="query-form">
        <el-form-item :label="'\u5b66\u751f\u59d3\u540d'">
          <el-input v-model="queryParams.studentName" :placeholder="'\u8bf7\u8f93\u5165\u5b66\u751f\u59d3\u540d'" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item :label="'\u72b6\u6001'">
          <el-select v-model="queryParams.status" clearable :placeholder="'\u8bf7\u9009\u62e9\u72b6\u6001'">
            <el-option :label="'\u5df2\u62a5\u540d'" value="0" />
            <el-option :label="'\u5df2\u5b8c\u6210'" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search" @click="getList">&#25628;&#32034;</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">&#37325;&#32622;</el-button>
        </el-form-item>
      </el-form>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </section>

    <el-table v-loading="loading" :data="enrollmentList" class="content-table">
      <el-table-column :label="'\u8bfe\u7a0b'" prop="courseName" min-width="150" />
      <el-table-column :label="'\u5b66\u751f'" prop="studentName" width="120" />
      <el-table-column :label="'\u5bb6\u957f'" prop="parentName" width="120" />
      <el-table-column :label="'\u6559\u5e08'" prop="teacherName" width="120" />
      <el-table-column :label="'\u72b6\u6001'" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'warning'">{{ scope.row.status === '1' ? '\u5df2\u5b8c\u6210' : '\u5df2\u62a5\u540d' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="'\u5b66\u4e60\u8bb0\u5f55'" prop="learningRecord" min-width="220" show-overflow-tooltip />
      <el-table-column v-if="showActionColumn" :label="'\u64cd\u4f5c'" width="140">
        <template slot-scope="scope">
          <el-button v-if="canEditEnrollment" size="mini" type="text" @click="handleUpdate(scope.row)">{{ actionButtonText }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="open" width="620px">
      <el-form :model="form" label-width="90px">
        <el-form-item :label="'\u72b6\u6001'">
          <el-radio-group v-model="form.status">
            <el-radio label="0">{{ '\u5df2\u62a5\u540d' }}</el-radio>
            <el-radio label="1">{{ '\u5df2\u5b8c\u6210' }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="'\u5b66\u4e60\u8bb0\u5f55'">
          <el-input v-model="form.learningRecord" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item :label="'\u4e92\u52a8\u603b\u7ed3'">
          <el-input v-model="form.interactionSummary" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">&#30830;&#23450;</el-button>
        <el-button @click="open = false">&#21462;&#28040;</el-button>
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
    roleKeys() {
      return this.$store.getters.roles || []
    },
    isStudentOrParent() {
      return this.roleKeys.includes('edu_student') || this.roleKeys.includes('edu_parent')
    },
    pageTitle() {
      return this.isStudentOrParent ? '\u5b66\u4e60\u8bb0\u5f55' : '\u62a5\u540d\u8bb0\u5f55'
    },
    pageBadge() {
      return this.isStudentOrParent ? 'Learning Record' : 'Enrollment Tracker'
    },
    pageDescription() {
      if (this.isStudentOrParent) {
        return '\u96c6\u4e2d\u67e5\u770b\u8bfe\u7a0b\u53c2\u4e0e\u72b6\u6001\u3001\u5b66\u4e60\u8bb0\u5f55\u4e0e\u6210\u957f\u53cd\u9988\uff0c\u5e2e\u52a9\u5b66\u751f\u548c\u5bb6\u957f\u66f4\u6e05\u6670\u5730\u4e86\u89e3\u8bfe\u540e\u670d\u52a1\u5b66\u4e60\u8fdb\u5c55\u3002'
      }
      return '\u96c6\u4e2d\u67e5\u770b\u62a5\u540d\u72b6\u6001\u3001\u8bfe\u7a0b\u5f52\u5c5e\u3001\u6559\u5e08\u5b89\u6392\u4e0e\u5b66\u4e60\u8bb0\u5f55\uff0c\u5feb\u901f\u8ddf\u8e2a\u8bfe\u540e\u670d\u52a1\u53c2\u4e0e\u60c5\u51b5\u3002'
    },
    finishedCount() {
      return this.enrollmentList.filter(item => item.status === '1').length
    },
    showActionColumn() {
      return !this.roleKeys.includes('edu_student')
    },
    actionButtonText() {
      if (this.roleKeys.includes('edu_parent')) {
        return '\u586b\u5199\u5b66\u4e60\u8bb0\u5f55'
      }
      return '\u7ef4\u62a4'
    },
    canEditEnrollment() {
      return this.roleKeys.includes('admin') || this.roleKeys.includes('edu_admin') || this.roleKeys.includes('edu_teacher') || this.roleKeys.includes('edu_parent')
    },
    dialogTitle() {
      return this.roleKeys.includes('edu_parent') ? '\u586b\u5199\u5b66\u4e60\u8bb0\u5f55' : '\u7ef4\u62a4\u5b66\u4e60\u8bb0\u5f55'
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
        this.$modal.msgSuccess('\u4fdd\u5b58\u6210\u529f')
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
