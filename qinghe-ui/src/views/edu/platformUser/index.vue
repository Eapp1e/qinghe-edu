<template>
  <div class="app-container platform-page">
    <section class="page-hero">
      <div>
        <span class="hero-badge">Platform User</span>
        <h1>平台用户管理</h1>
        <p class="summary">
          统一维护学生、家长、教师与管理员账号，便于展示平台角色协同、权限控制和基础信息维护能力。
        </p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>当前用户数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>启用账号</span>
          <strong>{{ enabledCount }}</strong>
        </div>
      </div>
    </section>

    <section class="guide-panel">
      <div class="guide-item" v-for="item in roleGuide" :key="item.key">
        <strong>{{ item.label }}</strong>
        <span>{{ item.desc }}</span>
      </div>
    </section>

    <section class="toolbar-panel">
      <div class="toolbar-main">
        <el-form :model="queryParams" inline size="small" class="query-form">
          <el-form-item label="账号">
            <el-input v-model="queryParams.userName" placeholder="如 edu_teacher" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="queryParams.nickName" placeholder="如 李老师 / 王家长" clearable @keyup.enter.native="getList" />
          </el-form-item>
          <el-form-item label="角色" class="status-select">
            <el-select v-model="queryParams.roleType" clearable placeholder="全部角色">
              <el-option label="系统管理员" value="admin" />
              <el-option label="管理员" value="edu_admin" />
              <el-option label="教师" value="edu_teacher" />
              <el-option label="家长" value="edu_parent" />
              <el-option label="学生" value="edu_student" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" class="status-select">
            <el-select v-model="queryParams.status" clearable placeholder="全部状态">
              <el-option label="启用" value="0" />
              <el-option label="停用" value="1" />
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

        <div class="toolbar-actions">
          <el-button type="primary" plain size="small" class="toolbar-gradient-btn" @click="handleAdd">新建用户</el-button>
          <el-button size="small" icon="el-icon-download" @click="handleExport">导出</el-button>
          <right-toolbar @queryTable="getList" />
        </div>
      </div>
    </section>

    <section class="table-section-card">
    <el-table v-loading="loading" :data="userList" class="content-table">
      <el-table-column label="ID" prop="userId" width="90" align="center" />
      <el-table-column label="账号" prop="userName" min-width="140" />
      <el-table-column label="姓名" min-width="120">
        <template slot-scope="scope">
          {{ resolveDisplayName(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="角色类型" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="roleTagType(resolveRoleLabel(scope.row))">
            {{ resolveRoleLabel(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="isSystemAdmin" label="所属学校" min-width="130">
        <template slot-scope="scope">{{ resolveSchoolName(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="教师类型" min-width="120">
        <template slot-scope="scope">{{ teacherTypeLabel(scope.row.teacherType) }}</template>
      </el-table-column>
      <el-table-column label="联系方式" prop="phonenumber" min-width="130" />
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="220" align="center">
        <template slot-scope="scope">
          <el-button v-if="canEditUser(scope.row)" type="text" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" size="mini" @click="handleResetPwd(scope.row)">重置密码</el-button>
          <el-button v-if="scope.row.userId !== 1" type="text" size="mini" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
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
    </section>

    <el-dialog :title="dialogTitle" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色类型" prop="roleId">
              <el-select v-model="form.roleId" placeholder="请选择角色">
                <el-option
                  v-for="item in eduRoleOptions"
                  :key="item.roleId"
                  :label="displayRoleOptionName(item)"
                  :value="item.roleId"
                  :disabled="item.roleKey === 'edu_admin' && !isSystemAdmin"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="isTeacherRoleSelected" :gutter="16">
          <el-col :span="12">
            <el-form-item label="教师类型" prop="teacherType">
              <el-select v-model="form.teacherType" placeholder="请选择教师类型">
                <el-option v-for="item in teacherTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属学校">
          <el-input v-model="form.schoolName" disabled />
          <div class="school-lock-tip">当前测试阶段，学校绑定后暂不支持修改</div>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item v-if="form.userId === undefined" label="账号" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入登录账号" />
            </el-form-item>
            <el-form-item v-else label="账号">
              <el-input v-model="form.userName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId === undefined" label="密码" prop="password">
              <el-input v-model="form.password" type="password" show-password placeholder="请输入初始密码" />
            </el-form-item>
            <el-form-item v-else label="登录状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">启用</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可填写用户在课后服务平台中的身份说明" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addUser, changeUserStatus, delUser, getUser, listUser, resetUserPwd, updateUser } from '@/api/system/user'
import { listRole } from '@/api/system/role'
import { listStudent } from '@/api/edu/student'
import { saveAs } from 'file-saver'

export default {
  name: 'EduPlatformUserPage',
  data() {
    return {
      loading: false,
      open: false,
      total: 0,
      userList: [],
      studentProfiles: [],
      allRoles: [],
      dialogTitle: '新增平台用户',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: '',
        nickName: '',
        status: '',
        roleType: ''
      },
      form: {
        userId: undefined,
        deptId: 103,
        userName: '',
        nickName: '',
        password: 'admin123',
        phonenumber: '',
        email: '',
        sex: '0',
        status: '0',
        remark: '',
        teacherType: '',
        schoolId: 1,
        schoolName: '青禾学校',
        roleId: undefined,
        postIds: [],
        roleIds: []
      },
      teacherTypeOptions: [
        { value: 'science', label: '理科' },
        { value: 'humanities', label: '文科' },
        { value: 'art', label: '美育' },
        { value: 'sports', label: '体育' },
        { value: 'computer', label: '计算机' },
        { value: 'general', label: '综合实践' }
      ],
      roleGuide: [
        { key: 'admin', label: '管理员', desc: '负责平台用户、课程、AI日志与公告的统一维护。' },
        { key: 'teacher', label: '教师', desc: '负责发布课程、查看报名与使用 AI 生成教学内容。' },
        { key: 'parent', label: '家长', desc: '负责为孩子报名课程并查看学习记录与互动历史。' },
        { key: 'student', label: '学生', desc: '负责查看课程、提交作业问题并获取 AI 解答。' }
      ],
      rules: {
        userName: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        nickName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        roleId: [{ required: true, message: '请选择角色类型', trigger: 'change' }],
        teacherType: [{ required: true, message: '请选择教师类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    eduRoleOptions() {
      return this.allRoles.filter(item => ['edu_admin', 'edu_teacher', 'edu_parent', 'edu_student'].includes(item.roleKey))
    },
    enabledCount() {
      return this.userList.filter(item => item.status === '0').length
    },
    isTeacherRoleSelected() {
      const teacherRole = this.allRoles.find(item => item.roleKey === 'edu_teacher')
      return teacherRole && this.form.roleId === teacherRole.roleId
    },
    isSystemAdmin() {
      return Number(this.$store.getters.id) === 1 || (this.$store.getters.roles || []).includes('admin')
    }
  },
  created() {
    Promise.all([this.getRoleOptions(), this.getStudentProfiles()]).finally(() => {
      this.getList()
    })
  },
  methods: {
    getRoleOptions() {
      return listRole({ pageNum: 1, pageSize: 100 }).then(res => {
        this.allRoles = res.rows || []
      })
    },
    getStudentProfiles() {
      return listStudent({ pageNum: 1, pageSize: 500 }).then(res => {
        this.studentProfiles = res.rows || []
      })
    },
    getList() {
      this.loading = true
      const requestParams = {
        ...this.queryParams,
        pageNum: 1,
        pageSize: 500
      }
      listUser(requestParams).then(async res => {
        const rows = res.rows || []
        const filteredRows = rows
          .filter(item => this.isEduPlatformUser(item))
          .filter(item => this.matchesRoleFilter(item))
          .sort((a, b) => (a.userId || 0) - (b.userId || 0))
        this.total = filteredRows.length
        const start = (this.queryParams.pageNum - 1) * this.queryParams.pageSize
        const end = start + this.queryParams.pageSize
        this.userList = await this.enrichUserRoles(filteredRows.slice(start, end))
      }).finally(() => {
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, userName: '', nickName: '', status: '', roleType: '' }
      this.getList()
    },
    async handleExport() {
      try {
        const requestParams = {
          ...this.queryParams,
          pageNum: 1,
          pageSize: 500
        }
        const res = await listUser(requestParams)
        const rows = res.rows || []
        const filteredRows = rows
          .filter(item => this.isEduPlatformUser(item))
          .sort((a, b) => (a.userId || 0) - (b.userId || 0))
        const enrichedRows = (await this.enrichUserRoles(filteredRows)).filter(item => this.matchesRoleFilter(item))
        const csvRows = [
          ['用户ID', '账号', '姓名', '角色类型', '联系方式', '状态', '备注']
        ].concat(
          enrichedRows.map(item => [
            this.escapeCsvValue(item.userId),
            this.escapeCsvValue(item.userName),
            this.escapeCsvValue(this.resolveDisplayName(item)),
            this.escapeCsvValue(this.resolveRoleLabel(item)),
            this.escapeCsvValue(item.phonenumber || ''),
            this.escapeCsvValue(item.status === '0' ? '启用' : '停用'),
            this.escapeCsvValue(item.remark || '')
          ])
        )
        const content = '\uFEFF' + csvRows.map(row => row.join(',')).join('\r\n')
        saveAs(new Blob([content], { type: 'text/csv;charset=utf-8;' }), `platform_user_${new Date().getTime()}.csv`)
      } catch (error) {
        this.$modal.msgWarning('平台用户导出失败，请稍后重试')
      }
    },
    resetFormData() {
      this.form = {
        userId: undefined,
        deptId: 103,
        userName: '',
        nickName: '',
        password: 'admin123',
        phonenumber: '',
        email: '',
        sex: '0',
        status: '0',
        remark: '',
        teacherType: '',
        schoolId: 1,
        schoolName: '青禾学校',
        roleId: undefined,
        postIds: [],
        roleIds: []
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
    },
    resolveRoleLabel(row) {
      const roleKeys = this.extractRoleKeys(row)
      const remark = row.remark || ''
      const hasStudentProfile = this.studentProfiles.some(item => Number(item.studentUserId) === Number(row.userId))
      const hasParentProfile = this.studentProfiles.some(item => Number(item.parentUserId) === Number(row.userId))

      if (row.userName === 'admin' || roleKeys.includes('admin')) {
        return '系统管理员'
      }
      if (roleKeys.includes('edu_admin') || remark.includes('管理员')) {
        return '管理员'
      }
      if (roleKeys.includes('edu_teacher') || (row.userName && row.userName.startsWith('edu_teacher')) || remark.includes('教师')) {
        return '教师'
      }
      if (roleKeys.includes('edu_parent') || (row.userName && row.userName.startsWith('edu_parent')) || hasParentProfile || remark.includes('家长')) {
        return '家长'
      }
      if (roleKeys.includes('edu_student') || (row.userName && row.userName.startsWith('edu_student')) || hasStudentProfile || remark.includes('学生')) {
        return '学生'
      }
      return '学生'
    },
    resolveRoleKey(row) {
      const label = this.resolveRoleLabel(row)
      const map = {
        系统管理员: 'admin',
        管理员: 'edu_admin',
        教师: 'edu_teacher',
        家长: 'edu_parent',
        学生: 'edu_student'
      }
      return map[label] || ''
    },
    resolveDisplayName(row) {
      const roleKeys = this.extractRoleKeys(row)
      const profile = this.studentProfiles.find(item => Number(item.studentUserId) === Number(row.userId))
      if (profile && profile.studentName) {
        return profile.studentName
      }
      if (roleKeys.includes('edu_parent')) {
        const parentProfile = this.studentProfiles.find(item => Number(item.parentUserId) === Number(row.userId) && item.parentName)
        if (parentProfile && parentProfile.parentName) {
          return parentProfile.parentName
        }
      }
      return row.nickName || row.userName || '--'
    },
    isEduPlatformUser(row) {
      if (!row) return false
      if (['ry', 'xiaozhi'].includes(row.userName)) return false
      if (row.userName === 'admin') return true
      const roleKeys = this.extractRoleKeys(row)
      if (roleKeys.some(key => ['admin', 'edu_admin', 'edu_teacher', 'edu_parent', 'edu_student'].includes(key))) {
        return true
      }
      if (Number(row.userId) >= 110) {
        return true
      }
      if (Number(row.deptId) === 103) {
        return true
      }
      if (row.userName && /^edu_/.test(row.userName)) {
        return true
      }
      return false
    },
    extractRoleKeys(row) {
      const roles = Array.isArray(row.roles) ? row.roles : []
      return roles.map(item => item.roleKey).filter(Boolean)
    },
    async enrichUserRoles(rows) {
      const results = await Promise.all(rows.map(async item => {
        try {
          const res = await getUser(item.userId)
          const roleIds = res.roleIds || []
          const roles = this.allRoles.filter(role => roleIds.includes(role.roleId))
          return {
            ...item,
            roles
          }
        } catch (error) {
          return item
        }
      }))
      return results
    },
    roleTagType(label) {
      if (label.includes('管理员')) return 'danger'
      if (label.includes('教师')) return 'success'
      if (label.includes('家长')) return 'warning'
      if (label.includes('学生')) return ''
      return 'info'
    },
    matchesRoleFilter(row) {
      if (!this.queryParams.roleType) return true
      const roleKeys = this.extractRoleKeys(row)
      if (this.queryParams.roleType === 'admin') {
        return row.userName === 'admin' || roleKeys.includes('admin') || this.resolveRoleKey(row) === 'admin'
      }
      return roleKeys.includes(this.queryParams.roleType) || this.resolveRoleKey(row) === this.queryParams.roleType
    },
    displayRoleOptionName(role) {
      if (!role) return ''
      if (role.roleKey === 'edu_admin') return '管理员'
      return role.roleName
        .replace('课后平台管理员', '管理员')
        .replace('课后服务管理员', '管理员')
        .replace('平台管理员', '管理员')
    },
    isPlatformAdminRow(row) {
      return this.extractRoleKeys(row).includes('edu_admin')
    },
    isSystemAdminRow(row) {
      return row && (row.userName === 'admin' || this.extractRoleKeys(row).includes('admin'))
    },
    isSelfRow(row) {
      return row && Number(row.userId) === Number(this.$store.getters.id)
    },
    canEditUser(row) {
      if (this.isSystemAdminRow(row)) {
        return this.isSystemAdmin
      }
      if (this.isSelfRow(row)) {
        return true
      }
      if (this.isPlatformAdminRow(row)) {
        return this.isSystemAdmin
      }
      return true
    },
    teacherTypeLabel(value) {
      const values = String(value || '').split(',').map(item => item.trim()).filter(Boolean)
      if (!values.length) return '--'
      return values.map(type => {
        const hit = this.teacherTypeOptions.find(item => item.value === type)
        return hit ? hit.label : type
      }).join('、')
    },
    escapeCsvValue(value) {
      const text = value == null ? '' : String(value)
      return `"${text.replace(/"/g, '""')}"`
    },
    resolveSchoolName(row) {
      if (row && row.userName === 'admin') {
        return '全平台'
      }
      return (row && row.schoolName) || '青禾学校'
    },
    handleAdd() {
      this.resetFormData()
      this.dialogTitle = '新增平台用户'
      this.open = true
    },
    handleUpdate(row) {
      if (!this.canEditUser(row)) {
        this.$modal.msgWarning('只有系统管理员可以编辑管理员账号')
        return
      }
      this.resetFormData()
      getUser(row.userId).then(res => {
        this.form = {
          ...this.form,
          ...res.data,
          password: '',
          schoolName: this.resolveSchoolName(res.data),
          roleId: this.resolveEditableRoleId(res.roleIds || []),
          roleIds: res.roleIds || [],
          postIds: res.postIds || []
        }
        this.dialogTitle = '编辑平台用户'
        this.open = true
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      changeUserStatus(row.userId, row.status).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    handleResetPwd(row) {
      this.$prompt(`请输入 ${row.userName} 的新密码`, '重置密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: '密码长度需在 5 到 20 位'
      }).then(({ value }) => {
        resetUserPwd(row.userId, value).then(() => {
          this.$modal.msgSuccess('密码已重置：' + value)
        })
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除平台用户 ${row.userName} 吗？`).then(() => {
        return delUser(row.userId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (!this.isTeacherRoleSelected) {
          this.form.teacherType = ''
        }
        if (!this.isSystemAdmin && Number(this.form.userId) !== Number(this.$store.getters.id)) {
          const adminRole = this.allRoles.find(item => item.roleKey === 'edu_admin')
          if (adminRole && this.form.roleId === adminRole.roleId) {
            this.$modal.msgWarning('只有系统管理员可以创建或编辑管理员账号')
            return
          }
        }
        const payload = {
          ...this.form,
          roleIds: this.form.roleId ? [this.form.roleId] : []
        }
        const request = this.form.userId ? updateUser(payload) : addUser(payload)
        request.then(() => {
          this.$modal.msgSuccess(this.form.userId ? '保存成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    resolveEditableRoleId(roleIds) {
      const ids = roleIds || []
      const eduRoleIds = this.eduRoleOptions.map(item => item.roleId)
      return ids.find(id => eduRoleIds.includes(id)) || ids[0]
    }
  }
}
</script>

<style lang="scss" scoped>
.platform-page {
  position: relative;
  display: grid;
  gap: 18px;
  padding-top: 6px;
  overflow: hidden;
}

.school-lock-tip {
  margin-top: 6px;
  color: #6f8796;
  font-size: 12px;
  line-height: 1.5;
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
.toolbar-panel,
.content-table {
  border-radius: 22px;
}

.stat-card,
.guide-item {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16);
}

.stat-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
}

.guide-item {
  background: #ffffff;
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

.guide-panel {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.guide-item {
  min-height: 118px;
}

.guide-item strong {
  display: block;
  margin-bottom: 8px;
  color: #2d3b33;
}

.guide-item span {
  color: #6c776f;
  line-height: 1.8;
  font-size: 14px;
}

.toolbar-panel {
  position: relative;
  z-index: 1;
  padding: 16px 18px;
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

.toolbar-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.query-form {
  flex: 1;
  margin-bottom: 0;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  flex-shrink: 0;
}

.content-table {
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.danger-text {
  color: #ef5753;
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
  background: var(--table-header-bg, #d6dbd4) !important;
  color: var(--table-header-text, #3f4a42) !important;
}

::v-deep .el-table th:first-child .cell,
::v-deep .el-table td:first-child .cell {
  padding-left: 18px;
}

::v-deep .el-table tr {
  background-color: rgba(255, 255, 255, 0.9);
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: #f2f4ef !important;
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

::v-deep .el-switch.is-checked .el-switch__core {
  border-color: #18d6bc;
  background-color: #18d6bc;
}

::v-deep .el-pagination .btn-next,
::v-deep .el-pagination .btn-prev,
::v-deep .el-pagination .el-pager li {
  border-radius: 12px;
}

@media (max-width: 992px) {
  .page-hero,
  .toolbar-main {
    flex-direction: column;
    align-items: stretch;
  }

  .guide-panel,
  .hero-stats {
    grid-template-columns: 1fr;
  }

  .toolbar-actions {
    justify-content: flex-start;
  }
}
</style>
