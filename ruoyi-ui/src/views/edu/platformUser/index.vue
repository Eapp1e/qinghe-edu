<template>
  <div class="platform-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">管理员后台</p>
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

    <section class="toolbar-card">
      <el-form :model="queryParams" inline size="small">
        <el-form-item label="账号">
          <el-input v-model="queryParams.userName" placeholder="如 edu_teacher" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.nickName" placeholder="如 李老师 / 王家长" clearable @keyup.enter.native="getList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部状态">
            <el-option label="启用" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="getList">查询</el-button>
          <el-button size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="quick-actions">
        <el-button type="primary" plain size="small" @click="handleAdd">新增平台用户</el-button>
      </div>
    </section>

    <section class="guide-panel">
      <div class="guide-item" v-for="item in roleGuide" :key="item.key">
        <strong>{{ item.label }}</strong>
        <span>{{ item.desc }}</span>
      </div>
    </section>

    <el-table v-loading="loading" :data="userList" class="content-table">
      <el-table-column label="账号" prop="userName" min-width="140" />
      <el-table-column label="姓名" prop="nickName" min-width="120" />
      <el-table-column label="角色类型" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="roleTagType(resolveRoleLabel(scope.row))">
            {{ resolveRoleLabel(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系方式" prop="phonenumber" min-width="130" />
      <el-table-column label="所属部门" min-width="130">
        <template slot-scope="scope">
          {{ scope.row.dept && scope.row.dept.deptName ? scope.row.dept.deptName : '-' }}
        </template>
      </el-table-column>
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
          <el-button type="text" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色类型" prop="roleIds">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
                <el-option v-for="item in eduRoleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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

export default {
  name: 'EduPlatformUserPage',
  data() {
    return {
      loading: false,
      open: false,
      total: 0,
      userList: [],
      allRoles: [],
      dialogTitle: '新增平台用户',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: '',
        nickName: '',
        status: ''
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
        postIds: [],
        roleIds: []
      },
      roleGuide: [
        { key: 'admin', label: '平台管理员', desc: '负责平台用户、课程、AI日志与公告的统一维护。' },
        { key: 'teacher', label: '教师', desc: '负责发布课程、查看报名与使用 AI 生成教学内容。' },
        { key: 'parent', label: '家长', desc: '负责为孩子报名课程并查看学习记录与互动历史。' },
        { key: 'student', label: '学生', desc: '负责查看课程、提交作业问题并获取 AI 解答。' }
      ],
      rules: {
        userName: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        nickName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        roleIds: [{ type: 'array', required: true, message: '请选择角色类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    eduRoleOptions() {
      return this.allRoles.filter(item => ['edu_admin', 'edu_teacher', 'edu_parent', 'edu_student'].includes(item.roleKey))
    },
    enabledCount() {
      return this.userList.filter(item => item.status === '0').length
    }
  },
  created() {
    this.getRoleOptions()
    this.getList()
  },
  methods: {
    getRoleOptions() {
      listRole({ pageNum: 1, pageSize: 100 }).then(res => {
        this.allRoles = res.rows || []
      })
    },
    getList() {
      this.loading = true
      listUser(this.queryParams).then(res => {
        const rows = res.rows || []
        this.userList = rows.filter(item => !item.userName || item.userName === 'admin' || item.userName.startsWith('edu_'))
        this.total = this.userList.length
      }).finally(() => {
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, userName: '', nickName: '', status: '' }
      this.getList()
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
        postIds: [],
        roleIds: []
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
    },
    resolveRoleLabel(row) {
      if (row.userName === 'admin' || row.userName === 'edu_admin') return '平台管理员'
      if (row.userName === 'edu_teacher') return '教师'
      if (row.userName === 'edu_parent') return '家长'
      if (row.userName === 'edu_student') return '学生'
      return '平台用户'
    },
    roleTagType(label) {
      if (label.includes('管理员')) return 'danger'
      if (label.includes('教师')) return 'success'
      if (label.includes('家长')) return 'warning'
      if (label.includes('学生')) return ''
      return 'info'
    },
    handleAdd() {
      this.resetFormData()
      this.dialogTitle = '新增平台用户'
      this.open = true
    },
    handleUpdate(row) {
      this.resetFormData()
      getUser(row.userId).then(res => {
        this.form = {
          ...this.form,
          ...res.data,
          password: '',
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
        inputErrorMessage: '密码长度需为 5 到 20 位'
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
        const request = this.form.userId ? updateUser(this.form) : addUser(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.userId ? '保存成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.platform-page { padding: 0 0 8px; }
.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 18px;
  padding: 24px 26px;
  border-radius: 20px;
  background: linear-gradient(135deg, #eff7ff 0%, #f8fbff 58%, #eefaf0 100%);
  border: 1px solid #dce9f7;
}
.eyebrow {
  margin: 0 0 10px;
  color: #4273a5;
  font-size: 12px;
  letter-spacing: 1.2px;
  text-transform: uppercase;
}
.page-hero h1 { margin: 0; color: #1b3d63; font-size: 28px; }
.summary { max-width: 680px; margin: 12px 0 0; color: #61758d; line-height: 1.8; }
.hero-stats { display: grid; grid-template-columns: repeat(2, minmax(120px, 1fr)); gap: 12px; }
.stat-card { padding: 16px; border-radius: 18px; background: rgba(255,255,255,.88); text-align: center; }
.stat-card span { display: block; color: #66809a; font-size: 12px; }
.stat-card strong { display: block; margin-top: 8px; color: #1d4870; font-size: 28px; }
.toolbar-card, .guide-panel, .content-table { margin-bottom: 18px; }
.toolbar-card { padding: 18px 18px 4px; border: 1px solid #e7eef7; border-radius: 18px; background: #fff; }
.quick-actions { margin-top: 4px; }
.guide-panel { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; }
.guide-item { padding: 16px 18px; border-radius: 16px; background: #fff; border: 1px solid #e7eef7; }
.guide-item strong { display: block; margin-bottom: 8px; color: #234569; }
.guide-item span { display: block; color: #6d8095; font-size: 13px; line-height: 1.7; }
.content-table { border-radius: 18px; overflow: hidden; }
.danger-text { color: #f56c6c; }
@media (max-width: 900px) {
  .page-hero { flex-direction: column; }
  .guide-panel { grid-template-columns: 1fr; }
}
</style>
