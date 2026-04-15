<template>
  <div class="platform-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">Notice Center</p>
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

    <section class="guide-panel">
      <div v-for="item in guideItems" :key="item.title" class="guide-item">
        <strong>{{ item.title }}</strong>
        <span>{{ item.desc }}</span>
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
        <el-form-item label="发布状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部状态">
            <el-option v-for="dict in dict.type.sys_notice_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="getList">查询</el-button>
          <el-button size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="quick-actions">
        <el-button type="primary" plain size="small" @click="handleAdd">新建通知</el-button>
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
          <el-button size="mini" type="text" @click="handlePreview(scope.row)">预览</el-button>
          <el-button size="mini" type="text" @click="handleReadUsers(scope.row)">已读情况</el-button>
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
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
import { addNotice, delNotice, getNotice, listNotice, listNoticeReadUsers, updateNotice } from '@/api/system/notice'

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
      guideItems: [
        { title: '课程通知', desc: '用于发布开课时间、教室调整、授课教师安排和上课提醒。' },
        { title: '报名提醒', desc: '适用于发送报名确认、材料准备、活动签到等课后服务消息。' },
        { title: '运营联动', desc: '切换不同角色后，可查看顶部通知、已读记录和管理发布流程。' }
      ],
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
      const role = (row.roles || [])[0]
      return role ? role.roleName : '-'
    }
  }
}
</script>

<style scoped>
.platform-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 24px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #fff9ef 0%, #eff5e8 100%);
  box-shadow: 0 20px 34px rgba(118, 126, 98, 0.08);
}

.eyebrow {
  margin: 0 0 12px;
  color: #73905f;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.page-hero h1 {
  margin: 0 0 10px;
  color: #293d29;
  font-size: 32px;
}

.summary {
  max-width: 720px;
  margin: 0;
  color: #6b7869;
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
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(205, 216, 190, 0.45);
}

.stat-card span {
  display: block;
  color: #788270;
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 8px;
  color: #2d402d;
  font-size: 28px;
}

.guide-panel {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.guide-item strong {
  display: block;
  margin-bottom: 8px;
  color: #314431;
}

.guide-item span {
  color: #6d7868;
  line-height: 1.8;
  font-size: 14px;
}

.toolbar-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px 4px;
  background: linear-gradient(180deg, rgba(255, 252, 246, 0.95), rgba(246, 249, 241, 0.95));
}

.quick-actions {
  padding-bottom: 14px;
}

.content-table {
  overflow: hidden;
}

.preview-header h2 {
  margin: 0 0 10px;
  color: #2b3d2b;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 18px;
  color: #788271;
  font-size: 13px;
}

.preview-content {
  color: #4d584d;
  line-height: 1.9;
}

.empty-readers {
  padding: 24px 8px 6px;
  text-align: center;
  color: #7c8376;
}

.danger-text {
  color: #dd5b4b;
}

@media (max-width: 992px) {
  .page-hero,
  .toolbar-card {
    flex-direction: column;
    align-items: stretch;
  }

  .guide-panel,
  .hero-stats {
    grid-template-columns: 1fr;
  }
}
</style>
