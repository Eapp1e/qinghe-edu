<template>
  <div class="app-container platform-page">
    <section class="page-hero">
      <div>
        <span class="hero-badge">Platform Notice</span>
        <h1>平台通知中心</h1>
        <p class="summary">
          统一发布课程安排、报名提醒、家校通知和活动公告，帮助学校建立清晰、规范的课后服务信息流。
        </p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>通知总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>已发布</span>
          <strong>{{ publishedCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-card">
      <el-form ref="queryForm" :model="queryParams" inline size="small">
        <el-form-item label="通知标题">
          <el-input v-model="queryParams.noticeTitle" placeholder="请输入通知标题" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="queryParams.createBy" placeholder="请输入发布人账号" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="queryParams.noticeType" clearable placeholder="全部类型">
            <el-option v-for="dict in dict.type.sys_notice_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态" class="status-select">
          <el-select v-model="queryParams.status" clearable placeholder="全部状态">
            <el-option v-for="dict in dict.type.sys_notice_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-tooltip content="查询" placement="top">
            <el-button type="primary" size="small" class="toolbar-icon-btn" icon="el-icon-search" @click="getList" />
          </el-tooltip>
          <el-tooltip content="重置筛选" placement="top">
            <el-button size="small" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetQuery" />
          </el-tooltip>
        </el-form-item>
      </el-form>

      <div v-if="canCreateNotice" class="quick-actions">
        <el-button type="primary" plain size="small" class="toolbar-gradient-btn" @click="handleAdd">新建通知</el-button>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>
    </section>

    <el-table v-loading="loading" :data="noticeList" class="content-table">
      <el-table-column label="通知标题" min-width="240" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="handlePreview(scope.row)">{{ scope.row.noticeTitle }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="通知类型" width="110" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_type" :value="scope.row.noticeType" />
        </template>
      </el-table-column>
      <el-table-column label="发布状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布人" prop="createBy" width="120" />
      <el-table-column label="发布时间" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handlePreview(scope.row)">{{ previewActionText }}</el-button>
          <el-button v-if="canViewReadUsers" size="mini" type="text" @click="handleReadUsers(scope.row)">已读情况</el-button>
          <el-button v-if="canEditNotice" size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-if="canDeleteNotice" size="mini" type="text" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      :total="total"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="820px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="88px">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="通知标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" maxlength="80" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择通知类型">
                <el-option v-for="dict in dict.type.sys_notice_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_notice_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通知内容" prop="noticeContent">
              <editor v-model="form.noticeContent" :min-height="220" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="通知详情" :visible.sync="previewOpen" width="760px" append-to-body>
      <div class="preview-panel">
        <div class="preview-header">
          <h2>{{ previewData.noticeTitle || '-' }}</h2>
          <div class="preview-meta">
            <span>类型：{{ noticeTypeLabel(previewData.noticeType) }}</span>
            <span>发布人：{{ previewData.createBy || '-' }}</span>
            <span>时间：{{ parseTime(previewData.createTime) || '-' }}</span>
          </div>
        </div>
        <div class="preview-content" v-html="previewData.noticeContent || '<p>暂无内容</p>'"></div>
      </div>
    </el-dialog>

    <el-dialog title="通知已读情况" :visible.sync="readOpen" width="720px" append-to-body>
      <el-table v-loading="readLoading" :data="readUserList">
        <el-table-column label="账号" prop="userName" min-width="120" />
        <el-table-column label="姓名" prop="nickName" min-width="120" />
        <el-table-column label="角色" min-width="120">
          <template slot-scope="scope">
            {{ resolveRole(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="阅读时间" min-width="170">
          <template slot-scope="scope">
            {{ parseTime(scope.row.readTime) || '-' }}
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!readLoading && !readUserList.length" class="empty-readers">
        暂无阅读记录，通知发布后将自动累计用户已读数据。
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addNotice, delNotice, getNotice, listNotice, listNoticeReadUsers, markNoticeRead, updateNotice } from '@/api/system/notice'

export default {
  name: 'EduPlatformNoticePage',
  dicts: ['sys_notice_status', 'sys_notice_type'],
  data() {
    return {
      loading: false,
      total: 0,
      noticeList: [],
      open: false,
      title: '',
      previewOpen: false,
      previewData: {},
      readOpen: false,
      readLoading: false,
      readUserList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: undefined,
        createBy: undefined,
        noticeType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        noticeTitle: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
        noticeType: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
        noticeContent: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    publishedCount() {
      return this.noticeList.filter(item => item.status === '0').length
    },
    previewActionText() {
      if (this.$auth.hasRole('edu_student') || this.$auth.hasRole('edu_parent')) {
        return '查看'
      }
      return '预览'
    },
    canViewReadUsers() {
      return this.$auth.hasRole('admin') || this.$auth.hasRole('edu_admin') || this.$auth.hasRole('edu_teacher')
    },
    canEditNotice() {
      return this.$auth.hasRole('admin') || this.$auth.hasRole('edu_admin') || this.$auth.hasRole('edu_teacher')
    },
    canCreateNotice() {
      return this.$auth.hasRole('admin') || this.$auth.hasRole('edu_admin') || this.$auth.hasRole('edu_teacher')
    },
    canDeleteNotice() {
      return this.$auth.hasRole('admin') || this.$auth.hasRole('edu_admin') || this.$auth.hasRole('edu_teacher')
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        noticeId: undefined,
        noticeTitle: '',
        noticeType: '1',
        noticeContent: '',
        status: '0'
      }
      this.resetForm('form')
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.queryParams.pageSize = 10
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.title = '新建平台通知'
      this.open = true
    },
    handleUpdate(row) {
      this.reset()
      getNotice(row.noticeId).then(response => {
        this.form = response.data
        this.title = '编辑平台通知'
        this.open = true
      })
    },
    handlePreview(row) {
      markNoticeRead(row.noticeId).catch(() => {})
      getNotice(row.noticeId).then(response => {
        this.previewData = response.data || {}
        this.previewOpen = true
      })
    },
    handleReadUsers(row) {
      this.readOpen = true
      this.readLoading = true
      this.readUserList = []
      listNoticeReadUsers({ pageNum: 1, pageSize: 100, noticeId: row.noticeId }).then(response => {
        this.readUserList = response.rows || []
      }).finally(() => {
        this.readLoading = false
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.noticeId ? updateNotice(this.form) : addNotice(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.noticeId ? '通知更新成功' : '通知发布成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除通知“${row.noticeTitle}”吗？`).then(() => {
        return delNotice(row.noticeId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    cancel() {
      this.open = false
    },
    noticeTypeLabel(value) {
      const match = (this.dict.type.sys_notice_type || []).find(item => item.value === value)
      return match ? match.label : '-'
    },
    resolveRole(row) {
      return row.roleName || row.roleNames || '-'
    }
  }
}
</script>

<style scoped>
.platform-page {
  position: relative;
  display: grid;
  gap: 18px;
  padding-top: 6px;
  overflow: hidden;
}

.platform-page::before,
.platform-page::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(8px);
  pointer-events: none;
}

.platform-page::before {
  top: -60px;
  right: 5%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(32, 231, 188, 0.18), transparent 68%);
}

.platform-page::after {
  left: -70px;
  bottom: 12%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(92, 186, 255, 0.15), transparent 70%);
}

.page-hero {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 18px;
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

.hero-badge {
  display: inline-flex;
  align-items: center;
  margin: 0 0 12px;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(32, 224, 182, 0.14);
  color: #8ff6dc;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
  text-transform: none;
  box-shadow: 0 10px 20px rgba(12, 34, 40, 0.2);
}

.page-hero h1 {
  margin: 0 0 10px;
  color: #ffffff;
  font-size: 32px;
}

.summary {
  max-width: 720px;
  margin: 0;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.9;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  min-width: 260px;
}

.stat-card,
.guide-item,
.toolbar-card,
.content-table,
.preview-panel {
  border-radius: 22px;
}

.stat-card,
.guide-item {
  padding: 18px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  border: 1px solid rgba(129, 224, 224, 0.14);
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16);
  min-height: 118px;
}

.stat-card span {
  display: block;
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 8px;
  color: #ffffff;
  font-size: 28px;
}

.toolbar-card {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px 4px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow:
    0 22px 40px rgba(41, 130, 141, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 14px;
}

.content-table {
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.preview-panel {
  border: 1px solid rgba(111, 217, 217, 0.16);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 252, 255, 0.96));
  box-shadow: 0 20px 40px rgba(41, 130, 141, 0.08);
  padding: 22px;
}

.preview-header h2 {
  margin: 0 0 10px;
  color: #163643;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 18px;
  color: #6f8794;
  font-size: 13px;
}

.preview-content {
  color: #526676;
  line-height: 1.9;
}

.empty-readers {
  padding: 24px 8px 6px;
  text-align: center;
  color: #6f8794;
}

.danger-text {
  color: #ef5753;
}

::v-deep .el-form-item__label {
  color: #456270;
  font-weight: 600;
}

::v-deep .el-input__inner,
::v-deep .el-select .el-input__inner,
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

::v-deep .status-select .el-input__inner {
  width: 148px;
}

::v-deep .el-table {
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

::v-deep .el-table th {
  background: #d1d5db !important;
  color: #374151 !important;
}

::v-deep .el-table th:first-child .cell,
::v-deep .el-table td:first-child .cell {
  padding-left: 18px;
}

::v-deep .el-table tr {
  background-color: rgba(255, 255, 255, 0.9);
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: #eef1f4 !important;
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

@media (max-width: 992px) {
  .page-hero,
  .toolbar-card {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-stats {
    grid-template-columns: 1fr;
  }
}
</style>
