<template>
  <div class="login">
    <div class="ambient ambient-a"></div>
    <div class="ambient ambient-b"></div>

    <div class="login-shell">
      <section class="login-intro">
        <p class="eyebrow">Smart After-school Platform</p>
        <h1>中小学智能课后服务平台</h1>
        <p class="subtitle">
          围绕学生、家长、教师和管理员四类角色，提供课程报名、学习记录、作业问答、平台通知和 AI 辅助服务。
        </p>

        <div class="feature-grid">
          <div class="feature-card">
            <strong>学生问答</strong>
            <span>提交作业问题，快速获得 AI 辅助解题思路。</span>
          </div>
          <div class="feature-card">
            <strong>家长协同</strong>
            <span>查看孩子档案、学习轨迹与课程报名情况。</span>
          </div>
          <div class="feature-card">
            <strong>教师教学</strong>
            <span>发布课程、管理名单并生成教学建议和通知。</span>
          </div>
          <div class="feature-card">
            <strong>平台治理</strong>
            <span>统一管理用户、通知、AI 日志和统计看板。</span>
          </div>
        </div>
      </section>

      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-head">
          <span class="form-badge">演示入口</span>
          <h3>{{ title }}</h3>
          <p>使用演示账号可快速体验四类角色流程</p>
        </div>

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

        <el-form-item v-if="captchaEnabled" prop="code" class="captcha-row">
          <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="请输入验证码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" class="login-code-img" @click="getCode" />
          </div>
        </el-form-item>

        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
        </div>

        <el-form-item style="width: 100%">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            class="login-btn"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">进入平台</span>
            <span v-else>正在登录...</span>
          </el-button>
        </el-form-item>

        <div class="demo-panel">
          <div class="demo-panel-head">
            <strong>一键切换演示账号</strong>
            <span>默认密码均为 `admin123`</span>
          </div>
          <div class="demo-list">
            <button
              v-for="item in demos"
              :key="item.username"
              type="button"
              class="demo-item"
              @click="useDemo(item)"
            >
              <strong>{{ item.role }}</strong>
              <span>{{ item.username }}</span>
            </button>
          </div>
        </div>
      </el-form>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from '@/api/login'
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: 'Login',
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: '',
      loginForm: {
        username: 'edu_admin',
        password: 'admin123',
        rememberMe: false,
        code: '',
        uuid: ''
      },
      demos: [
        { role: '管理员', username: 'edu_admin', password: 'admin123' },
        { role: '教师', username: 'edu_teacher', password: 'admin123' },
        { role: '家长', username: 'edu_parent', password: 'admin123' },
        { role: '学生', username: 'edu_student', password: 'admin123' }
      ],
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '请输入账号' }],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
        code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
      },
      loading: false,
      captchaEnabled: true,
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
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = 'data:image/gif;base64,' + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    useDemo(item) {
      this.loginForm.username = item.username
      this.loginForm.password = item.password
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
        } else {
          Cookies.remove('username')
          Cookies.remove('password')
          Cookies.remove('rememberMe')
        }
        this.$store.dispatch('Login', this.loginForm).then(() => {
          this.$router.push({ path: this.redirect || '/' }).catch(() => {})
        }).catch(() => {
          this.loading = false
          if (this.captchaEnabled) {
            this.getCode()
          }
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
  grid-template-columns: 1.08fr 0.92fr;
  gap: 28px;
  width: min(1160px, 100%);
}

.login-intro,
.login-form {
  border: 1px solid rgba(130, 112, 82, 0.12);
  border-radius: 28px;
  box-shadow: 0 22px 60px rgba(58, 63, 48, 0.1);
  backdrop-filter: blur(10px);
}

.login-intro {
  padding: 42px;
  background:
    radial-gradient(circle at top right, rgba(201, 222, 164, 0.22), transparent 30%),
    linear-gradient(160deg, #223225 0%, #2c4631 100%);
  color: #f7f6f0;
}

.eyebrow {
  margin: 0 0 14px;
  color: #d7ebbb;
  font-size: 12px;
  letter-spacing: 1.8px;
  text-transform: uppercase;
}

.login-intro h1 {
  margin: 0;
  font-size: 42px;
  line-height: 1.15;
  letter-spacing: 1px;
}

.subtitle {
  margin: 18px 0 0;
  color: rgba(247, 246, 240, 0.8);
  line-height: 1.9;
  font-size: 15px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 34px;
}

.feature-card {
  padding: 18px;
  border: 1px solid rgba(216, 233, 183, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
}

.feature-card strong {
  display: block;
  margin-bottom: 10px;
  color: #f7f4e8;
  font-size: 17px;
}

.feature-card span {
  color: rgba(247, 246, 240, 0.72);
  line-height: 1.7;
  font-size: 13px;
}

.login-form {
  padding: 34px 30px 28px;
  background: rgba(255, 252, 245, 0.94);
}

.form-head h3 {
  margin: 12px 0 6px;
  color: #243025;
  font-size: 30px;
}

.form-head p {
  margin: 0 0 24px;
  color: #7a8175;
}

.form-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: #e6efd3;
  color: #456b25;
  font-size: 12px;
  font-weight: 700;
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 110px;
  gap: 12px;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  color: #6d766a;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.demo-panel {
  margin-top: 14px;
  padding: 16px;
  border: 1px solid rgba(130, 112, 82, 0.1);
  border-radius: 18px;
  background: #fbf8f1;
}

.demo-panel-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 14px;
}

.demo-panel-head strong {
  color: #2d382d;
}

.demo-panel-head span {
  color: #8a8f84;
  font-size: 12px;
}

.demo-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.demo-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 14px 14px 12px;
  border: 1px solid rgba(130, 112, 82, 0.12);
  border-radius: 16px;
  background: #fffdf8;
  cursor: pointer;
  transition: all 0.22s ease;
}

.demo-item:hover {
  transform: translateY(-2px);
  border-color: rgba(95, 143, 78, 0.4);
  box-shadow: 0 12px 22px rgba(95, 143, 78, 0.12);
}

.demo-item strong {
  color: #314523;
  font-size: 15px;
}

.demo-item span {
  margin-top: 6px;
  color: #7b8478;
  font-size: 12px;
}

.login-code {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.login-code-img {
  width: 100%;
  height: 46px;
  border-radius: 14px;
  object-fit: cover;
  cursor: pointer;
}

.el-login-footer {
  position: absolute;
  bottom: 22px;
  left: 0;
  right: 0;
  z-index: 1;
  color: #707866;
  text-align: center;
  font-size: 13px;
}

@media (max-width: 1024px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .feature-grid,
  .demo-list {
    grid-template-columns: 1fr;
  }
}
</style>
