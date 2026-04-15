<template>
  <div class="login">
    <div class="ambient ambient-a"></div>
    <div class="ambient ambient-b"></div>

    <div class="login-shell">
      <section class="login-intro">
        <p class="eyebrow">After-school Service Platform</p>
        <h1>中小学智能课后服务平台</h1>
        <p class="subtitle">
          面向学校课后服务管理场景，围绕学生、家长、教师与管理员四类角色，
          提供课程发布、在线报名、学习跟踪、作业问答、平台通知与 AI 辅助服务。
        </p>

        <div class="feature-grid">
          <div class="feature-card">
            <strong>课程服务</strong>
            <span>统一管理兴趣课程、授课教师、上课时间与报名容量，保障课后课程组织有序。</span>
          </div>
          <div class="feature-card">
            <strong>家校协同</strong>
            <span>支持家长查看学生档案、学习记录和报名状态，提升课后服务透明度。</span>
          </div>
          <div class="feature-card">
            <strong>教学辅助</strong>
            <span>教师可借助 AI 生成课程通知、教学建议和作业答疑内容，提高工作效率。</span>
          </div>
          <div class="feature-card">
            <strong>安全运营</strong>
            <span>管理员统一查看用户、通知、统计报表与 AI 调用日志，确保平台运行可控。</span>
          </div>
        </div>
      </section>

      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-head">
          <span class="form-badge">身份认证</span>
          <h3>欢迎登录</h3>
          <p>请选择登录身份，并输入账号与密码进入对应工作台。</p>
        </div>

        <el-form-item prop="loginRole" class="role-selector-item">
          <div class="role-selector">
            <button
              v-for="item in roleOptions"
              :key="item.value"
              type="button"
              class="role-option"
              :class="{ active: loginForm.loginRole === item.value }"
              @click="selectRole(item.value)"
            >
              <strong>{{ item.label }}</strong>
              <span>{{ item.desc }}</span>
            </button>
          </div>
        </el-form-item>

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="请输入账号"
          >
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="请输入密码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          <span class="login-tip">建议由平台管理员统一分配账号权限</span>
        </div>

        <el-form-item style="width: 100%">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            class="login-btn"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登录平台</span>
            <span v-else>正在登录...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: 'Login',
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
        loginRole: 'edu_student',
        code: '',
        uuid: ''
      },
      roleOptions: [
        { value: 'edu_student', label: '学生登录', desc: '查看课程与提交作业问题' },
        { value: 'edu_parent', label: '家长登录', desc: '报名课程与查看学习记录' },
        { value: 'edu_teacher', label: '教师登录', desc: '发布课程与处理教学事务' },
        { value: 'edu_admin', label: '管理员登录', desc: '管理平台用户、通知和统计数据' }
      ],
      loginRules: {
        loginRole: [{ required: true, trigger: 'change', message: '请选择登录角色' }],
        username: [{ required: true, trigger: 'blur', message: '请输入账号' }],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }]
      },
      loading: false,
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCookie()
  },
  methods: {
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      const loginRole = Cookies.get('loginRole')
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
        loginRole: loginRole === undefined ? this.loginForm.loginRole : loginRole
      }
    },
    selectRole(role) {
      this.loginForm.loginRole = role
      this.$nextTick(() => {
        this.$refs.loginForm.clearValidate('loginRole')
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        if (this.loginForm.rememberMe) {
          Cookies.set('username', this.loginForm.username, { expires: 30 })
          Cookies.set('password', encrypt(this.loginForm.password), { expires: 30 })
          Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          Cookies.set('loginRole', this.loginForm.loginRole, { expires: 30 })
        } else {
          Cookies.remove('username')
          Cookies.remove('password')
          Cookies.remove('rememberMe')
          Cookies.remove('loginRole')
        }
        this.$store.dispatch('Login', this.loginForm).then(() => {
          this.$router.push({ path: this.redirect || '/' }).catch(() => {})
        }).catch(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  padding: 42px 24px 76px;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(208, 229, 176, 0.5), transparent 25%),
    radial-gradient(circle at bottom right, rgba(255, 214, 153, 0.25), transparent 24%),
    linear-gradient(135deg, #f7f1e6 0%, #f7f8f0 45%, #eef6ea 100%);
}

.ambient {
  position: absolute;
  border-radius: 50%;
  filter: blur(6px);
}

.ambient-a {
  top: 8%;
  left: 4%;
  width: 240px;
  height: 240px;
  background: rgba(191, 219, 149, 0.28);
}

.ambient-b {
  right: 5%;
  bottom: 8%;
  width: 260px;
  height: 260px;
  background: rgba(244, 202, 138, 0.18);
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(360px, 430px);
  gap: 30px;
  width: min(1160px, 100%);
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 32px;
  background: rgba(255, 252, 246, 0.72);
  box-shadow: 0 28px 60px rgba(138, 132, 112, 0.14);
  backdrop-filter: blur(16px);
}

.login-intro {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 620px;
  padding: 28px 18px 28px 12px;
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  margin: 0 0 18px;
  padding: 8px 16px;
  border-radius: 999px;
  background: rgba(119, 152, 86, 0.12);
  color: #6d8b53;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.login-intro h1 {
  margin: 0;
  color: #2f3d2f;
  font-size: 42px;
  line-height: 1.2;
}

.subtitle {
  max-width: 540px;
  margin: 22px 0 0;
  color: #66725f;
  font-size: 16px;
  line-height: 1.9;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 34px;
}

.feature-card {
  padding: 22px 20px;
  border: 1px solid rgba(181, 196, 162, 0.2);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.75);
  box-shadow: 0 18px 32px rgba(121, 129, 100, 0.08);
}

.feature-card strong {
  display: block;
  margin-bottom: 10px;
  color: #314230;
  font-size: 18px;
}

.feature-card span {
  color: #71806d;
  font-size: 14px;
  line-height: 1.8;
}

.login-form {
  align-self: center;
  width: 100%;
  padding: 34px 32px 30px;
  border: 1px solid rgba(214, 206, 187, 0.7);
  border-radius: 28px;
  background: rgba(255, 251, 245, 0.94);
  box-shadow: 0 20px 38px rgba(109, 106, 83, 0.12);
}

.form-head {
  margin-bottom: 26px;
}

.form-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(243, 184, 116, 0.16);
  color: #b46f22;
  font-size: 12px;
  font-weight: 700;
}

.form-head h3 {
  margin: 18px 0 10px;
  color: #2e3b2d;
  font-size: 28px;
}

.form-head p {
  margin: 0;
  color: #7c806f;
  line-height: 1.7;
}

.role-selector-item {
  margin-bottom: 18px;
}

.role-selector {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.role-option {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding: 14px 16px;
  border: 1px solid rgba(202, 197, 183, 0.75);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);
  color: #55614d;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.role-option:hover {
  border-color: rgba(133, 165, 102, 0.48);
  box-shadow: 0 14px 24px rgba(123, 144, 99, 0.12);
  transform: translateY(-1px);
}

.role-option strong {
  color: #304130;
  font-size: 15px;
}

.role-option span {
  color: #7a806e;
  font-size: 12px;
  line-height: 1.6;
}

.role-option.active {
  border-color: #86a965;
  background: linear-gradient(180deg, rgba(241, 248, 233, 0.96), rgba(255, 255, 255, 0.96));
  box-shadow: 0 16px 30px rgba(122, 152, 91, 0.16);
}

.input-icon {
  color: #a7af9d;
  font-size: 17px;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 8px 0 24px;
  color: #7d7b73;
  font-size: 13px;
}

.login-tip {
  color: #8c8a7b;
}

.login-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #8cab6d 0%, #6f9253 100%);
  box-shadow: 0 16px 28px rgba(112, 146, 82, 0.28);
  font-size: 16px;
  font-weight: 700;
}

.el-login-footer {
  position: absolute;
  right: 0;
  bottom: 18px;
  left: 0;
  z-index: 1;
  text-align: center;
  color: #8d8776;
  font-size: 12px;
  letter-spacing: 0.03em;
}

::v-deep .el-input__inner {
  height: 50px;
  border: 1px solid rgba(204, 197, 183, 0.72);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  color: #4a5449;
  font-size: 15px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

::v-deep .el-input__inner:focus {
  border-color: #8cab6d;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(140, 171, 109, 0.12);
}

::v-deep .el-checkbox__label {
  color: #767366;
}

@media (max-width: 1100px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-intro {
    min-height: auto;
    padding-right: 0;
  }
}

@media (max-width: 768px) {
  .login {
    padding: 18px 14px 60px;
  }

  .login-shell {
    padding: 16px;
    border-radius: 24px;
  }

  .login-intro {
    padding: 14px 4px 4px;
  }

  .login-intro h1 {
    font-size: 30px;
  }

  .subtitle {
    font-size: 14px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .role-selector {
    grid-template-columns: 1fr;
  }

  .login-form {
    padding: 24px 18px 22px;
    border-radius: 22px;
  }

  .login-options {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
