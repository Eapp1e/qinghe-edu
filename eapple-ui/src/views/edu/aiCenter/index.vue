<template>
  <div class="app-container ai-center-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">AI Center</span>
        <h1>{{ pageConfig.title }}</h1>
        <p>{{ pageConfig.description }}</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>当前模型</span>
          <strong>{{ currentModelDisplay }}</strong>
        </div>
        <div class="stat-card">
          <span>最近调用</span>
          <strong>{{ summaryTotal }}</strong>
        </div>
      </div>
    </section>

    <section class="overview-grid">
      <div class="overview-card accent-card">
        <div class="card-head">
          <div>
            <h3>模型选择</h3>
          </div>
        </div>
        <div class="model-picker">
          <el-select v-model="currentAiModel" class="model-select" @change="handleModelChange">
            <el-option
              v-for="item in aiModelOptions"
              :key="item.modelName"
              :label="item.displayName"
              :value="item.modelName"
            >
              <div class="model-option">
                <strong>{{ item.displayName }}</strong>
                <span>{{ item.description }}</span>
              </div>
            </el-option>
          </el-select>
          <div class="model-current-tip">当前已为本账号记住默认模型：<strong>{{ currentModelDisplay }}</strong></div>
        </div>
        <div class="model-tags">
          <span
            v-for="item in aiModelOptions"
            :key="item.modelName"
            :class="['model-tag', { active: item.modelName === currentAiModel }]"
          >
            {{ item.displayName }}
          </span>
        </div>
      </div>

      <div class="overview-card compact-card">
        <h3>{{ pageConfig.snapshotTitle }}</h3>
        <div class="mini-stats mini-stats-dual">
          <div>
            <strong>{{ successCount }}</strong>
            <span>成功调用</span>
          </div>
          <div>
            <strong>{{ failedCount }}</strong>
            <span>失败调用</span>
          </div>
        </div>
        <p class="compact-copy">{{ pageConfig.snapshotTip }}</p>
      </div>

      <div class="overview-card compact-card">
        <h3>{{ pageConfig.featureTitle }}</h3>
        <ul class="feature-list">
          <li
            v-for="item in pageConfig.features"
            :key="item.label || item"
            :class="{ clickable: !!item.path }"
            @click="handleFeatureClick(item)"
          >
            <span class="feature-dot" />
            <span class="feature-label">{{ item.label || item }}</span>
            <i v-if="item.path" class="el-icon-arrow-right" />
          </li>
        </ul>
      </div>
    </section>

    <section class="log-panel">
      <div class="panel-head">
        <div>
          <h3>{{ pageConfig.logTitle }}</h3>
        </div>
        <el-tooltip content="刷新" placement="top">
          <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-refresh" @click="refreshData" />
        </el-tooltip>
      </div>

      <el-table v-loading="loading" :data="aiLogList" class="content-table">
        <el-table-column label="业务类型" min-width="150">
          <template slot-scope="scope">
            <div class="biz-type-cell">
              <span class="biz-type-tag">{{ formatBusinessType(scope.row.businessType) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="模型" prop="modelName" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag size="small" :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="showRoleColumn" label="角色" prop="roleType" width="100" />
        <el-table-column label="返回内容" min-width="320">
          <template slot-scope="scope">
            <div class="response-card">
              <p class="response-preview">{{ getResponsePreview(scope.row.responseContent, scope.row.errorMessage) }}</p>
              <el-button
                v-if="hasResponseDetail(scope.row.responseContent, scope.row.errorMessage)"
                type="text"
                size="mini"
                @click="showResponseDetail(scope.row)"
              >
                查看完整内容
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getLogList"
      />
    </section>

    <el-dialog :title="responseDialogTitle" :visible.sync="responseDialogOpen" width="720px" append-to-body>
      <div class="response-detail-panel">
        <div class="response-detail-meta">
          <span class="biz-type-tag">{{ responseDialogBusinessType }}</span>
          <el-tag size="small" :type="responseDialogStatusType">{{ responseDialogStatus }}</el-tag>
        </div>
        <div class="response-detail-body ai-rich-content" v-html="responseDialogHtml"></div>
      </div>
      <div slot="footer">
        <el-button @click="responseDialogOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAiModels, getCurrentAiModel, updateCurrentAiModel } from '@/api/edu/ai'
import { listAiLog, listMyAiLog, getAiLogSummary, getMyAiLogSummary } from '@/api/edu/aiLog'
import { renderAiContentHtml, sanitizeAiDisplayContent } from '@/utils/aiContent'

export default {
  name: 'EduAiCenter',
  data() {
    return {
      loading: false,
      total: 0,
      aiLogList: [],
      summaryData: {
        total: 0,
        success: 0,
        failed: 0
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      aiModelOptions: [],
      currentAiModel: '',
      responseDialogOpen: false,
      responseDialogTitle: 'AI 返回内容',
      responseDialogBusinessType: '',
      responseDialogStatus: '',
      responseDialogStatusType: 'info',
      responseDialogContent: '',
      responseDialogHtml: ''
    }
  },
  computed: {
    isAdminView() {
      return this.$auth.hasRole('admin') || this.$auth.hasRole('edu_admin')
    },
    isTeacherView() {
      return this.$auth.hasRole('edu_teacher')
    },
    isParentView() {
      return this.$auth.hasRole('edu_parent')
    },
    isStudentView() {
      return this.$auth.hasRole('edu_student')
    },
    currentModelDisplay() {
      const current = this.aiModelOptions.find(item => item.modelName === this.currentAiModel)
      return current ? current.displayName : (this.currentAiModel || '未设置')
    },
    summaryTotal() {
      return this.summaryData.total || 0
    },
    successCount() {
      return this.summaryData.success || 0
    },
    failedCount() {
      return this.summaryData.failed || 0
    },
    showRoleColumn() {
      return this.isAdminView
    },
    pageConfig() {
      if (this.isAdminView) {
        return {
          title: 'AI中心',
          description: '统一管理平台中的 AI 模型、生成能力与调用记录，重点掌握平台整体使用情况、模型稳定性与风险状态。',
          modelTip: '平台内的 AI 能力会优先使用当前账号在这里选中的模型，管理员可用它做测试与巡检。',
          snapshotTitle: '平台运行概览',
          snapshotTip: '适合查看平台整体 AI 使用质量、失败率与模型稳定性，再决定后续优化方向。',
          featureTitle: '平台重点能力',
          features: [
            { label: '课程通知生成', path: '/edu/platform-notice' },
            { label: '教学建议生成', path: '/edu/course' },
            { label: '作业问答辅助', path: '/edu/question' },
            { label: '课程推荐理由生成', path: '/edu/student' }
          ],
          logTitle: '平台最近 AI 调用',
          logDescription: '展示平台最近的 AI 调用记录，便于核查返回质量与整体使用情况。'
        }
      }
      if (this.isTeacherView) {
        return {
          title: '教学AI中心',
          description: '围绕课程通知、教学建议与课堂辅助统一配置 AI，帮助教师更高效地完成课后服务教学工作。',
          modelTip: '课程中心里的 AI 通知和 AI 建议都会优先使用这里选中的模型。',
          snapshotTitle: '教学生成概览',
          snapshotTip: '如果 AI 生成超时，建议优先切换到更轻量模型，再重新生成课程通知或教学建议。',
          featureTitle: '教学辅助能力',
          features: [
            { label: '课程通知生成', path: '/edu/course' },
            { label: '教学建议生成', path: '/edu/course' },
            { label: '作业答疑辅助', path: '/edu/question' }
          ],
          logTitle: '我的最近 AI 调用',
          logDescription: '展示当前教师账号最近的 AI 调用记录，便于回看通知与教学建议生成结果。'
        }
      }
      if (this.isParentView) {
        return {
          title: '陪学AI中心',
          description: '为家长提供更合适的 AI 模型选择，支持查看陪学问答与课程推荐相关的 AI 使用记录。',
          modelTip: '家长侧的 AI 互动、课程推荐理由等内容会优先使用这里选中的模型。',
          snapshotTitle: '陪学使用概览',
          snapshotTip: '推荐优先选择响应更快、表达更稳定的模型，方便日常陪学与课程参考。',
          featureTitle: '家长常用能力',
          features: [
            { label: '陪学问答支持', path: '/edu/question' },
            { label: '孩子课程推荐', path: '/edu/student' }
          ],
          logTitle: '我的最近 AI 互动',
          logDescription: '展示当前家长账号最近的 AI 调用记录，便于回看陪学问题和推荐内容。'
        }
      }
      return {
        title: '学习AI中心',
        description: '为学生提供更适合学习场景的 AI 助手配置，帮助完成作业问答、课程理解与课后学习支持。',
        modelTip: '作业问答、课程推荐等学习场景会优先使用这里选中的模型。',
        snapshotTitle: '学习助手概览',
        snapshotTip: '建议优先选择更简洁、响应更快的模型，方便日常提问和课后学习。',
        featureTitle: '学习辅助能力',
        features: [
          { label: '作业问题解答', path: '/edu/question' },
          { label: '课程推荐理由', path: '/edu/student' },
          { label: '网课推荐', path: '/edu/online-course' }
        ],
        logTitle: '我的最近 AI 使用',
        logDescription: '展示当前学生账号最近的 AI 调用记录，便于回看作业问答与学习辅助内容。'
      }
    }
  },
  created() {
    this.getAiModelConfig()
    this.refreshData()
  },
  methods: {
    getModelCacheKey() {
      const userId = this.$store.getters.id || 'default'
      return `edu.ai.userModel.${userId}`
    },
    saveLocalModel(modelName) {
      localStorage.setItem(this.getModelCacheKey(), modelName || '')
    },
    getLocalModel() {
      return localStorage.getItem(this.getModelCacheKey()) || ''
    },
    getAiModelConfig() {
      const localModel = this.getLocalModel()
      if (localModel) {
        this.currentAiModel = localModel
      }
      listAiModels().then(res => {
        this.aiModelOptions = res.data || []
      })
      getCurrentAiModel().then(res => {
        this.currentAiModel = res.data || localModel || ''
        this.saveLocalModel(this.currentAiModel)
      })
    },
    getLogList() {
      this.loading = true
      const request = this.isAdminView
        ? listAiLog(this.queryParams)
        : listMyAiLog(this.queryParams)
      request.then(res => {
        this.aiLogList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    getLogSummary() {
      const request = this.isAdminView
        ? getAiLogSummary()
        : getMyAiLogSummary()
      request.then(res => {
        this.summaryData = {
          total: (res.data && res.data.total) || 0,
          success: (res.data && res.data.success) || 0,
          failed: (res.data && res.data.failed) || 0
        }
      })
    },
    refreshData() {
      this.queryParams.pageNum = 1
      this.getLogList()
      this.getLogSummary()
    },
    handleModelChange(modelName) {
      updateCurrentAiModel(modelName).then(() => {
        this.currentAiModel = modelName
        this.saveLocalModel(modelName)
        const current = this.aiModelOptions.find(item => item.modelName === modelName)
        this.$modal.msgSuccess(`已切换到 ${current ? current.displayName : modelName}`)
        this.refreshData()
      })
    },
    handleFeatureClick(item) {
      if (!item || !item.path || this.$route.path === item.path) {
        return
      }
      this.$router.push(item.path)
    },
    statusTagType(status) {
      if (status === 'success') return 'success'
      if (status === 'mock') return 'warning'
      if (status === 'failed') return 'danger'
      return 'info'
    },
    formatStatus(status) {
      const map = {
        success: '成功',
        failed: '失败',
        mock: '模拟'
      }
      return map[status] || '未知'
    },
    formatBusinessType(type) {
      const map = {
        course_notice: '课程通知生成',
        teaching_suggestion: '教学建议生成',
        homework_answer: '作业问答',
        course_recommendation: '课程推荐',
        online_resource_recommendation: '网课推荐',
        learning_guidance: '学习辅导',
        parent_support: '家长陪学',
        ai_chat: '智能对话'
      }
      return map[type] || type || '未分类'
    },
    sanitizeResponseContent(content) {
      return sanitizeAiDisplayContent(content)
    },
    getResponsePreview(responseContent, errorMessage) {
      const content = this.sanitizeResponseContent(responseContent || errorMessage)
      if (!content) {
        return '暂无可展示内容'
      }
      return content.length > 90 ? `${content.slice(0, 90)}...` : content
    },
    hasResponseDetail(responseContent, errorMessage) {
      return !!this.sanitizeResponseContent(responseContent || errorMessage)
    },
    showResponseDetail(row) {
      const content = this.sanitizeResponseContent(row.responseContent || row.errorMessage)
      this.responseDialogTitle = `${this.formatBusinessType(row.businessType)}详情`
      this.responseDialogBusinessType = this.formatBusinessType(row.businessType)
      this.responseDialogStatus = this.formatStatus(row.status)
      this.responseDialogStatusType = this.statusTagType(row.status)
      this.responseDialogContent = content || '暂无可展示内容'
      this.responseDialogHtml = renderAiContentHtml(content || '暂无可展示内容')
      this.responseDialogOpen = true
    }
  }
}
</script>

<style scoped>
.ai-center-page {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
}

.ai-center-page::before,
.ai-center-page::after {
  display: none;
}

.ai-center-page::before {
  top: -50px;
  right: 6%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(38, 232, 183, 0.18), transparent 68%);
}

.ai-center-page::after {
  left: -70px;
  bottom: 12%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(91, 187, 255, 0.14), transparent 70%);
}

.hero-panel,
.log-panel,
.overview-card {
  position: relative;
  z-index: 1;
}

.hero-panel {
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
  min-width: 240px;
  max-width: 280px;
}

.stat-card,
.overview-card,
.log-panel {
  border: 1px solid rgba(105, 214, 220, 0.18);
  border-radius: 24px;
  background: #fffdfa;
  box-shadow: 0 18px 32px rgba(41, 130, 141, 0.08);
}

.stat-card {
  padding: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16), inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.stat-card span {
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 8px;
  color: #ffffff;
  font-size: 24px;
}

.biz-type-cell {
  display: flex;
  align-items: center;
}

.biz-type-tag {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border: 1px solid rgba(35, 196, 178, 0.18);
  border-radius: 999px;
  background: rgba(20, 210, 177, 0.1);
  color: #0d8f88;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
}

.response-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 14px;
  border: 1px solid rgba(109, 210, 219, 0.16);
  border-radius: 16px;
  background: #ffffff;
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: var(--table-row-hover-bg, #edf4e8) !important;
}

.response-preview {
  display: -webkit-box;
  flex: 1;
  margin: 0;
  overflow: hidden;
  color: #355163;
  line-height: 1.8;
  white-space: pre-line;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.response-card :deep(.el-button) {
  flex: 0 0 auto;
  margin-left: auto;
  padding: 0;
  white-space: nowrap;
}

.response-detail-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.response-detail-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.response-detail-body {
  padding: 18px 20px;
  border: 1px solid rgba(108, 208, 219, 0.16);
  border-radius: 18px;
  background: #ffffff;
  color: #314c5d;
  line-height: 1.9;
}

.ai-rich-content :deep(h2),
.ai-rich-content :deep(h3),
.ai-rich-content :deep(h4) {
  margin: 16px 0 10px;
  color: #173848;
  font-weight: 700;
}

.ai-rich-content :deep(p) {
  margin: 0 0 10px;
}

.ai-rich-content :deep(ul),
.ai-rich-content :deep(ol) {
  margin: 8px 0 10px 18px;
  padding: 0;
}

.ai-rich-content :deep(li) {
  margin-bottom: 6px;
}

.ai-rich-content :deep(strong) {
  color: #183949;
}

.ai-rich-content :deep(code) {
  padding: 2px 6px;
  border-radius: 8px;
  background: rgba(25, 197, 174, 0.08);
  color: #117b7f;
}

.overview-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr;
  gap: 16px;
  margin-bottom: 18px;
}

.overview-card {
  padding: 22px;
}

.accent-card {
  background: #fffdfa;
}

.card-head h3,
.panel-head h3,
.overview-card h3 {
  margin: 0 0 8px;
  color: #173746;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.01em;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
}

.card-head p,
.panel-head p,
.compact-copy {
  margin: 0;
  color: #6d8794;
  line-height: 1.8;
}

.model-picker {
  margin-top: 18px;
}

.model-select {
  width: 100%;
}

.model-current-tip {
  margin-top: 12px;
  color: #56707f;
}

.model-current-tip strong {
  color: #173746;
}

.model-option {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.model-option strong {
  color: #173746;
  font-size: 13px;
}

.model-option span {
  color: #6e8794;
  font-size: 12px;
  line-height: 1.5;
}

.model-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.model-tag {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(232, 247, 250, 0.88);
  border: 1px solid rgba(116, 214, 224, 0.18);
  color: #58707e;
  font-size: 12px;
}

.model-tag.active {
  background: linear-gradient(135deg, rgba(95, 143, 78, 0.16), rgba(131, 176, 118, 0.12));
  border-color: rgba(95, 143, 78, 0.34);
  color: #4d6852;
}

.mini-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin: 18px 0 16px;
}

.mini-stats-dual {
  grid-template-columns: repeat(2, 1fr);
}

.mini-stats strong {
  display: block;
  color: #173746;
  font-size: 26px;
}

.mini-stats span,
.feature-list li {
  color: #6d8794;
}

.feature-list {
  margin: 18px 0 0;
  padding-left: 0;
  display: grid;
  gap: 12px;
  list-style: none;
}

.feature-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 12px 14px;
  border: 1px solid rgba(112, 214, 223, 0.18);
  border-radius: 16px;
  background: rgba(247, 253, 255, 0.9);
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.feature-dot {
  width: 10px;
  height: 10px;
  flex: 0 0 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5fa16a 0%, #89b07a 100%);
  box-shadow: 0 0 0 3px rgba(95, 161, 106, 0.14);
}

.feature-label {
  flex: 1;
}

.feature-list li i {
  color: #88a28e;
  font-size: 14px;
}

.feature-list li.clickable {
  cursor: pointer;
}

.feature-list li.clickable:hover {
  transform: translateY(-1px);
  border-color: rgba(95, 143, 78, 0.34);
  box-shadow: 0 14px 26px rgba(33, 145, 155, 0.1);
}

.feature-list li.clickable:hover i,
.feature-list li.clickable:hover span {
  color: #4d6852;
}

.log-panel {
  padding: 22px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.content-table {
  margin-bottom: 0;
}

::v-deep .el-input__inner,
::v-deep .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: #ffffff;
  color: #355161;
}

::v-deep .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  box-shadow: 0 16px 30px rgba(20, 175, 183, 0.22);
}

::v-deep .el-table {
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

::v-deep .el-table th {
  background: var(--table-header-bg, #d6dbd4) !important;
  color: var(--table-header-text, #3f4a42) !important;
}

@media (max-width: 1100px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .hero-panel,
  .panel-head {
    flex-direction: column;
  }

  .hero-stats {
    min-width: 0;
    grid-template-columns: 1fr;
  }

  .mini-stats {
    grid-template-columns: 1fr;
  }

  .response-card {
    align-items: stretch;
    flex-direction: column;
    gap: 8px;
  }

  .response-card :deep(.el-button) {
    margin-left: 0;
    align-self: flex-end;
  }
}
</style>
