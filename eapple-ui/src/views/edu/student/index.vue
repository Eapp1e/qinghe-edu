<template>
  <div class="app-container student-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Student Profile</span>
        <h1>{{ pageTitle }}</h1>
        <p>{{ pageDescription }}</p>
      </div>
      <div v-if="!isOwnerView" class="hero-stats">
        <div class="stat-card">
          <span>档案总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>正常在册</span>
          <strong>{{ activeCount }}</strong>
        </div>
        <div class="stat-card">
          <span>关联家长</span>
          <strong>{{ thirdStat }}</strong>
        </div>
      </div>
    </section>

    <section v-if="isOwnerView" class="owner-layout">
      <div v-if="canSelfEdit" class="owner-toolbar">
        <el-button
          v-if="!studentList.length"
          type="primary"
          icon="el-icon-plus"
          @click="handleOwnerAdd"
        >
          完善我的档案
        </el-button>
      </div>

      <div v-if="studentList.length" class="profile-grid">
        <article v-for="item in studentList" :key="item.profileId" class="profile-card">
          <div class="profile-head">
            <div>
              <span class="profile-badge">{{ $auth.hasRole('edu_student') ? '我的档案' : '学生档案' }}</span>
              <h2>{{ item.studentName }}</h2>
              <p>{{ item.gradeName || '未设置年级' }} · {{ item.className || '未设置班级' }}</p>
            </div>
            <el-tag :type="item.status === '0' ? 'success' : 'info'" size="mini">
              {{ item.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </div>

          <div class="profile-body">
            <div class="profile-section">
              <h3>基础信息</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span>学生姓名</span>
                  <strong>{{ item.studentName || '未填写' }}</strong>
                </div>
                <div class="info-item">
                  <span>性别</span>
                  <strong>{{ item.gender || '未填写' }}</strong>
                </div>
                <div class="info-item">
                  <span>年级</span>
                  <strong>{{ item.gradeName || '未填写' }}</strong>
                </div>
                <div class="info-item">
                  <span>班级</span>
                  <strong>{{ item.className || '未填写' }}</strong>
                </div>
                <div class="info-item">
                  <span>家长姓名</span>
                  <strong>{{ item.parentName || '未关联' }}</strong>
                </div>
                <div class="info-item">
                  <span>关联家长 ID</span>
                  <strong>{{ item.parentUserId || '未关联' }}</strong>
                </div>
              </div>
            </div>

            <div class="profile-section">
              <h3>兴趣画像</h3>
              <div class="tag-panel">
                <el-tag
                  v-for="tag in splitTags(item.interestTags)"
                  :key="tag"
                  size="small"
                  effect="plain"
                  class="interest-tag"
                >
                  {{ tag }}
                </el-tag>
                <span v-if="!splitTags(item.interestTags).length" class="empty-text">暂未填写兴趣标签</span>
              </div>
            </div>

            <div class="profile-section recommend-section">
              <div>
                <h3>智能课程推荐</h3>
                <p>结合学生档案、兴趣标签和课程热度，为当前学生推荐更合适的课后服务课程。</p>
              </div>
              <div class="recommend-actions">
                <el-button v-if="canSelfEdit" plain icon="el-icon-edit" @click="handleOwnerEdit(item)">编辑档案</el-button>
                <el-button type="primary" icon="el-icon-magic-stick" @click="handleRecommend(item)">查看推荐</el-button>
              </div>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else :description="ownerEmptyDescription" class="empty-panel" />
    </section>

    <template v-else>
      <section v-show="showSearch" class="toolbar-panel">
        <div class="toolbar-main">
          <el-form ref="queryForm" :model="queryParams" size="small" inline class="query-form">
            <el-form-item label="学生姓名" prop="studentName">
              <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="年级" prop="gradeName">
              <el-input v-model="queryParams.gradeName" placeholder="请输入年级" clearable @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="班级" prop="className">
              <el-input v-model="queryParams.className" placeholder="请输入班级" clearable @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status" class="status-select">
              <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
            <div class="query-inline-actions">
              <el-tooltip content="查询" placement="top">
                <el-button type="primary" size="mini" class="toolbar-icon-btn" icon="el-icon-search" @click="handleQuery" />
              </el-tooltip>
              <el-tooltip content="重置筛选" placement="top">
                <el-button size="mini" class="toolbar-icon-btn" icon="el-icon-delete" @click="resetQuery" />
              </el-tooltip>
            </div>
          </el-form>

          <div class="toolbar-actions">
            <el-button
              v-if="canManageProfile"
              v-hasPermi="['edu:student:add']"
              type="primary"
              size="mini"
              icon="el-icon-plus"
              class="toolbar-gradient-btn"
              @click="handleAdd"
            >
              新建档案
            </el-button>
            <el-button
              v-if="canManageProfile"
              size="mini"
              icon="el-icon-download"
              @click="handleExport"
            >
              导出
            </el-button>
          </div>
        </div>
      </section>

      <section class="content-layout">
        <el-card shadow="never" class="list-panel">
          <div slot="header" class="panel-header">
            <div>
              <strong>档案列表</strong>
            </div>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
          </div>

          <el-table v-loading="loading" :data="studentList" class="content-table">
            <el-table-column label="学生姓名" min-width="150">
              <template slot-scope="scope">
                <div class="name-cell">
                  <strong>{{ scope.row.studentName }}</strong>
                  <span>ID {{ scope.row.studentUserId }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="家长信息" min-width="150">
              <template slot-scope="scope">
                <div class="name-cell">
                  <strong>{{ scope.row.parentName || '未关联' }}</strong>
                  <span>{{ scope.row.parentUserId ? '家长ID ' + scope.row.parentUserId : '待补充' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="年级班级" min-width="130">
              <template slot-scope="scope">
                <div class="meta-stack">
                  <span>{{ scope.row.gradeName || '未设置年级' }}</span>
                  <span>{{ scope.row.className || '未设置班级' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="兴趣标签" min-width="220" prop="interestTags" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">
                  {{ scope.row.status === '0' ? '正常' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="课程推荐" width="120">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click.stop="handleRecommend(scope.row)">智能推荐</el-button>
              </template>
            </el-table-column>
            <el-table-column v-if="canManageProfile" label="操作" width="150" fixed="right">
              <template slot-scope="scope">
                <el-button v-hasPermi="['edu:student:edit']" type="text" size="mini" @click.stop="handleUpdate(scope.row)">编辑</el-button>
                <el-button v-hasPermi="['edu:student:remove']" type="text" size="mini" @click.stop="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </section>
    </template>

    <el-dialog :title="title" :visible.sync="open" width="700px">
      <el-form ref="form" :model="form" :rules="formRules" label-width="90px">
        <el-row :gutter="12">
          <el-col v-if="!isStudentOwner" :span="12"><el-form-item label="学生ID" prop="studentUserId"><el-input v-model="form.studentUserId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学生姓名" prop="studentName"><el-input v-model="form.studentName" /></el-form-item></el-col>
          <el-col v-if="!isStudentOwner" :span="12"><el-form-item label="家长ID" prop="parentUserId"><el-input v-model="form.parentUserId" /></el-form-item></el-col>
          <el-col v-if="!isStudentOwner" :span="12"><el-form-item label="家长姓名"><el-input v-model="form.parentName" /></el-form-item></el-col>
          <el-col v-if="isStudentOwner" :span="24">
            <el-form-item label="家长账号" prop="parentAccount">
              <el-input v-model="form.parentAccount" placeholder="请输入家长登录账号进行绑定" />
              <div class="field-tip">填写家长登录账号后即可完成绑定，留空则保持当前绑定关系不变。</div>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="年级"><el-input v-model="form.gradeName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级"><el-input v-model="form.className" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="!isStudentOwner" :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="兴趣标签"><el-input v-model="form.interestTags" placeholder="例如：编程, 绘画, 篮球" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="recommendTitle" :visible.sync="recommendOpen" width="820px">
      <div v-loading="recommendLoading" class="recommend-dialog">
        <div v-if="recommendList.length" class="recommend-grid">
          <div v-for="item in recommendList" :key="item.courseId" class="recommend-card">
            <div class="recommend-head">
              <div>
                <strong>{{ item.courseName }}</strong>
                <span>{{ item.category || '综合课程' }}</span>
              </div>
              <el-tag type="success" size="mini">匹配度 {{ item.recommendationScore || 0 }}</el-tag>
            </div>
            <p class="recommend-meta">授课教师：{{ item.teacherName || '待安排' }} · {{ item.weekDay || '' }} {{ item.startTime || '' }}</p>
            <p class="recommend-desc">{{ item.description || '暂无课程简介' }}</p>
            <div class="recommend-reason">
              <span>AI 推荐理由</span>
              <div
                class="ai-rich-content"
                v-html="renderAiHtml(item.recommendationReason || '该课程与学生当前档案较为匹配，建议优先关注。')"
              ></div>
            </div>
          </div>
        </div>
        <div v-else class="recommend-empty">
          <i class="el-icon-magic-stick" />
          <p>当前暂无可推荐课程，请先完善兴趣标签或新增更多课程。</p>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="recommendOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addStudent, delStudent, exportStudent, listStudent, updateStudent } from '@/api/edu/student'
import { recommendCourse } from '@/api/edu/course'
import { renderAiContentHtml } from '@/utils/aiContent'

export default {
  name: 'EduStudent',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      open: false,
      recommendOpen: false,
      recommendLoading: false,
      title: '',
      recommendTitle: '智能推荐课程',
      studentList: [],
      recommendList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentName: '',
        gradeName: '',
        className: '',
        status: ''
      },
      form: {},
      rules: {
        studentName: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }]
      }
    }
  },
  computed: {
    roleKeys() {
      return this.$store.getters.roles || []
    },
    isOwnerView() {
      return this.roleKeys.includes('edu_student') || this.roleKeys.includes('edu_parent')
    },
    activeCount() {
      return this.studentList.filter(item => item.status === '0').length
    },
    thirdStat() {
      if (this.isOwnerView) {
        return this.studentList.reduce((count, item) => count + this.splitTags(item.interestTags).length, 0)
      }
      return new Set(this.studentList.map(item => item.parentUserId).filter(Boolean)).size
    },
    canManageProfile() {
      return this.roleKeys.includes('admin') || this.roleKeys.includes('edu_admin')
    },
    canSelfEdit() {
      return this.roleKeys.includes('edu_student')
    },
    isStudentOwner() {
      return this.roleKeys.includes('edu_student')
    },
    ownerEmptyDescription() {
      if (this.isStudentOwner) {
        return '当前还没有个人档案，请先完善基础信息'
      }
      return '当前暂无可查看的学生档案'
    },
    formRules() {
      const rules = {
        studentName: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }]
      }
      if (!this.isStudentOwner) {
        rules.studentUserId = [{ required: true, message: '请输入学生ID', trigger: 'blur' }]
        rules.parentUserId = [{ required: true, message: '请输入家长ID', trigger: 'blur' }]
      }
      return rules
    },
    pageTitle() {
      if (this.roleKeys.includes('edu_student')) {
        return '我的档案'
      }
      if (this.roleKeys.includes('edu_parent')) {
        return '学生档案'
      }
      return '学生档案'
    },
    pageDescription() {
      if (this.roleKeys.includes('edu_student')) {
        return '集中查看自己的基础信息、班级归属、兴趣标签和智能课程推荐结果。'
      }
      if (this.roleKeys.includes('edu_parent')) {
        return '集中查看关联学生的基础档案、班级信息、兴趣标签和智能推荐课程。'
      }
      return '教师端和管理员端可查看全部学生档案，并维护学生基础信息、家长关联关系、年级班级与兴趣标签。'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    splitTags(tags) {
      if (!tags) {
        return []
      }
      return tags.split(/[，,、\s]+/).map(item => item.trim()).filter(Boolean)
    },
    renderAiHtml(content) {
      return renderAiContentHtml(content)
    },
    getList() {
      this.loading = true
      listStudent(this.queryParams).then(res => {
        this.studentList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        studentName: '',
        gradeName: '',
        className: '',
        status: ''
      }
      this.getList()
    },
    handleExport() {
      exportStudent(this.queryParams)
    },
    handleAdd() {
      this.form = { status: '0', gender: '男' }
      this.title = '新增学生档案'
      this.open = true
    },
    handleOwnerAdd() {
      this.form = {
        status: '0',
        gender: '男',
        studentUserId: this.$store.getters.id,
        studentName: this.$store.getters.nickName || this.$store.getters.name || '',
        interestTags: '',
        parentAccount: ''
      }
      this.title = '完善我的档案'
      this.open = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.title = '编辑学生档案'
      this.open = true
    },
    handleOwnerEdit(row) {
      this.form = { ...row, parentAccount: '' }
      this.title = '修改我的档案'
      this.open = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.isStudentOwner) {
          this.form.studentUserId = this.$store.getters.id
          if (!this.form.profileId && !this.form.parentAccount) {
            this.$modal.msgError('首次完善档案时，请先填写家长账号完成绑定')
            return
          }
        }
        const request = this.form.profileId ? updateStudent(this.form) : addStudent(this.form)
        request.then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除档案“${row.studentName}”吗？`).then(() => delStudent(row.profileId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    },
    handleRecommend(row) {
      this.recommendTitle = `${row.studentName} 的课程推荐`
      this.recommendOpen = true
      this.recommendLoading = true
      this.recommendList = []
      recommendCourse(row.studentUserId).then(res => {
        this.recommendList = res.data || []
        this.recommendLoading = false
      }).catch(() => {
        this.recommendLoading = false
        this.$modal.msgError('智能推荐生成时间较长，请稍后重试；若 AI 中心已有记录，可等待片刻后再次打开。')
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

.field-tip {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.6;
  color: #7a8a9d;
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
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  min-width: 360px;
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

.owner-layout,
.toolbar-panel,
.list-panel {
  position: relative;
  z-index: 1;
}

.owner-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 14px;
}

.profile-grid {
  display: grid;
  gap: 18px;
}

.profile-card,
.toolbar-panel,
.list-panel {
  border-radius: 24px;
  border: 1px solid rgba(103, 216, 219, 0.18);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(239, 253, 255, 0.88));
  box-shadow: 0 20px 34px rgba(41, 130, 141, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

.toolbar-panel {
  border-color: rgba(157, 232, 233, 0.42);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow: 0 22px 40px rgba(41, 130, 141, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
}

.profile-card {
  padding: 24px;
}

.profile-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(86, 190, 207, 0.14);
}

.profile-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 11px;
  border-radius: 999px;
  background: rgba(21, 203, 167, 0.12);
  color: #159e90;
  font-size: 12px;
  font-weight: 700;
}

.profile-head h2 {
  margin: 14px 0 8px;
  color: #163746;
  font-size: 28px;
}

.profile-head p {
  margin: 0;
  color: #6b8794;
}

.profile-body {
  display: grid;
  gap: 18px;
  margin-top: 20px;
}

.profile-section {
  padding: 20px;
  border-radius: 20px;
  border: 1px solid rgba(92, 192, 212, 0.16);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(240, 251, 255, 0.86));
}

.profile-section h3 {
  margin: 0 0 14px;
  color: #173848;
  font-size: 18px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.info-item {
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(247, 252, 255, 0.9);
  border: 1px solid rgba(122, 206, 216, 0.14);
}

.info-item span {
  display: block;
  color: #6f8894;
  font-size: 12px;
}

.info-item strong {
  display: block;
  margin-top: 8px;
  color: #163949;
  font-size: 16px;
}

.tag-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.interest-tag {
  border-color: rgba(27, 202, 168, 0.18);
  color: #117f7f;
  background: rgba(22, 205, 171, 0.08);
}

.empty-text {
  color: #7c939e;
  font-size: 13px;
}

.field-tip {
  margin-top: 6px;
  color: #7b929d;
  font-size: 12px;
  line-height: 1.6;
}

.recommend-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.recommend-section p {
  margin: 8px 0 0;
  color: #6c8794;
  line-height: 1.8;
}

.recommend-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.filter-panel {
  margin-bottom: 16px;
  padding: 18px 20px 4px;
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-header strong {
  display: block;
  color: #18394a;
  font-size: 18px;
}

.panel-header span {
  display: block;
  margin-top: 4px;
  color: #6e8794;
  font-size: 13px;
}

.toolbar-panel {
  margin-bottom: 16px;
  padding: 20px 22px 6px;
}

.toolbar-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.query-form {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  flex: 1;
  min-width: 0;
  margin-bottom: 0;
}

.query-inline-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 0 0 auto;
  margin-left: 0;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
}

::v-deep .query-form .el-form-item {
  display: inline-flex;
  align-items: center;
  margin-right: 4px;
  margin-bottom: 0;
}

::v-deep .query-form .el-form-item:nth-child(1) {
  width: 220px;
}

::v-deep .query-form .el-form-item:nth-child(2),
::v-deep .query-form .el-form-item:nth-child(3) {
  width: 180px;
}

::v-deep .query-form .el-form-item:nth-child(4) {
  width: 180px;
  margin-right: 0;
}

::v-deep .query-form .el-form-item:last-child {
  margin-right: 0;
}

::v-deep .query-form .el-form-item__label {
  flex: 0 0 auto;
  padding-right: 8px;
  line-height: 38px;
}

::v-deep .query-form .el-form-item__content {
  display: flex;
  align-items: center;
  width: auto;
  margin-left: 0 !important;
  flex: 1;
}

::v-deep .query-form .el-input,
::v-deep .query-form .el-select {
  width: 100%;
}

::v-deep .status-select .el-input__inner {
  width: 136px;
}

::v-deep .query-form .el-input__inner,
::v-deep .query-form .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 249, 255, 0.94));
  color: #355161;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

::v-deep .query-form .el-input__inner:focus,
::v-deep .query-form .el-select .el-input.is-focus .el-input__inner,
::v-deep .query-form .el-select .el-input__inner:focus {
  border-color: #25ddbf;
  box-shadow:
    0 0 0 4px rgba(37, 221, 191, 0.12),
    0 12px 22px rgba(39, 182, 194, 0.12);
}

.content-layout {
  display: block;
}

.content-table {
  margin-bottom: 10px;
}

.recommend-dialog {
  min-height: 240px;
}

.recommend-grid {
  display: grid;
  gap: 14px;
}

.recommend-card {
  padding: 18px 20px;
  border-radius: 18px;
  border: 1px solid rgba(86, 190, 207, 0.14);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(239, 250, 252, 0.92));
}

.recommend-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.recommend-head strong {
  display: block;
  color: #173542;
  font-size: 18px;
}

.recommend-head span {
  display: block;
  margin-top: 6px;
  color: #6f8792;
  font-size: 12px;
}

.recommend-meta,
.recommend-desc,
.recommend-reason p {
  margin: 12px 0 0;
  color: #5f7885;
  line-height: 1.7;
}

.recommend-reason {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(76, 171, 190, 0.12);
}

.recommend-reason span {
  color: #1f998c;
  font-size: 12px;
  font-weight: 700;
}

.ai-rich-content {
  margin-top: 12px;
  color: #46606f;
  line-height: 1.8;
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

.recommend-empty,
.empty-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 220px;
}

.name-cell,
.meta-stack {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name-cell strong,
.meta-stack span:first-child {
  color: #1b3947;
}

.name-cell span,
.meta-stack span:last-child {
  color: #738b97;
  font-size: 12px;
}

::v-deep .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  box-shadow: 0 16px 30px rgba(20, 175, 183, 0.22);
}

::v-deep .el-button--success {
  border: none;
  background: linear-gradient(135deg, #2ad7b8 0%, #2ea0ff 100%);
}

::v-deep .el-table {
  overflow: hidden;
  border-radius: 22px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

::v-deep .el-table th {
  background: #d1d5db !important;
  color: #374151 !important;
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: #eef1f4 !important;
}

::v-deep .el-input__inner,
::v-deep .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 249, 255, 0.94));
  color: #355161;
}

::v-deep .el-dialog {
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 252, 255, 0.96));
  box-shadow: 0 30px 60px rgba(25, 112, 133, 0.22);
}

@media (max-width: 1200px) {
  .hero-panel {
    flex-direction: column;
  }

  .hero-stats {
    min-width: 0;
  }

  .info-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .toolbar-main {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .hero-stats,
  .info-grid {
    grid-template-columns: 1fr;
  }

  .recommend-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .recommend-actions {
    justify-content: flex-start;
  }

  .query-form {
    flex-direction: column;
    align-items: stretch;
  }

  ::v-deep .query-form .el-form-item,
  ::v-deep .query-form .el-form-item:nth-child(1),
  ::v-deep .query-form .el-form-item:nth-child(2),
  ::v-deep .query-form .el-form-item:nth-child(3),
  ::v-deep .query-form .el-form-item:nth-child(4) {
    width: 100%;
  }
}
</style>
