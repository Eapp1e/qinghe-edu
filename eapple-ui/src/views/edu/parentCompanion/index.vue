<template>
  <div class="app-container companion-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Parent Companion</span>
        <h1>家长陪学</h1>
        <p>{{ pageDescription }}</p>
      </div>
      <div class="hero-stats hero-stats--compact">
        <div class="stat-card">
          <span>{{ isAdminView ? '建议记录' : '绑定孩子' }}</span>
          <strong>{{ isAdminView ? historyTotal : children.length }}</strong>
        </div>
        <div class="stat-card">
          <span>课堂资源</span>
          <strong>{{ resources.length }}</strong>
        </div>
      </div>
    </section>

    <section v-if="isParentView" class="ai-recommend-panel">
      <div class="ai-recommend-head">
        <div>
          <h3>AI 诊断建议</h3>
          <p>结合孩子学习记录、作业问答与家长近期关注，生成更具体的沟通和陪伴办法。</p>
        </div>
        <el-button class="recommend-trigger" type="primary" icon="el-icon-magic-stick" :loading="aiLoading" @click="handleDiagnosis">
          {{ aiLoading ? 'AI 分析中...' : '生成建议' }}
        </el-button>
      </div>
      <el-form :inline="true" class="diagnosis-form" size="small">
        <el-form-item label="孩子">
          <el-select v-model="diagnosisForm.studentUserId" class="child-select" placeholder="选择孩子">
            <el-option v-for="item in children" :key="item.studentUserId" :label="item.studentName" :value="item.studentUserId" />
          </el-select>
        </el-form-item>
        <el-form-item label="近期关注" class="concern-item">
          <el-input
            v-model="diagnosisForm.concern"
            class="concern-input"
            placeholder="例如：写作业拖拉、沟通容易顶嘴、阅读坚持不稳定"
            clearable
            @keyup.enter.native="handleDiagnosis"
          />
        </el-form-item>
      </el-form>
    </section>

    <section class="history-panel">
      <div class="panel-head">
        <h3>历史建议记录</h3>
      </div>

      <section class="toolbar-panel history-toolbar">
        <div class="toolbar-main">
          <el-form :inline="true" class="query-form" size="small" @submit.native.prevent>
            <el-form-item v-if="isAdminView" label="孩子">
              <el-input v-model="historySearch.studentName" clearable placeholder="输入孩子姓名" @keyup.enter.native="handleHistorySearch" />
            </el-form-item>
            <el-form-item label="近期关注">
              <el-input v-model="historySearch.concern" clearable placeholder="如：阅读、作业、沟通" @keyup.enter.native="handleHistorySearch" />
            </el-form-item>
            <el-form-item label="状态" class="status-select">
              <el-select v-model="historySearch.status" clearable placeholder="全部状态">
                <el-option label="成功" value="success" />
                <el-option label="失败" value="failed" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-tooltip content="查询" placement="top">
                <el-button type="primary" size="mini" class="toolbar-icon-btn" icon="el-icon-search" @click="handleHistorySearch" />
              </el-tooltip>
              <el-tooltip content="重置筛选" placement="top">
                <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetHistorySearch" />
              </el-tooltip>
            </el-form-item>
          </el-form>
        </div>
        <div class="toolbar-actions">
          <el-button size="mini" class="toolbar-plain-btn" icon="el-icon-download" @click="handleExportHistory">导出</el-button>
          <el-tooltip content="刷新" placement="top">
            <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-refresh" @click="getDiagnosisHistory" />
          </el-tooltip>
        </div>
      </section>

      <section class="table-section-card">
        <el-table v-loading="historyLoading" :data="historyList" class="content-table">
          <el-table-column v-if="isAdminView" label="孩子" min-width="110">
            <template slot-scope="scope">{{ extractStudentName(scope.row.promptContent) }}</template>
          </el-table-column>
          <el-table-column label="近期关注" min-width="150">
            <template slot-scope="scope">{{ extractConcern(scope.row.promptContent) }}</template>
          </el-table-column>
          <el-table-column v-if="isAdminView" label="家长账号" prop="userName" width="130" />
          <el-table-column label="生成时间" prop="createTime" width="170" />
          <el-table-column label="建议内容" min-width="300">
            <template slot-scope="scope">
              <div class="history-content">{{ getHistoryPreview(scope.row.responseContent || scope.row.errorMessage) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="92">
            <template slot-scope="scope">
              <el-tag size="small" :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="170" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="showHistoryDetail(scope.row)">查看完整内容</el-button>
              <el-button type="text" size="mini" class="danger-text" @click="handleDeleteLog(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="historyTotal > 0" :total="historyTotal" :page.sync="historyQuery.pageNum" :limit.sync="historyQuery.pageSize" @pagination="getDiagnosisHistory" />
      </section>
    </section>

    <section class="toolbar-panel resource-toolbar">
      <div class="toolbar-row">
        <div>
          <h3 class="resource-title">家长课堂</h3>
        </div>
        <el-button v-if="isAdminView" class="resource-manage-btn" icon="el-icon-plus" @click="openResourceDialog">添加资源</el-button>
      </div>
      <div class="toolbar-filters">
        <button v-for="item in categoryOptions" :key="item.value" type="button" :class="['filter-chip', { active: activeCategory === item.value }]" @click="activeCategory = item.value">
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="resource-grid resource-grid--top">
      <article v-for="item in filteredResources" :key="item.title" class="resource-card">
        <div class="resource-head">
          <div>
            <span class="resource-source">{{ item.source }}</span>
            <h3>{{ item.title }}</h3>
          </div>
          <span :class="['resource-type', item.typeClass]">{{ item.typeLabel }}</span>
        </div>
        <p class="resource-desc">{{ item.description }}</p>
        <div class="resource-meta">
          <span>{{ item.category }}</span>
          <span>{{ item.duration }}</span>
          <span>{{ item.freeText }}</span>
        </div>
        <div class="resource-tags">
          <span v-for="tag in item.tags" :key="tag">{{ tag }}</span>
        </div>
        <button type="button" class="resource-link" @click="openLink(item.link)">立即进入</button>
      </article>
    </section>

    <el-dialog :title="historyDialogTitle" :visible.sync="historyDialogOpen" width="760px" append-to-body>
      <div v-if="historyDialogShowMeta" class="history-detail-meta">
        <span>{{ historyDialogStudent }}</span>
        <span>{{ historyDialogConcern }}</span>
        <el-tag size="small" :type="historyDialogStatusType">{{ historyDialogStatus }}</el-tag>
      </div>
      <div class="history-detail-body ai-rich-content" v-html="historyDialogHtml"></div>
      <div slot="footer">
        <el-button @click="historyDialogOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog v-if="isAdminView" title="添加家长课堂资源" :visible.sync="resourceDialogOpen" width="720px" append-to-body>
      <el-form ref="resourceForm" :model="resourceForm" :rules="resourceRules" label-width="96px" class="resource-form">
        <el-row :gutter="18">
          <el-col :span="12"><el-form-item label="资源标题" prop="title"><el-input v-model="resourceForm.title" placeholder="请输入资源标题" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源来源" prop="source"><el-input v-model="resourceForm.source" placeholder="如：教育部平台 / 专家讲座" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="资源类型" prop="sourceType">
              <el-select v-model="resourceForm.sourceType" class="full-width" placeholder="请选择资源类型">
                <el-option label="官方平台" value="official" />
                <el-option label="专家微课" value="video" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="category">
              <el-select v-model="resourceForm.category" class="full-width" placeholder="请选择分类">
                <el-option v-for="item in categoryOptions.filter(item => item.value !== 'all')" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="资源描述" prop="description"><el-input v-model="resourceForm.description" type="textarea" :rows="3" placeholder="请输入资源简介" /></el-form-item>
        <el-row :gutter="18">
          <el-col :span="12"><el-form-item label="链接地址" prop="link"><el-input v-model="resourceForm.link" placeholder="请输入可跳转链接" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学习时长" prop="duration"><el-input v-model="resourceForm.duration" placeholder="如：10-20分钟 / 专题合集" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="18">
          <el-col :span="12"><el-form-item label="展示文案" prop="freeText"><el-input v-model="resourceForm.freeText" placeholder="如：免费使用 / 免费检索" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源标签" prop="tagsText"><el-input v-model="resourceForm.tagsText" placeholder="多个标签用中文逗号分隔" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="resourceDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitResource">保存资源</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMyChildren } from '@/api/edu/student'
import { generateParentDiagnosis } from '@/api/edu/ai'
import { delAiLog, delMyAiLog, listAiLog, listMyAiLog } from '@/api/edu/aiLog'
import { renderAiContentHtml, sanitizeAiDisplayContent } from '@/utils/aiContent'

const COMPANION_RESOURCE_STORAGE_KEY = 'edu.parentCompanion.customResources'

export default {
  name: 'EduParentCompanion',
  data() {
    return {
      activeCategory: 'all',
      children: [],
      aiLoading: false,
      historyLoading: false,
      diagnosisResult: '',
      historyList: [],
      historyTotal: 0,
      historySearch: { studentName: '', concern: '', status: '' },
      historyQuery: { pageNum: 1, pageSize: 5, businessType: 'parent_diagnosis' },
      historyDialogOpen: false,
      historyDialogTitle: '建议完整内容',
      historyDialogShowMeta: true,
      historyDialogStudent: '',
      historyDialogConcern: '',
      historyDialogStatus: '',
      historyDialogStatusType: 'info',
      historyDialogHtml: '',
      resourceDialogOpen: false,
      diagnosisForm: { studentUserId: undefined, concern: '' },
      resourceForm: {
        title: '',
        source: '',
        sourceType: 'official',
        category: '',
        description: '',
        link: '',
        duration: '专题合集',
        freeText: '免费使用',
        tagsText: ''
      },
      resourceRules: {
        title: [{ required: true, message: '请填写资源标题', trigger: 'blur' }],
        source: [{ required: true, message: '请填写资源来源', trigger: 'blur' }],
        sourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        description: [{ required: true, message: '请填写资源描述', trigger: 'blur' }],
        link: [{ required: true, message: '请填写链接地址', trigger: 'blur' }],
        duration: [{ required: true, message: '请填写学习时长', trigger: 'blur' }],
        freeText: [{ required: true, message: '请填写展示文案', trigger: 'blur' }]
      },
      categoryOptions: [
        { label: '全部类型', value: 'all' },
        { label: '亲子沟通', value: '亲子沟通' },
        { label: '陪伴方法', value: '陪伴方法' },
        { label: '习惯养成', value: '习惯养成' },
        { label: '情绪支持', value: '情绪支持' },
        { label: '家庭教育', value: '家庭教育' }
      ],
      resources: [
        {
          title: '国家中小学智慧教育平台家庭教育',
          source: '教育部平台',
          description: '面向家长提供家庭教育、心理健康和亲子沟通等公开课程与专题内容。',
          category: '家庭教育',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          duration: '专题合集',
          freeText: '免费使用',
          tags: ['家庭教育', '权威课程', '亲子沟通'],
          link: 'https://basic.smartedu.cn/'
        },
        {
          title: '中国教育电视台家庭教育公开课',
          source: '中国教育电视台',
          description: '适合家长了解孩子成长规律、沟通方式和学习习惯培养方法。',
          category: '陪伴方法',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          duration: '公开课',
          freeText: '免费访问',
          tags: ['专家讲座', '成长规律', '沟通方法'],
          link: 'https://www.centv.cn/'
        },
        {
          title: 'Bilibili 亲子沟通微课精选',
          source: 'Bilibili',
          description: '可按孩子阶段检索沟通、陪伴、情绪管理和习惯养成类视频。',
          category: '亲子沟通',
          sourceType: 'video',
          typeLabel: '专家微课',
          typeClass: 'video',
          duration: '10-20分钟',
          freeText: '免费检索',
          tags: ['微课视频', '情绪支持', '陪伴技巧'],
          link: 'https://search.bilibili.com/all?keyword=%E4%BA%B2%E5%AD%90%E6%B2%9F%E9%80%9A%20%E5%AE%B6%E5%BA%AD%E6%95%99%E8%82%B2'
        },
        {
          title: '儿童习惯养成方法合集',
          source: 'Bilibili',
          description: '覆盖阅读、整理、时间管理与自律培养，适合作为亲子任务设计参考。',
          category: '习惯养成',
          sourceType: 'video',
          typeLabel: '专家微课',
          typeClass: 'video',
          duration: '碎片学习',
          freeText: '免费检索',
          tags: ['习惯养成', '时间管理', '亲子约定'],
          link: 'https://search.bilibili.com/all?keyword=%E5%84%BF%E7%AB%A5%E4%B9%A0%E6%83%AF%E5%85%BB%E6%88%90'
        },
        {
          title: '家庭情绪支持与积极倾听',
          source: 'Bilibili',
          description: '围绕孩子情绪表达、亲子冲突降温和积极倾听技巧，适合家长做日常沟通练习。',
          category: '情绪支持',
          sourceType: 'video',
          typeLabel: '专家微课',
          typeClass: 'video',
          duration: '15分钟起',
          freeText: '免费检索',
          tags: ['情绪管理', '积极倾听', '冲突降温'],
          link: 'https://search.bilibili.com/all?keyword=%E5%84%BF%E7%AB%A5%E6%83%85%E7%BB%AA%E7%AE%A1%E7%90%86%20%E4%BA%B2%E5%AD%90%E6%B2%9F%E9%80%9A'
        },
        {
          title: '阅读陪伴与家庭共读方法',
          source: 'Bilibili',
          description: '覆盖亲子共读、阅读兴趣激发和读后交流方法，适合与家庭阅读任务配套使用。',
          category: '陪伴方法',
          sourceType: 'video',
          typeLabel: '专家微课',
          typeClass: 'video',
          duration: '碎片学习',
          freeText: '免费检索',
          tags: ['亲子共读', '阅读兴趣', '表达训练'],
          link: 'https://search.bilibili.com/all?keyword=%E4%BA%B2%E5%AD%90%E5%85%B1%E8%AF%BB%20%E9%98%85%E8%AF%BB%E9%99%AA%E4%BC%B4'
        }
      ]
    }
  },
  computed: {
    isAdminView() {
      return this.$auth.hasRole('admin') || this.$auth.hasRole('edu_admin')
    },
    isParentView() {
      return this.$auth.hasRole('edu_parent')
    },
    pageDescription() {
      return this.isAdminView
        ? '统一维护家长课堂资源，并查看全平台家长陪学 AI 建议记录。'
        : '面向家长的教育建议中心，结合孩子学习记录、作业问答与家庭反馈，给出更具体的沟通和陪伴方法。'
    },
    filteredResources() {
      return this.resources.filter(item => this.activeCategory === 'all' || item.category === this.activeCategory)
    }
  },
  created() {
    this.resources = this.loadResourceList()
    this.getDiagnosisHistory()
    if (this.isParentView) {
      this.getChildren()
    }
  },
  methods: {
    getChildren() {
      listMyChildren().then(res => {
        this.children = res.data || []
        if (!this.diagnosisForm.studentUserId && this.children.length) {
          this.diagnosisForm.studentUserId = this.children[0].studentUserId
        }
      })
    },
    getCustomResources() {
      try {
        const raw = localStorage.getItem(COMPANION_RESOURCE_STORAGE_KEY)
        const parsed = raw ? JSON.parse(raw) : []
        return Array.isArray(parsed) ? parsed : []
      } catch (error) {
        return []
      }
    },
    loadResourceList() {
      return this.resources.slice().concat(this.getCustomResources())
    },
    saveCustomResources(list) {
      localStorage.setItem(COMPANION_RESOURCE_STORAGE_KEY, JSON.stringify(list || []))
    },
    openResourceDialog() {
      this.resourceDialogOpen = true
      this.$nextTick(() => this.resetResourceForm())
    },
    resetResourceForm() {
      this.resourceForm = {
        title: '',
        source: '',
        sourceType: 'official',
        category: '',
        description: '',
        link: '',
        duration: '专题合集',
        freeText: '免费使用',
        tagsText: ''
      }
      if (this.$refs.resourceForm) {
        this.$refs.resourceForm.resetFields()
      }
    },
    submitResource() {
      this.$refs.resourceForm.validate(valid => {
        if (!valid) return
        const resource = {
          title: this.resourceForm.title.trim(),
          source: this.resourceForm.source.trim(),
          sourceType: this.resourceForm.sourceType,
          typeLabel: this.resourceForm.sourceType === 'official' ? '官方平台' : '专家微课',
          typeClass: this.resourceForm.sourceType,
          category: this.resourceForm.category,
          description: this.resourceForm.description.trim(),
          link: this.resourceForm.link.trim(),
          duration: this.resourceForm.duration.trim(),
          freeText: this.resourceForm.freeText.trim(),
          tags: (this.resourceForm.tagsText || '').split(/[，,]/).map(item => item.trim()).filter(Boolean)
        }
        const customResources = this.getCustomResources()
        customResources.unshift(resource)
        this.saveCustomResources(customResources)
        this.resources = this.loadResourceList()
        this.resourceDialogOpen = false
        this.$modal.msgSuccess('资源已添加到家长课堂')
      })
    },
    handleDiagnosis() {
      if (!this.diagnosisForm.studentUserId) {
        this.$modal.msgWarning('请先选择已绑定孩子')
        return
      }
      this.aiLoading = true
      generateParentDiagnosis(this.diagnosisForm).then(res => {
        const content = this.resolveDiagnosisContent(res)
        this.diagnosisResult = content || '暂无可展示内容'
        this.openDiagnosisDialog(this.diagnosisResult)
        this.getDiagnosisHistory()
      }).catch(() => {
        this.$modal.msgWarning('AI 暂时不可用，请稍后再试')
      }).finally(() => {
        this.aiLoading = false
      })
    },
    resolveDiagnosisContent(res) {
      if (!res) {
        return ''
      }
      if (typeof res.data === 'string') {
        return res.data
      }
      if (res.data && typeof res.data === 'object') {
        return res.data.responseContent || res.data.content || res.data.message || res.data.msg || ''
      }
      return res.responseContent || res.content || res.msg || ''
    },
    renderSafeAiHtml(content) {
      const raw = String(content || '').trim()
      const html = renderAiContentHtml(raw)
      if (html && html !== '<p>暂无可展示内容</p>') {
        return html
      }
      return raw ? `<p>${this.escapeHtml(raw).replace(/\n/g, '<br>')}</p>` : '<p>暂无可展示内容</p>'
    },
    escapeHtml(value) {
      return String(value || '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
    },
    openLink(link) {
      window.open(link, '_blank', 'noopener')
    },
    buildHistoryQuery() {
      return {
        ...this.historyQuery,
        studentName: this.isAdminView ? this.historySearch.studentName : '',
        concern: this.historySearch.concern,
        status: this.historySearch.status
      }
    },
    getDiagnosisHistory() {
      this.historyLoading = true
      const request = this.isAdminView ? listAiLog(this.buildHistoryQuery()) : listMyAiLog(this.buildHistoryQuery())
      request.then(res => {
        this.historyList = res.rows || []
        this.historyTotal = res.total || 0
      }).finally(() => {
        this.historyLoading = false
      })
    },
    handleHistorySearch() {
      this.historyQuery.pageNum = 1
      this.getDiagnosisHistory()
    },
    resetHistorySearch() {
      this.historySearch = { studentName: '', concern: '', status: '' }
      this.handleHistorySearch()
    },
    extractStudentName(prompt) {
      const match = (prompt || '').match(/学生：([^，,\n]+)/)
      return match ? match[1].trim() : '--'
    },
    extractConcern(prompt) {
      const match = (prompt || '').match(/家长补充关注：([^\n]+)/)
      const value = match ? match[1].trim() : ''
      return value || '未填写'
    },
    getHistoryPreview(content) {
      const text = sanitizeAiDisplayContent(content || '')
      if (!text) return '暂无可展示内容'
      return text.length > 110 ? `${text.slice(0, 110)}...` : text
    },
    statusTagType(status) {
      if (status === 'success') return 'success'
      if (status === 'failed') return 'danger'
      return 'success'
    },
    formatStatus(status) {
      const map = { success: '成功', failed: '失败', mock: '已生成' }
      return map[status] || '已生成'
    },
    showHistoryDetail(row) {
      const content = row.responseContent || row.errorMessage || '暂无可展示内容'
      this.historyDialogTitle = '建议完整内容'
      this.historyDialogShowMeta = true
      this.historyDialogStudent = this.extractStudentName(row.promptContent)
      this.historyDialogConcern = this.extractConcern(row.promptContent)
      this.historyDialogStatus = this.formatStatus(row.status)
      this.historyDialogStatusType = this.statusTagType(row.status)
      this.historyDialogHtml = this.renderSafeAiHtml(content)
      this.historyDialogOpen = true
    },
    openDiagnosisDialog(content) {
      this.historyDialogTitle = 'AI 诊断建议'
      this.historyDialogShowMeta = false
      this.historyDialogHtml = this.renderSafeAiHtml(content || '暂无可展示内容')
      this.historyDialogOpen = true
    },
    handleExportHistory() {
      const headers = ['孩子', '近期关注', '家长账号', '生成时间', '状态', '建议内容']
      const rows = this.historyList.map(row => [
        this.extractStudentName(row.promptContent),
        this.extractConcern(row.promptContent),
        row.userName || '',
        row.createTime || '',
        this.formatStatus(row.status),
        sanitizeAiDisplayContent(row.responseContent || row.errorMessage || '')
      ])
      const csv = [headers].concat(rows)
        .map(row => row.map(value => `"${String(value || '').replace(/"/g, '""')}"`).join(','))
        .join('\n')
      const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `家长陪学历史建议_${Date.now()}.csv`
      link.click()
      URL.revokeObjectURL(link.href)
    },
    handleDeleteLog(row) {
      this.$modal.confirm('确认删除这条历史建议记录吗？').then(() => {
        const request = this.isAdminView ? delAiLog(row.logId) : delMyAiLog(row.logId)
        return request
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getDiagnosisHistory()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.companion-page {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
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
  box-shadow: 0 24px 44px rgba(15, 35, 46, 0.18), inset 0 1px 0 rgba(255, 255, 255, 0.08);
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
  gap: 12px;
  margin-left: auto;
}

.hero-stats--compact {
  grid-template-columns: repeat(2, 1fr);
  min-width: 250px;
}

.stat-card {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16), inset 0 1px 0 rgba(255, 255, 255, 0.06);
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

.ai-recommend-panel,
.toolbar-panel {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
  padding: 20px 22px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow: 0 22px 40px rgba(41, 130, 141, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
}

.ai-recommend-head,
.panel-head,
.toolbar-row,
.toolbar-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.ai-recommend-head {
  margin-bottom: 16px;
}

.panel-head {
  align-items: center;
  margin: 0 0 14px;
}

.ai-recommend-head h3,
.panel-head h3,
.resource-title {
  margin: 0 0 8px;
  color: #173746;
  font-size: 24px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
  letter-spacing: 0.01em;
}

.ai-recommend-head p {
  margin: 0;
  color: #6f8792;
  line-height: 1.75;
}

.diagnosis-form {
  margin-bottom: 0;
}

.child-select {
  width: 92px;
}

.concern-item {
  min-width: 520px;
}

.concern-input {
  width: 520px;
}

.recommend-trigger,
.resource-manage-btn {
  height: 48px;
  padding: 0 24px;
  border: none;
  border-radius: 18px;
  background: linear-gradient(135deg, #62d4b0 0%, #4aa6f3 100%);
  box-shadow: 0 14px 26px rgba(73, 171, 204, 0.2);
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
}

.history-panel {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
  padding: 20px 22px 22px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  border-radius: 24px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

.history-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  padding: 16px 18px;
  border-color: rgba(106, 216, 218, 0.18);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: none;
  backdrop-filter: none;
}

.history-toolbar .toolbar-main {
  align-items: center;
}

.query-form {
  flex: 1;
  margin-bottom: 0;
}

.table-section-card {
  position: relative;
  z-index: 1;
  overflow: hidden;
  border: 1px solid rgba(106, 216, 218, 0.18);
  border-radius: 0 0 24px 24px;
  border-top: none;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

.history-panel .table-section-card {
  margin: 0;
  border: 1px solid rgba(106, 216, 218, 0.18);
  border-radius: 24px;
  box-shadow: 0 18px 30px rgba(41, 130, 141, 0.06);
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  margin-left: auto;
}

.toolbar-plain-btn {
  height: 32px;
  padding: 0 14px;
  border-radius: 12px;
  border-color: rgba(188, 222, 227, 0.8);
  background: #ffffff;
  color: #526b77;
  font-weight: 700;
}

.content-table {
  margin-bottom: 0;
}

.history-content {
  display: -webkit-box;
  overflow: hidden;
  color: #4f6672;
  line-height: 1.75;
  white-space: pre-line;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.danger-text {
  color: #ef5753;
}

.history-detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 14px;
}

.history-detail-meta span {
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(20, 210, 177, 0.1);
  color: #0d8f88;
  font-size: 12px;
  font-weight: 700;
}

.history-detail-body {
  min-height: 120px;
  padding: 18px 20px;
  border: 1px solid rgba(108, 208, 219, 0.16);
  border-radius: 18px;
  background: #ffffff;
  color: #314c5d;
  line-height: 1.9;
}

.toolbar-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-row {
  align-items: center;
  margin-bottom: 14px;
}

.filter-chip {
  height: 40px;
  padding: 0 18px;
  border: 1px solid rgba(188, 222, 227, 0.9);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #5f7581;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
}

.filter-chip.active,
.filter-chip:hover {
  border-color: rgba(76, 198, 176, 0.55);
  background: linear-gradient(135deg, rgba(78, 216, 174, 0.16), rgba(105, 184, 255, 0.16));
  color: #205262;
  box-shadow: 0 10px 18px rgba(56, 167, 169, 0.1);
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.resource-card {
  display: flex;
  flex-direction: column;
  padding: 22px;
  border: 1px solid rgba(188, 222, 227, 0.52);
  border-radius: 28px;
  background: rgba(247, 253, 251, 0.88);
  box-shadow: 0 16px 34px rgba(35, 72, 78, 0.08);
  backdrop-filter: blur(10px);
  transition: all 0.22s ease;
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 30px rgba(35, 72, 78, 0.1);
}

.resource-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.resource-source {
  display: inline-flex;
  margin-bottom: 10px;
  color: #57bea9;
  font-size: 13px;
  font-weight: 700;
}

.resource-head h3 {
  margin: 0;
  color: #203648;
  font-size: 24px;
  line-height: 1.25;
}

.resource-type {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 78px;
  height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.resource-type.official {
  background: rgba(219, 248, 232, 0.85);
  color: #2e9d72;
}

.resource-type.video {
  background: rgba(223, 238, 255, 0.86);
  color: #4488d8;
}

.resource-desc {
  min-height: 78px;
  margin: 14px 0 0;
  color: #657c87;
  line-height: 1.85;
}

.resource-meta,
.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.resource-meta {
  margin-top: 16px;
}

.resource-tags {
  margin-top: 14px;
  margin-bottom: 18px;
}

.resource-meta span,
.resource-tags span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  background: rgba(243, 250, 252, 0.95);
  color: #607783;
  font-size: 13px;
  font-weight: 700;
}

.resource-tags span {
  height: 34px;
  padding: 0 14px;
  background: rgba(243, 250, 252, 0.95);
}

.resource-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 46px;
  margin-top: auto;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #5cd3a9, #57a0f4);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.full-width {
  width: 100%;
}

::v-deep .query-form .el-form-item,
::v-deep .diagnosis-form .el-form-item {
  display: inline-flex;
  align-items: center;
  margin-bottom: 0;
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
  background: #ffffff;
  color: #355161;
  box-shadow: none;
}

::v-deep .el-input__inner:focus,
::v-deep .el-textarea__inner:focus,
::v-deep .el-select .el-input.is-focus .el-input__inner {
  border-color: #25ddbf;
  box-shadow: 0 0 0 4px rgba(37, 221, 191, 0.12), 0 12px 22px rgba(39, 182, 194, 0.12);
}

::v-deep .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  box-shadow: 0 16px 30px rgba(20, 175, 183, 0.22);
}

::v-deep .el-table {
  overflow: hidden;
  border-radius: 0;
  border: none;
  background: rgba(255, 255, 255, 0.92);
}

::v-deep .el-table th {
  background: var(--table-header-bg, #d6dbd4) !important;
  color: var(--table-header-text, #3f4a42) !important;
}

.table-section-card :deep(.pagination-container) {
  margin: 0;
  padding: 14px 16px;
  border-top: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: none;
}

.toolbar-icon-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  border-radius: 10px;
}

@media (max-width: 1180px) {
  .resource-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .concern-item,
  .concern-input {
    min-width: 360px;
    width: 360px;
  }
}

@media (max-width: 900px) {
  .hero-panel,
  .ai-recommend-head,
  .toolbar-row,
  .toolbar-main {
    flex-direction: column;
  }

  .hero-stats,
  .resource-grid {
    min-width: 0;
    grid-template-columns: 1fr;
  }

  .concern-item,
  .concern-input,
  .child-select {
    min-width: 0;
    width: 100%;
  }
}
</style>
