<template>
  <div class="app-container family-task-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Family Agreement</span>
        <h1>亲子任务</h1>
        <p>{{ pageDescription }}</p>
      </div>
      <div class="hero-stats hero-stats--compact">
        <div class="stat-card">
          <span>任务总数</span>
          <strong>{{ summary.total || 0 }}</strong>
        </div>
        <div class="stat-card">
          <span>兑换奖励</span>
          <strong>{{ rewardList.length }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <div class="toolbar-main">
        <el-form :inline="true" :model="queryParams" class="query-form" size="small">
          <el-form-item label="任务">
            <el-input v-model="queryParams.taskTitle" placeholder="搜索任务标题" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="queryParams.taskType" clearable placeholder="全部类型">
              <el-option label="家务劳动" value="chore" />
              <el-option label="阅读习惯" value="reading" />
              <el-option label="运动健康" value="sport" />
              <el-option label="艺术练习" value="art" />
              <el-option label="生活习惯" value="habit" />
              <el-option label="学习复盘" value="study" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" clearable placeholder="全部状态">
              <el-option label="待完成" value="0" />
              <el-option label="待确认" value="1" />
              <el-option label="已完成" value="2" />
              <el-option label="已退回" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-tooltip content="查询" placement="top">
              <el-button type="primary" size="mini" class="toolbar-icon-btn" icon="el-icon-search" @click="handleQuery" />
            </el-tooltip>
            <el-tooltip content="重置筛选" placement="top">
              <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetQuery" />
            </el-tooltip>
          </el-form-item>
        </el-form>
        <div class="toolbar-actions">
          <el-button v-if="isParent" type="primary" size="mini" icon="el-icon-plus" class="toolbar-gradient-btn" @click="handleAdd">发布任务</el-button>
        </div>
      </div>
    </section>

    <section class="table-section-card task-section-card">
      <div class="task-grid">
        <article v-for="item in taskList" :key="item.taskId" class="task-card">
          <div class="task-head">
            <div>
              <span class="task-type">{{ formatType(item.taskType) }}</span>
              <h3>{{ item.taskTitle }}</h3>
            </div>
            <el-tag :type="statusTagType(item.status)">{{ formatStatus(item.status) }}</el-tag>
          </div>
          <p class="task-content">{{ item.taskContent || '暂无任务说明' }}</p>
          <div class="task-meta">
            <span>{{ item.studentName }}</span>
            <span>{{ item.rewardPoints || 0 }} 积分</span>
            <span>{{ item.dueDate || '不限日期' }}</span>
          </div>
          <div v-if="item.rewardText" class="reward-box">可兑换：{{ item.rewardText }}</div>
          <div v-if="item.studentFeedback" class="feedback-box">孩子反馈：{{ item.studentFeedback }}</div>
          <div v-if="!isAdminView" class="task-actions">
            <el-button v-if="isParent && item.status === '0'" type="text" @click="handleUpdate(item)">编辑</el-button>
            <el-button v-if="isParent && item.status === '1'" type="text" @click="handleReview(item, '2')">检查</el-button>
            <el-button v-if="isStudent && item.status !== '2'" type="text" @click="handleSubmit(item)">{{ studentSubmitText(item.status) }}</el-button>
            <el-button v-if="isParent && item.status !== '2'" type="text" class="danger-link" @click="handleDelete(item)">删除</el-button>
          </div>
        </article>
      </div>
      <el-empty v-if="!loading && !taskList.length" description="暂无亲子任务" />
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </section>

    <section class="exchange-section table-section-card">
      <div class="exchange-head">
        <div>
          <h3>积分兑换专区</h3>
        </div>
        <div class="exchange-head-actions">
          <span v-if="!isAdminView" class="points-balance">{{ isStudent ? '当前剩余' : '孩子剩余' }} {{ availablePoints }} 积分</span>
          <el-button v-if="isParent" type="primary" size="mini" icon="el-icon-plus" class="toolbar-gradient-btn" @click="openRewardDialog">添加奖励</el-button>
        </div>
      </div>
      <div class="reward-grid">
        <article v-for="item in paginatedRewardList" :key="item.rewardId" class="exchange-card">
          <div class="exchange-card-head">
            <span>{{ item.points }} 积分</span>
            <el-tag size="mini" :type="rewardStatusTagType(item)">{{ rewardStatusText(item) }}</el-tag>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
          <div v-if="isParent && getRewardRequest(item).status === 'pending'" class="exchange-request">
            学生已申请兑换，等待家长确认。
          </div>
          <el-button
            v-if="isStudent && !getRewardRequest(item).status"
            size="mini"
            class="exchange-action-btn"
            :disabled="availablePoints < item.points"
            @click="handleRedeem(item)"
          >
            {{ availablePoints >= item.points ? '申请兑换' : '积分不足' }}
          </el-button>
          <el-button v-if="isStudent && getRewardRequest(item).status === 'pending'" size="mini" class="exchange-action-btn muted" @click="handleCancelRedeem(item)">取消申请</el-button>
          <div v-if="isParent" class="exchange-card-actions">
            <el-button v-if="getRewardRequest(item).status !== 'confirmed'" type="text" size="mini" @click="editReward(item)">编辑</el-button>
            <el-button v-if="getRewardRequest(item).status === 'pending'" type="text" size="mini" @click="confirmReward(item)">确认兑换完成</el-button>
          </div>
        </article>
      </div>
      <pagination
        v-show="rewardList.length > rewardQuery.pageSize"
        :total="rewardList.length"
        :page.sync="rewardQuery.pageNum"
        :limit.sync="rewardQuery.pageSize"
        @pagination="handleRewardPageChange"
      />
    </section>

    <el-dialog :title="taskDialogTitle" :visible.sync="taskOpen" width="680px" append-to-body>
      <el-form ref="taskForm" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="孩子" prop="studentUserId">
          <el-select v-model="form.studentUserId" class="full-width" placeholder="请选择已绑定孩子" :disabled="!!form.taskId">
            <el-option v-for="item in children" :key="item.studentUserId" :label="item.studentName" :value="item.studentUserId" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务标题" prop="taskTitle">
          <el-input v-model="form.taskTitle" placeholder="如：阅读 30 分钟" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="任务类型" prop="taskType">
              <el-select v-model="form.taskType" class="full-width">
                <el-option label="家务劳动" value="chore" />
                <el-option label="阅读习惯" value="reading" />
                <el-option label="运动健康" value="sport" />
                <el-option label="艺术练习" value="art" />
                <el-option label="生活习惯" value="habit" />
                <el-option label="学习复盘" value="study" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker v-model="form.dueDate" value-format="yyyy-MM-dd" type="date" class="full-width" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="任务说明">
          <el-input v-model="form.taskContent" type="textarea" :rows="3" placeholder="说明完成标准，例如：整理书桌并拍照上传" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="积分奖励">
              <el-input-number v-model="form.rewardPoints" :min="0" :max="999" class="full-width" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="taskOpen = false">取消</el-button>
        <el-button type="primary" @click="submitTaskForm">保存</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="rewardForm.rewardId ? '编辑兑换奖励' : '添加兑换奖励'" :visible.sync="rewardOpen" width="560px" append-to-body>
      <el-form ref="rewardForm" :model="rewardForm" :rules="rewardRules" label-width="96px">
        <el-form-item label="奖励名称" prop="title">
          <el-input v-model="rewardForm.title" placeholder="如：周末观影半小时" />
        </el-form-item>
        <el-form-item label="所需积分" prop="points">
          <el-input-number v-model="rewardForm.points" :min="1" :max="999" class="full-width" />
        </el-form-item>
        <el-form-item label="奖励类型" prop="category">
          <el-select v-model="rewardForm.category" class="full-width">
            <el-option label="亲子陪伴" value="亲子陪伴" />
            <el-option label="休闲娱乐" value="休闲娱乐" />
            <el-option label="生活体验" value="生活体验" />
            <el-option label="成长激励" value="成长激励" />
          </el-select>
        </el-form-item>
        <el-form-item label="兑换说明">
          <el-input v-model="rewardForm.description" type="textarea" :rows="3" placeholder="说明兑换后的履约方式和家长确认规则" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rewardOpen = false">取消</el-button>
        <el-button type="primary" @click="submitReward">保存奖励</el-button>
      </div>
    </el-dialog>

    <el-dialog title="提交完成证明" :visible.sync="submitOpen" width="620px" append-to-body>
      <el-form :model="submitForm" label-width="96px">
        <el-form-item label="完成图片">
          <image-upload v-model="submitForm.proofImages" :limit="3" />
        </el-form-item>
        <el-form-item label="完成说明">
          <el-input v-model="submitForm.studentFeedback" type="textarea" :rows="3" placeholder="简单说说你完成了什么" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="submitOpen = false">取消</el-button>
        <el-button type="primary" @click="submitStudentTask">提交给家长确认</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="reviewForm.status === '2' ? '检查完成情况' : '退回重做'" :visible.sync="reviewOpen" width="680px" append-to-body>
      <section class="review-proof-card">
        <div class="review-proof-head">
          <div>
            <span>{{ reviewForm.studentName || '孩子' }}</span>
            <strong>{{ reviewForm.taskTitle || '亲子任务' }}</strong>
          </div>
          <el-tag size="small" :type="statusTagType(reviewForm.currentStatus || '1')">{{ formatStatus(reviewForm.currentStatus || '1') }}</el-tag>
        </div>
        <div class="review-proof-grid">
          <div class="review-image-panel">
            <el-image
              v-if="reviewProofImage"
              :src="reviewProofImage"
              fit="cover"
              :preview-src-list="[reviewProofImage]"
            />
            <div v-else class="default-proof-image">
              <div class="default-proof-card">
                <i class="el-icon-picture-outline"></i>
                <span></span>
                <span></span>
              </div>
              <p>孩子暂未上传完成图片</p>
            </div>
          </div>
          <div class="review-feedback-panel">
            <span>孩子完成说明</span>
            <p>{{ reviewForm.studentFeedback || '孩子暂未填写完成说明，家长可结合线下情况进行检查。' }}</p>
          </div>
        </div>
      </section>
      <el-form :model="reviewForm" label-width="96px">
        <el-form-item label="家长评语">
          <el-input v-model="reviewForm.parentComment" type="textarea" :rows="3" placeholder="可以写一句具体肯定或调整建议" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="reviewOpen = false">取消</el-button>
        <el-button v-if="reviewForm.status === '2'" class="review-return-btn" @click="submitReviewAs('3')">退回</el-button>
        <el-button type="primary" @click="submitReview">{{ reviewForm.status === '2' ? '确认完成' : '确定退回' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMyChildren } from '@/api/edu/student'
import { listFamilyTask, getFamilyTaskSummary, addFamilyTask, updateFamilyTask, submitFamilyTask, reviewFamilyTask, delFamilyTask } from '@/api/edu/familyTask'
import ImageUpload from '@/components/ImageUpload'

const FAMILY_REWARD_STORAGE_KEY = 'edu.familyTask.rewards'
const FAMILY_REWARD_REQUEST_STORAGE_KEY = 'edu.familyTask.rewardRequests'

export default {
  name: 'EduFamilyTask',
  components: { ImageUpload },
  data() {
    return {
      loading: false,
      total: 0,
      taskList: [],
      children: [],
      summary: {},
      taskOpen: false,
      submitOpen: false,
      reviewOpen: false,
      rewardOpen: false,
      form: {},
      submitForm: {},
      reviewForm: {},
      rewardForm: {},
      rewardList: [],
      rewardRequests: [],
      rewardQuery: {
        pageNum: 1,
        pageSize: 6
      },
      queryParams: {
        pageNum: 1,
        pageSize: 12,
        taskTitle: undefined,
        taskType: undefined,
        status: undefined
      },
      rules: {
        studentUserId: [{ required: true, message: '请选择孩子', trigger: 'change' }],
        taskTitle: [{ required: true, message: '请填写任务标题', trigger: 'blur' }],
        taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }]
      },
      rewardRules: {
        title: [{ required: true, message: '请填写奖励名称', trigger: 'blur' }],
        points: [{ required: true, message: '请设置兑换积分', trigger: 'change' }],
        category: [{ required: true, message: '请选择奖励类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    roles() {
      return this.$store.getters.roles || []
    },
    isParent() {
      return this.roles.includes('edu_parent')
    },
    isStudent() {
      return this.roles.includes('edu_student')
    },
    isAdminView() {
      return this.roles.includes('admin') || this.roles.includes('edu_admin')
    },
    pageDescription() {
      if (this.isAdminView) {
        return '查看全平台亲子任务发布、提交、确认与积分发放情况，用于掌握课后服务中的生活习惯培养进展。'
      }
      if (this.isParent) {
        return '发布生活习惯、阅读运动与家庭协作任务，给孩子设置积分奖励和亲子约定。'
      }
      return '接收家长发布的亲子任务，完成后上传证明，积累可兑换的亲子积分。'
    },
    taskDialogTitle() {
      return this.form.taskId ? '编辑亲子任务' : '发布亲子任务'
    },
    availablePoints() {
      const confirmedCost = this.rewardRequests
        .filter(item => item.status === 'confirmed')
        .reduce((sum, item) => sum + Number(item.points || 0), 0)
      return Math.max((this.summary.points || 0) - confirmedCost, 0)
    },
    paginatedRewardList() {
      const start = (this.rewardQuery.pageNum - 1) * this.rewardQuery.pageSize
      return this.rewardList.slice(start, start + this.rewardQuery.pageSize)
    },
    reviewProofImage() {
      const images = (this.reviewForm.proofImages || '').split(',').map(item => item.trim()).filter(Boolean)
      return images[0] || ''
    }
  },
  created() {
    this.rewardList = this.loadRewardList()
    this.rewardRequests = this.loadRewardRequests()
    this.getList()
    this.getSummary()
    if (this.isParent) {
      this.getChildren()
    }
  },
  methods: {
    getList() {
      this.loading = true
      listFamilyTask(this.queryParams).then(res => {
        this.taskList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    getSummary() {
      getFamilyTaskSummary().then(res => {
        this.summary = res.data || {}
      })
    },
    getChildren() {
      listMyChildren().then(res => {
        this.children = res.data || []
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 12, taskTitle: undefined, taskType: undefined, status: undefined }
      this.getList()
    },
    handleAdd() {
      this.form = { taskType: 'habit', rewardPoints: 10, rewardText: '' }
      if (this.children.length) {
        this.form.studentUserId = this.children[0].studentUserId
      }
      this.taskOpen = true
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.taskOpen = true
    },
    submitTaskForm() {
      this.$refs.taskForm.validate(valid => {
        if (!valid) return
        const request = this.form.taskId ? updateFamilyTask(this.form) : addFamilyTask(this.form)
        request.then(() => {
          this.$modal.msgSuccess('保存成功')
          this.taskOpen = false
          this.getList()
          this.getSummary()
        })
      })
    },
    handleSubmit(row) {
      this.submitForm = { taskId: row.taskId, proofImages: row.proofImages || '', studentFeedback: row.studentFeedback || '' }
      this.submitOpen = true
    },
    studentSubmitText(status) {
      const normalizedStatus = String(status)
      if (normalizedStatus === '1') {
        return '修改提交'
      }
      if (normalizedStatus === '3') {
        return '重新提交'
      }
      return '提交完成'
    },
    submitStudentTask() {
      submitFamilyTask(this.submitForm).then(() => {
        this.$modal.msgSuccess('已提交给家长确认')
        this.submitOpen = false
        this.getList()
        this.getSummary()
      })
    },
    handleReview(row, status) {
      this.reviewForm = {
        taskId: row.taskId,
        status,
        currentStatus: row.status,
        taskTitle: row.taskTitle,
        studentName: row.studentName,
        proofImages: row.proofImages || '',
        studentFeedback: row.studentFeedback || '',
        parentComment: row.parentComment || ''
      }
      this.reviewOpen = true
    },
    submitReview() {
      reviewFamilyTask(this.reviewForm).then(() => {
        this.$modal.msgSuccess(this.reviewForm.status === '2' ? '已确认完成' : '已退回重做')
        this.reviewOpen = false
        this.getList()
        this.getSummary()
      })
    },
    submitReviewAs(status) {
      this.reviewForm.status = status
      this.submitReview()
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该亲子任务吗？').then(() => delFamilyTask(row.taskId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
        this.getSummary()
      }).catch(() => {})
    },
    getDefaultRewards() {
      return [
        { rewardId: 'default-1', title: '睡前故事点播权', points: 5, category: '亲子陪伴', description: '孩子选择一个睡前故事主题，家长当天确认后完成。' },
        { rewardId: 'default-2', title: '亲子游戏十分钟', points: 8, category: '亲子陪伴', description: '完成兑换后安排一次短时亲子桌游或小游戏。' },
        { rewardId: 'default-3', title: '早餐菜单建议权', points: 10, category: '生活体验', description: '孩子可提出一次早餐搭配建议，家长确认后安排。' },
        { rewardId: 'default-4', title: '周末亲子观影半小时', points: 30, category: '亲子陪伴', description: '由家长确认兑换时间，作为完成亲子约定后的陪伴奖励。' },
        { rewardId: 'default-5', title: '自主选择一次晚餐菜单', points: 45, category: '生活体验', description: '孩子可提出一次家庭晚餐建议，家长根据实际情况共同完成。' },
        { rewardId: 'default-6', title: '户外活动优先选择权', points: 60, category: '成长激励', description: '周末户外安排中，孩子可优先选择一次活动项目。' },
        { rewardId: 'default-7', title: '家庭小队长一天', points: 20, category: '成长激励', description: '孩子负责一次家庭小安排，家长给予支持和确认。' },
        { rewardId: 'default-8', title: '自选绘本共读', points: 15, category: '休闲娱乐', description: '兑换后孩子选择一本绘本或短篇内容，与家长共同阅读。' }
      ]
    },
    getCustomRewards() {
      try {
        const raw = localStorage.getItem(FAMILY_REWARD_STORAGE_KEY)
        const parsed = raw ? JSON.parse(raw) : []
        return Array.isArray(parsed) ? parsed : []
      } catch (error) {
        return []
      }
    },
    loadRewardList() {
      const customRewards = this.getCustomRewards()
      const customMap = customRewards.reduce((map, item) => {
        map[item.rewardId] = item
        return map
      }, {})
      const defaultIds = this.getDefaultRewards().map(item => item.rewardId)
      const mergedDefaults = this.getDefaultRewards().map(item => customMap[item.rewardId] || item)
      const appendedRewards = customRewards.filter(item => !defaultIds.includes(item.rewardId))
      return appendedRewards.concat(mergedDefaults)
    },
    saveCustomRewards(list) {
      localStorage.setItem(FAMILY_REWARD_STORAGE_KEY, JSON.stringify(list || []))
    },
    openRewardDialog() {
      this.rewardForm = { title: '', points: 10, category: '亲子陪伴', description: '' }
      this.rewardOpen = true
      this.$nextTick(() => {
        if (this.$refs.rewardForm) {
          this.$refs.rewardForm.clearValidate()
        }
      })
    },
    submitReward() {
      this.$refs.rewardForm.validate(valid => {
        if (!valid) return
        const reward = {
          rewardId: this.rewardForm.rewardId || `custom-${Date.now()}`,
          title: this.rewardForm.title.trim(),
          points: this.rewardForm.points,
          category: this.rewardForm.category,
          description: (this.rewardForm.description || '家长确认后完成兑换。').trim()
        }
        const customRewards = this.getCustomRewards().filter(item => item.rewardId !== reward.rewardId)
        customRewards.unshift(reward)
        this.saveCustomRewards(customRewards)
        this.rewardList = this.loadRewardList()
        this.rewardOpen = false
        this.$modal.msgSuccess('兑换奖励已保存')
      })
    },
    editReward(item) {
      this.rewardForm = { ...item }
      this.rewardOpen = true
      this.$nextTick(() => {
        if (this.$refs.rewardForm) {
          this.$refs.rewardForm.clearValidate()
        }
      })
    },
    loadRewardRequests() {
      try {
        const raw = localStorage.getItem(FAMILY_REWARD_REQUEST_STORAGE_KEY)
        const parsed = raw ? JSON.parse(raw) : []
        return Array.isArray(parsed) ? parsed : []
      } catch (error) {
        return []
      }
    },
    saveRewardRequests(list) {
      localStorage.setItem(FAMILY_REWARD_REQUEST_STORAGE_KEY, JSON.stringify(list || []))
    },
    getRewardRequest(item) {
      return this.rewardRequests.find(request => request.rewardId === item.rewardId) || {}
    },
    handleRedeem(item) {
      if (this.availablePoints < item.points) {
        this.$modal.msgWarning('当前积分不足，继续完成亲子任务即可兑换')
        return
      }
      this.rewardRequests = this.rewardRequests
        .filter(request => request.rewardId !== item.rewardId)
        .concat([{ rewardId: item.rewardId, title: item.title, points: item.points, status: 'pending', createTime: Date.now() }])
      this.saveRewardRequests(this.rewardRequests)
      this.$modal.msgSuccess(`已提交“${item.title}”兑换申请，等待家长确认`)
    },
    handleCancelRedeem(item) {
      this.rewardRequests = this.rewardRequests.filter(request => request.rewardId !== item.rewardId)
      this.saveRewardRequests(this.rewardRequests)
      this.$modal.msgSuccess('兑换申请已取消')
    },
    confirmReward(item) {
      this.rewardRequests = this.rewardRequests.map(request => {
        if (request.rewardId !== item.rewardId) {
          return request
        }
        return { ...request, status: 'confirmed', confirmTime: Date.now() }
      })
      this.saveRewardRequests(this.rewardRequests)
      this.$modal.msgSuccess('已确认兑换完成')
    },
    rewardStatusText(item) {
      const request = this.getRewardRequest(item)
      if (request.status === 'pending') return '待家长确认'
      if (request.status === 'confirmed') return '已兑换'
      return item.category
    },
    rewardStatusTagType(item) {
      const request = this.getRewardRequest(item)
      if (request.status === 'pending') return 'warning'
      if (request.status === 'confirmed') return 'success'
      return 'info'
    },
    handleRewardPageChange() {
      this.rewardList = this.loadRewardList()
    },
    formatType(type) {
      return ({ chore: '家务劳动', reading: '阅读习惯', sport: '运动健康', art: '艺术练习', habit: '生活习惯', study: '学习复盘' })[type] || '亲子任务'
    },
    formatStatus(status) {
      return ({ 0: '待完成', 1: '待确认', 2: '已完成', 3: '已退回' })[status] || '未知'
    },
    statusTagType(status) {
      return ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' })[status] || 'info'
    }
  }
}
</script>

<style scoped>
.family-task-page {
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
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  min-width: 250px;
  margin-left: auto;
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

.toolbar-panel {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  padding: 20px 22px 6px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow: 0 22px 40px rgba(41, 130, 141, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
}

.toolbar-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.query-form {
  flex: 1;
  margin-bottom: 0;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
}

.table-section-card {
  position: relative;
  z-index: 1;
  overflow: hidden;
  border: 1px solid rgba(106, 216, 218, 0.18);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

.task-section-card {
  padding: 18px 18px 0;
  margin-bottom: 18px;
}

.task-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.task-card {
  display: flex;
  flex-direction: column;
  padding: 22px;
  border: 1px solid rgba(188, 222, 227, 0.52);
  border-radius: 22px;
  background: #fffdfa;
  box-shadow: 0 16px 34px rgba(35, 72, 78, 0.08);
}

.task-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.task-type {
  color: #0d8f88;
  font-size: 13px;
  font-weight: 700;
}

.task-head h3 {
  margin: 8px 0 0;
  color: #173746;
  font-size: 22px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
}

.task-content {
  min-height: 60px;
  color: #607783;
  line-height: 1.8;
}

.task-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  padding: 12px;
  border: 1px solid rgba(111, 150, 116, 0.14);
  border-radius: 16px;
  background: #f6f8f1;
}

.task-meta span {
  color: #58707e;
  font-size: 13px;
  font-weight: 700;
}

.reward-box {
  display: none;
}

.feedback-box {
  padding: 10px 12px;
  border: 1px solid rgba(111, 150, 116, 0.14);
  border-radius: 14px;
  background: #ffffff;
  color: #58707e;
  font-size: 13px;
}

.reward-box,
.feedback-box {
  margin-top: 10px;
}

.task-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: auto;
  padding-top: 14px;
}

.exchange-section {
  margin-bottom: 18px;
  padding: 20px 22px 22px;
}

.exchange-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.exchange-head-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}

.points-balance {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 14px;
  border: 1px solid rgba(111, 150, 116, 0.18);
  border-radius: 999px;
  background: #f6f8f1;
  color: #4d6852;
  font-size: 13px;
  font-weight: 800;
}

.exchange-head h3,
.exchange-card h3 {
  margin: 0;
  color: #173746;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
}

.exchange-head h3 {
  font-size: 24px;
}

.reward-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.exchange-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 18px;
  border: 1px solid rgba(188, 222, 227, 0.52);
  border-radius: 20px;
  background: #fffdfa;
  box-shadow: 0 14px 28px rgba(35, 72, 78, 0.06);
}

.exchange-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.exchange-card-head span {
  color: #4d6852;
  font-size: 15px;
  font-weight: 800;
}

.exchange-card h3 {
  font-size: 20px;
}

.exchange-card p {
  min-height: 48px;
  margin: 0;
  color: #617985;
  line-height: 1.7;
}

.exchange-request {
  padding: 9px 12px;
  border-radius: 14px;
  background: #fff8e8;
  color: #9b7134;
  font-size: 13px;
  font-weight: 700;
}

.exchange-action-btn {
  align-self: flex-start;
  border: 1px solid rgba(95, 143, 78, 0.22);
  border-radius: 12px;
  background: #f6f8f1;
  color: #4d6852;
  font-weight: 800;
}

.exchange-action-btn:hover,
.exchange-action-btn:focus {
  border-color: rgba(95, 143, 78, 0.36);
  background: #eef4e8;
  color: #3f5c45;
}

.exchange-action-btn.muted {
  border-color: rgba(188, 150, 95, 0.28);
  background: #fff8e8;
  color: #8a6a3a;
}

.exchange-card-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: auto;
  padding-top: 4px;
}

.review-proof-card {
  margin-bottom: 18px;
  padding: 18px;
  border: 1px solid rgba(134, 214, 222, 0.3);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(239, 252, 255, 0.94));
  box-shadow: 0 14px 28px rgba(35, 112, 128, 0.08);
}

.review-proof-head {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.review-proof-head span {
  display: block;
  margin-bottom: 6px;
  color: #0d8f88;
  font-size: 13px;
  font-weight: 800;
}

.review-proof-head strong {
  color: #173746;
  font-size: 20px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
}

.review-proof-grid {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 16px;
}

.review-image-panel {
  min-height: 150px;
  overflow: hidden;
  border: 1px solid rgba(134, 214, 222, 0.28);
  border-radius: 18px;
  background: #ffffff;
}

.review-image-panel ::v-deep .el-image {
  width: 100%;
  height: 100%;
  min-height: 150px;
  display: block;
}

.default-proof-image {
  position: relative;
  display: grid;
  place-items: center;
  min-height: 150px;
  padding: 18px;
  overflow: hidden;
  background:
    radial-gradient(circle at 76% 22%, rgba(255, 211, 116, 0.28), transparent 26%),
    linear-gradient(145deg, rgba(238, 252, 255, 0.96), rgba(255, 255, 255, 0.98));
}

.default-proof-card {
  position: relative;
  display: grid;
  place-items: center;
  width: 108px;
  height: 78px;
  border: 2px dashed rgba(73, 171, 183, 0.34);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 14px 24px rgba(58, 142, 160, 0.14);
}

.default-proof-card i {
  color: #4aaeba;
  font-size: 30px;
}

.default-proof-card span {
  position: absolute;
  left: 18px;
  right: 18px;
  height: 4px;
  border-radius: 999px;
  background: rgba(74, 174, 186, 0.22);
}

.default-proof-card span:nth-child(2) {
  bottom: 18px;
}

.default-proof-card span:nth-child(3) {
  bottom: 10px;
  right: 34px;
}

.default-proof-image p {
  margin: 8px 0 0;
  color: #5f7885;
  font-size: 13px;
  font-weight: 800;
}

.review-feedback-panel {
  padding: 16px 18px;
  border: 1px solid rgba(134, 214, 222, 0.24);
  border-radius: 18px;
  background: #ffffff;
}

.review-feedback-panel span {
  display: block;
  margin-bottom: 10px;
  color: #0d8f88;
  font-size: 13px;
  font-weight: 800;
}

.review-feedback-panel p {
  margin: 0;
  color: #536f7e;
  line-height: 1.8;
  white-space: pre-wrap;
}

.danger-link {
  color: #e66b6b;
}

.review-return-btn {
  border-color: rgba(239, 87, 83, 0.2);
  background: rgba(255, 238, 237, 0.94);
  color: #ef5753;
  font-weight: 800;
}

.full-width {
  width: 100%;
}

::v-deep .query-form .el-form-item {
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

.toolbar-gradient-btn {
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  color: #ffffff;
  box-shadow: 0 14px 24px rgba(20, 175, 183, 0.18);
}

.toolbar-icon-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  border-radius: 10px;
}

.task-section-card :deep(.pagination-container) {
  margin: 16px -18px 0;
  padding: 14px 16px;
  border-top: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
}

.exchange-section :deep(.pagination-container) {
  margin: 16px -22px -22px;
  padding: 14px 16px;
  border-top: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: none;
}

@media (max-width: 1200px) {
  .task-grid,
  .reward-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 800px) {
  .hero-panel,
  .toolbar-main {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-stats,
  .task-grid,
  .reward-grid {
    grid-template-columns: 1fr;
  }
}
</style>
