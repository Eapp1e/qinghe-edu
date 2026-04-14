<template>
  <div class="platform-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">Admin Console</p>
        <h1>平台通知中心</h1>
        <p class="summary">
          统一发布课程开班、报名提醒、活动安排与家校通知，帮助管理员和教师用更清晰的方式组织课后服务信息流。
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
      <div class="guide-item" v-for="item in guideItems" :key="item.title">
        <strong>{{ item.title }}</strong>
        <span>{{ item.desc }}</span>
      </div>
    </section>

    <section class="toolbar-card">
      <el-form ref="queryForm" :model="queryParams" inline size="small">
        <el-form-item label="通知标题">
          <el-input
            v-model="queryParams.noticeTitle"
            placeholder="如：科学实验班开班通知"
            clearable
            @keyup.enter.native="getList"
          />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input
            v-model="queryParams.createBy"
            placeholder="支持按教师或管理员账号检索"
            clearable
            @keyup.enter.native="getList"
          />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="queryParams.noticeType" clearable placeholder="全部类型">
            <el-option
              v-for="dict in dict.type.sys_notice_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部状态">
            <el-option
              v-for="dict in dict.type.sys_notice_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="getList">查询</el-button>
          <el-button size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="quick-actions">
        <el-button type="primary" plain size="small" @click="handleAdd">发布平台通知</el-button>
      </div>
    </section>

    <el-table v-loading="loading" :data="noticeList" class="content-table">
      <el-table-column label="通知标题" min-width="240" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="handlePreview(scope.row)">
            {{ scope.row.noticeTitle }}
          </el-link>
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
                <el-option
                  v-for="dict in dict.type.sys_notice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_notice_status" :key="dict.value" :label="dict.value">
                  {{ dict.label }}
                </el-radio>
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
        暂无阅读记录，适合答辩时演示通知发布后的联动效果。
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
        { title: '课程通知', desc: '用于发布开班时间、地点调整、教师安排和上课提醒。' },
        { title: '家校提醒', desc: '适合发送报名确认、材料准备、活动签到等课后服务消息。' },
        { title: '答辩展示', desc: '切换不同角色后，可演示顶部通知、已读记录和管理发布流程。' }
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
      this.title = '发布平台通知'
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
      this.reset()
    },
    noticeTypeLabel(value) {
      return this.selectDictLabel(this.dict.type.sys_notice_type, value) || '未分类'
    },
    resolveRole(row) {
      if (row.roleName) {
        return row.roleName
      }
      if (row.roleKey) {
        if (row.roleKey === 'edu_admin') return '平台管理员'
        if (row.roleKey === 'edu_teacher') return '教师'
        if (row.roleKey === 'edu_parent') return '家长'
        if (row.roleKey === 'edu_student') return '学生'
      }
      return '平台用户'
    }
  }
}
</script>

<style lang="scss" scoped>
.platform-page {
  padding-bottom: 8px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 18px;
  padding: 24px 26px;
  border-radius: 20px;
  background: linear-gradient(135deg, #fff7ee 0%, #fffdf7 56%, #edf6ff 100%);
  border: 1px solid #f1dfc7;
}

.eyebrow {
  margin: 0 0 10px;
  color: #b17322;
  font-size: 12px;
  letter-spacing: 1.2px;
  text-transform: uppercase;
}

.page-hero h1 {
  margin: 0;
  color: #654116;
  font-size: 28px;
}

.summary {
  max-width: 700px;
  margin: 12px 0 0;
  color: #7b6650;
  line-height: 1.8;
}

.hero-stats {
  display: grid;
  gap: 12px;
  min-width: 180px;
}

.stat-card {
  padding: 16px 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 14px 24px rgba(95, 60, 18, 0.06);
}

.stat-card span {
  display: block;
  color: #8c6a44;
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 8px;
  color: #5f3c12;
  font-size: 24px;
}

.guide-panel {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.guide-item,
.toolbar-card {
  padding: 18px 20px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid #f1eadc;
  box-shadow: 0 10px 24px rgba(95, 60, 18, 0.05);
}

.guide-item strong {
  display: block;
  margin-bottom: 8px;
  color: #6d4715;
  font-size: 15px;
}

.guide-item span {
  color: #7b6650;
  line-height: 1.7;
}

.toolbar-card {
  margin-bottom: 18px;
}

.quick-actions {
  display: flex;
  justify-content: flex-end;
}

.content-table {
  padding: 8px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid #f2ebdf;
  box-shadow: 0 10px 24px rgba(95, 60, 18, 0.05);
}

.danger-text {
  color: #d84a4a;
}

.preview-panel {
  min-height: 240px;
}

.preview-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #efe7d9;
}

.preview-header h2 {
  margin: 0;
  color: #5f3c12;
  font-size: 24px;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
  color: #8a7254;
  font-size: 13px;
}

.preview-content {
  padding-top: 18px;
  color: #5b4e3f;
  line-height: 1.8;
}

.empty-readers {
  padding: 28px 0 8px;
  color: #8c7657;
  text-align: center;
}

@media (max-width: 960px) {
  .page-hero,
  .guide-panel {
    grid-template-columns: 1fr;
    display: grid;
  }

  .hero-stats {
    min-width: 0;
  }
}
</style>
