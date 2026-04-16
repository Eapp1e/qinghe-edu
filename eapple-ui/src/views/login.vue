<template>
  <div class="login">
    <div class="ambient ambient-a"></div>
    <div class="ambient ambient-b"></div>
    <div class="ambient ambient-c"></div>
    <div class="ambient ambient-d"></div>
    <div class="scene-grid"></div>
    <div class="scene-wave wave-a"></div>
    <div class="scene-wave wave-b"></div>
    <div class="scene-ring ring-a"></div>
    <div class="scene-ring ring-b"></div>

    <div class="login-shell">
      <div class="shell-topline"></div>
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
        <div class="form-glow form-glow-a"></div>
        <div class="form-glow form-glow-b"></div>
        <div class="form-glow form-glow-c"></div>
        <div class="form-grid"></div>
        <div class="form-shine"></div>
        <div class="form-orbit orbit-a"></div>
        <div class="form-orbit orbit-b"></div>
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
            :type="passwordVisible ? 'text' : 'password'"
            auto-complete="off"
            placeholder="请输入密码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            <span
              slot="suffix"
              class="password-toggle"
              @click="passwordVisible = !passwordVisible"
            >
              <svg-icon :icon-class="passwordVisible ? 'eye-open' : 'eye'" class="password-toggle-icon" />
            </span>
          </el-input>
        </el-form-item>

        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
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

        <div class="register-entry">
          <span>还没有账号？</span>
          <button type="button" class="register-link" @click="openRegister">立即注册</button>
        </div>
      </el-form>
    </div>

    <el-dialog
      title="注册账号"
      :visible.sync="registerDialogVisible"
      width="480px"
      custom-class="register-dialog"
      append-to-body
      @closed="resetRegisterForm"
    >
      <div class="register-dialog__intro">
        <strong>创建新账号</strong>
        <span>支持学生、家长和教师自助注册，注册成功后可直接返回登录。</span>
      </div>

      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-position="top">
        <el-form-item label="注册身份" prop="loginRole">
          <div class="register-role-grid">
            <button
              v-for="item in registerRoleOptions"
              :key="item.value"
              type="button"
              class="register-role"
              :class="{ active: registerForm.loginRole === item.value }"
              @click="selectRegisterRole(item.value)"
            >
              <strong>{{ item.label }}</strong>
              <span>{{ item.desc }}</span>
            </button>
          </div>
        </el-form-item>

        <el-form-item label="账号" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入注册账号" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            :type="registerPasswordVisible ? 'text' : 'password'"
            placeholder="请输入登录密码"
          >
            <span
              slot="suffix"
              class="password-toggle"
              @click="registerPasswordVisible = !registerPasswordVisible"
            >
              <svg-icon :icon-class="registerPasswordVisible ? 'eye-open' : 'eye'" class="password-toggle-icon" />
            </span>
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            :type="registerConfirmVisible ? 'text' : 'password'"
            placeholder="请再次输入密码"
          >
            <span
              slot="suffix"
              class="password-toggle"
              @click="registerConfirmVisible = !registerConfirmVisible"
            >
              <svg-icon :icon-class="registerConfirmVisible ? 'eye-open' : 'eye'" class="password-toggle-icon" />
            </span>
          </el-input>
        </el-form-item>
      </el-form>

      <div class="register-tip">
        注册后将自动绑定所选身份，管理员账号仍需由平台统一创建。
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">
          <span v-if="!registerLoading">提交注册</span>
          <span v-else>正在提交...</span>
        </el-button>
      </div>
    </el-dialog>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { register } from '@/api/login'
import defaultSettings from '@/settings'

export default {
  name: 'Login',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
        return
      }
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
        return
      }
      callback()
    }
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
      registerRoleOptions: [
        { value: 'edu_student', label: '学生账号', desc: '适用于自主查看课程与作业问答' },
        { value: 'edu_parent', label: '家长账号', desc: '适用于查看孩子档案和报名课程' },
        { value: 'edu_teacher', label: '教师账号', desc: '适用于发布课程和生成教学建议' }
      ],
      passwordVisible: false,
      registerPasswordVisible: false,
      registerConfirmVisible: false,
      loginRules: {
        loginRole: [{ required: true, trigger: 'change', message: '请选择登录角色' }],
        username: [{ required: true, trigger: 'blur', message: '请输入账号' }],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }]
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        loginRole: 'edu_student'
      },
      registerRules: {
        loginRole: [{ required: true, trigger: 'change', message: '请选择注册身份' }],
        username: [{ required: true, trigger: 'blur', message: '请输入注册账号' }],
        password: [
          { required: true, trigger: 'blur', message: '请输入登录密码' },
          { min: 5, max: 20, trigger: 'blur', message: '密码长度必须在 5 到 20 个字符之间' }
        ],
        confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
      },
      loading: false,
      registerDialogVisible: false,
      registerLoading: false,
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
    openRegister() {
      this.registerDialogVisible = true
      this.$nextTick(() => {
        this.$refs.registerForm && this.$refs.registerForm.clearValidate()
      })
    },
    resetRegisterForm() {
      this.registerPasswordVisible = false
      this.registerConfirmVisible = false
      this.registerLoading = false
      this.registerForm = {
        username: '',
        password: '',
        confirmPassword: '',
        loginRole: 'edu_student'
      }
    },
    selectRegisterRole(role) {
      this.registerForm.loginRole = role
      this.$nextTick(() => {
        this.$refs.registerForm && this.$refs.registerForm.clearValidate('loginRole')
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
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (!valid) {
          return
        }
        this.registerLoading = true
        register({
          username: this.registerForm.username,
          password: this.registerForm.password,
          loginRole: this.registerForm.loginRole
        }).then(response => {
          this.$message.success(response.msg || '注册成功，请返回登录')
          this.loginForm.username = this.registerForm.username
          this.loginForm.password = this.registerForm.password
          this.loginForm.loginRole = this.registerForm.loginRole
          this.registerDialogVisible = false
        }).catch(() => {
          this.registerLoading = false
        }).finally(() => {
          this.registerLoading = false
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
    radial-gradient(circle at top left, rgba(132, 245, 190, 0.34), transparent 22%),
    radial-gradient(circle at 82% 16%, rgba(255, 208, 96, 0.28), transparent 18%),
    radial-gradient(circle at bottom right, rgba(112, 214, 255, 0.24), transparent 20%),
    linear-gradient(135deg, #f4f6ef 0%, #eef5f4 44%, #e8f3ef 100%);
}

.ambient {
  position: absolute;
  border-radius: 50%;
  filter: blur(6px);
  animation: ambientFloat 10s ease-in-out infinite;
}

.ambient-a {
  top: 8%;
  left: 4%;
  width: 240px;
  height: 240px;
  background: rgba(106, 234, 178, 0.24);
}

.ambient-b {
  right: 5%;
  bottom: 8%;
  width: 260px;
  height: 260px;
  background: rgba(255, 194, 78, 0.2);
  animation-delay: -4s;
}

.ambient-c {
  top: 38%;
  right: 24%;
  width: 180px;
  height: 180px;
  background: rgba(107, 198, 255, 0.16);
  animation-delay: -2s;
}

.ambient-d {
  left: 18%;
  bottom: 16%;
  width: 220px;
  height: 220px;
  background: rgba(143, 255, 196, 0.14);
  animation-delay: -6s;
}

.scene-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(84, 168, 150, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(84, 168, 150, 0.06) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: radial-gradient(circle at center, rgba(0, 0, 0, 0.42), transparent 88%);
  pointer-events: none;
}

.scene-wave {
  position: absolute;
  border-radius: 999px;
  filter: blur(10px);
  opacity: 0.7;
  pointer-events: none;
}

.wave-a {
  left: -8%;
  top: 22%;
  width: 480px;
  height: 160px;
  background: linear-gradient(90deg, rgba(87, 236, 174, 0.22), rgba(255, 255, 255, 0));
  transform: rotate(-18deg);
}

.wave-b {
  right: -6%;
  bottom: 18%;
  width: 420px;
  height: 150px;
  background: linear-gradient(90deg, rgba(255, 196, 78, 0), rgba(255, 196, 78, 0.22));
  transform: rotate(16deg);
}

.scene-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.24);
  pointer-events: none;
  animation: orbitSpin 24s linear infinite;
}

.ring-a {
  top: 10%;
  left: 8%;
  width: 220px;
  height: 220px;
  border-color: rgba(80, 226, 179, 0.18);
}

.ring-b {
  right: 10%;
  bottom: 10%;
  width: 260px;
  height: 260px;
  border-color: rgba(88, 196, 255, 0.18);
  animation-direction: reverse;
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(360px, 430px);
  gap: 30px;
  width: min(1160px, 100%);
  padding: 24px;
  border: 1px solid rgba(120, 220, 200, 0.42);
  border-radius: 32px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.52), rgba(214, 255, 244, 0.26), rgba(223, 242, 255, 0.22)),
    rgba(246, 255, 252, 0.68);
  box-shadow:
    0 36px 92px rgba(42, 116, 122, 0.18),
    0 10px 28px rgba(255, 255, 255, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.5),
    inset 0 2px 0 rgba(124, 229, 205, 0.24);
  backdrop-filter: blur(22px) saturate(120%);
}

.login-shell::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: 32px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(76, 226, 174, 0.28), rgba(255, 194, 76, 0.22));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.login-shell::after {
  content: '';
  position: absolute;
  inset: 14px;
  border-radius: 24px;
  border: 1px solid rgba(116, 213, 214, 0.26);
  pointer-events: none;
}

.login-shell .shell-topline {
  position: absolute;
  top: 0;
  left: 26px;
  right: 26px;
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(93, 236, 178, 0.12), rgba(93, 236, 178, 0.42), rgba(255, 194, 76, 0.12));
  filter: blur(0.2px);
  pointer-events: none;
}

.login-intro {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 620px;
  padding: 30px 24px 30px 20px;
  border: 1px solid rgba(255, 255, 255, 0.76);
  border-radius: 30px;
  background:
    linear-gradient(160deg, rgba(255, 255, 255, 0.5), rgba(206, 255, 239, 0.24), rgba(228, 244, 255, 0.18)),
    rgba(247, 255, 252, 0.3);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.52),
    0 26px 58px rgba(47, 117, 127, 0.12),
    0 8px 20px rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(18px);
}

.login-intro::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: 30px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.94), rgba(80, 235, 191, 0.24), rgba(84, 194, 255, 0.16));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.login-intro::after {
  content: '';
  position: absolute;
  inset: 12px;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.14), transparent 28%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.08), transparent 52%);
  pointer-events: none;
}

.eyebrow {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  width: fit-content;
  margin: 0 0 18px;
  padding: 8px 16px;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(48, 236, 177, 0.26), rgba(188, 243, 255, 0.26));
  color: #0d7961;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.46),
    0 10px 20px rgba(47, 194, 168, 0.16);
}

.login-intro h1 {
  position: relative;
  z-index: 1;
  margin: 0;
  color: #15333b;
  font-size: 42px;
  line-height: 1.2;
  text-shadow: 0 10px 28px rgba(33, 182, 200, 0.15);
}

.subtitle {
  position: relative;
  z-index: 1;
  max-width: 540px;
  margin: 22px 0 0;
  color: #55707a;
  font-size: 16px;
  line-height: 1.9;
}

.feature-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 34px;
}

.feature-card {
  position: relative;
  padding: 22px 20px;
  border: 1px solid rgba(255, 255, 255, 0.38);
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.32), rgba(214, 255, 243, 0.14), rgba(224, 244, 255, 0.1));
  box-shadow:
    0 18px 32px rgba(46, 119, 137, 0.09),
    inset 0 1px 0 rgba(255, 255, 255, 0.42);
  backdrop-filter: blur(18px);
  overflow: hidden;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.feature-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), transparent 55%);
  pointer-events: none;
}

.feature-card:hover {
  transform: translateY(-4px);
  border-color: rgba(255, 255, 255, 0.52);
  box-shadow:
    0 24px 38px rgba(43, 145, 149, 0.14),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.feature-card strong {
  position: relative;
  z-index: 1;
  display: block;
  margin-bottom: 10px;
  color: #183844;
  font-size: 18px;
}

.feature-card span {
  position: relative;
  z-index: 1;
  color: #5a7381;
  font-size: 14px;
  line-height: 1.8;
}

.login-form {
  position: relative;
  overflow: hidden;
  align-self: center;
  width: 100%;
  padding: 34px 32px 30px;
  border: 1px solid rgba(255, 255, 255, 0.78);
  border-radius: 30px;
  background:
    linear-gradient(160deg, rgba(255, 255, 255, 0.84), rgba(221, 255, 246, 0.62) 44%, rgba(222, 240, 255, 0.58)),
    rgba(248, 255, 252, 0.66);
  box-shadow:
    0 34px 90px rgba(25, 117, 135, 0.22),
    inset 0 1px 0 rgba(255, 255, 255, 0.92),
    inset 0 -16px 28px rgba(184, 238, 255, 0.14);
  backdrop-filter: blur(28px) saturate(145%);
}

.login-form::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: 30px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.88), rgba(44, 228, 170, 0.48), rgba(64, 181, 255, 0.42), rgba(255, 188, 69, 0.26));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.login-form > * {
  position: relative;
  z-index: 1;
}

.form-glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(3px);
  opacity: 0.8;
  animation: glowPulse 8s ease-in-out infinite;
}

.form-glow-a {
  top: -34px;
  right: -18px;
  width: 160px;
  height: 160px;
  background: radial-gradient(circle, rgba(63, 228, 181, 0.34), transparent 68%);
}

.form-glow-b {
  bottom: 26px;
  left: -40px;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(255, 183, 58, 0.22), transparent 70%);
  animation-delay: -3s;
}

.form-glow-c {
  top: 34%;
  right: 8%;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(100, 197, 255, 0.22), transparent 72%);
  animation-delay: -5s;
}

.form-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(55, 178, 168, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(55, 178, 168, 0.05) 1px, transparent 1px);
  background-size: 18px 18px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.22), transparent 72%);
  pointer-events: none;
}

.form-shine {
  position: absolute;
  top: -20%;
  left: -38%;
  width: 48%;
  height: 140%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.03), rgba(255, 255, 255, 0.32), rgba(255, 255, 255, 0.04));
  transform: rotate(18deg);
  opacity: 0.75;
  animation: shineSweep 7.5s ease-in-out infinite;
  pointer-events: none;
}

.form-orbit {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.24);
  pointer-events: none;
}

.orbit-a {
  top: -70px;
  right: -90px;
  width: 220px;
  height: 220px;
  animation: orbitSpin 18s linear infinite;
}

.orbit-b {
  bottom: -54px;
  left: -58px;
  width: 170px;
  height: 170px;
  border-color: rgba(89, 226, 179, 0.18);
  animation: orbitSpinReverse 15s linear infinite;
}

.form-head {
  margin-bottom: 26px;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(53, 213, 181, 0.16);
}

.form-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(255, 183, 60, 0.48), rgba(255, 232, 166, 0.68));
  color: #9a4f00;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.7),
    0 10px 20px rgba(255, 165, 36, 0.22);
}

.form-head h3 {
  margin: 18px 0 10px;
  color: #163743;
  font-size: 32px;
  letter-spacing: 0.01em;
  text-shadow: 0 6px 20px rgba(49, 184, 149, 0.14);
}

.form-head p {
  margin: 0;
  color: #5f7482;
  line-height: 1.7;
}

.role-selector-item {
  margin-bottom: 18px;
}

.role-selector {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.role-option {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding: 15px 16px 14px;
  border: 1px solid rgba(141, 217, 220, 0.36);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(234, 250, 255, 0.92));
  color: #345667;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.22s ease, box-shadow 0.22s ease, transform 0.22s ease, background 0.22s ease;
  box-shadow:
    0 12px 24px rgba(63, 139, 156, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);
  overflow: hidden;
}

.role-option::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.34), transparent 48%);
  pointer-events: none;
}

.role-option::before {
  content: '';
  width: 8px;
  height: 8px;
  position: absolute;
  top: 14px;
  right: 14px;
  border-radius: 50%;
  background: rgba(102, 166, 194, 0.74);
  box-shadow: 0 0 0 6px rgba(102, 166, 194, 0.1);
  transition: background 0.22s ease, box-shadow 0.22s ease;
}

.role-option:hover {
  border-color: rgba(84, 223, 176, 0.62);
  box-shadow: 0 18px 32px rgba(67, 177, 145, 0.18);
  transform: translateY(-4px) scale(1.015);
}

.role-option strong {
  color: #1f4254;
  font-size: 15px;
}

.role-option span {
  color: #648093;
  font-size: 12px;
  line-height: 1.6;
}

.role-option.active {
  border-color: #2de6ba;
  background:
    radial-gradient(circle at top right, rgba(91, 255, 211, 0.56), transparent 58%),
    linear-gradient(180deg, rgba(232, 255, 249, 0.99), rgba(237, 250, 255, 0.97));
  box-shadow:
    0 22px 38px rgba(41, 189, 170, 0.26),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.role-option.active::before {
  background: #20e7bd;
  box-shadow:
    0 0 0 6px rgba(32, 231, 189, 0.16),
    0 0 18px rgba(32, 231, 189, 0.4);
}

.input-icon {
  color: #74a1b8;
  font-size: 17px;
}

.password-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  color: #4e7389 !important;
  cursor: pointer;
  opacity: 1;
  user-select: none;
  transition: color 0.2s ease, transform 0.2s ease;
}

.password-toggle:hover {
  color: #18c6b0;
  transform: scale(1.05);
}

.password-toggle-icon {
  font-size: 18px;
}

::v-deep .el-input__suffix {
  display: flex;
  align-items: center;
  right: 12px;
}

::v-deep .el-input__suffix-inner {
  display: flex;
  align-items: center;
  height: 100%;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  margin: 8px 0 24px;
  color: #667f8b;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  height: 54px;
  border: none;
  border-radius: 20px;
  background:
    linear-gradient(135deg, #1fdb98 0%, #10b981 48%, #0f9f9c 100%);
  box-shadow:
    0 20px 34px rgba(17, 167, 137, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.35);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.04em;
  transition: transform 0.22s ease, box-shadow 0.22s ease, filter 0.22s ease;
}

.login-btn:hover {
  transform: translateY(-2px) scale(1.01);
  box-shadow:
    0 28px 44px rgba(17, 167, 137, 0.36),
    inset 0 1px 0 rgba(255, 255, 255, 0.42);
  filter: saturate(108%);
}

.register-entry {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 6px;
  color: #5c7383;
  font-size: 14px;
}

.register-link {
  border: none;
  padding: 0;
  color: #0f9f9c;
  font-size: 14px;
  font-weight: 700;
  background: transparent;
  cursor: pointer;
  transition: color 0.2s ease, transform 0.2s ease;
}

.register-link:hover {
  color: #18c6b0;
  transform: translateY(-1px);
}

.register-dialog__intro {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
  color: #597182;
}

.register-dialog__intro strong {
  color: #203646;
  font-size: 18px;
}

.register-role-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.register-role {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
  min-height: 92px;
  padding: 14px 12px;
  border: 1px solid rgba(78, 181, 177, 0.18);
  border-radius: 18px;
  background: rgba(244, 252, 252, 0.88);
  color: #436273;
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.register-role strong {
  color: #203646;
  font-size: 14px;
}

.register-role span {
  line-height: 1.5;
  font-size: 12px;
}

.register-role:hover,
.register-role.active {
  border-color: rgba(16, 185, 129, 0.5);
  box-shadow: 0 16px 28px rgba(24, 153, 144, 0.14);
  transform: translateY(-2px);
}

.register-tip {
  margin-top: 10px;
  color: #718797;
  font-size: 13px;
  line-height: 1.6;
}

::v-deep .register-dialog {
  border-radius: 24px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(248, 253, 252, 0.98), rgba(239, 247, 246, 0.96));
  box-shadow: 0 28px 60px rgba(25, 71, 88, 0.22);
}

::v-deep .register-dialog .el-dialog__header {
  padding: 22px 24px 12px;
}

::v-deep .register-dialog .el-dialog__title {
  color: #1e3544;
  font-weight: 700;
}

::v-deep .register-dialog .el-dialog__body {
  padding: 0 24px 10px;
}

::v-deep .register-dialog .el-dialog__footer {
  padding: 8px 24px 24px;
}

.el-login-footer {
  position: absolute;
  right: 0;
  bottom: 18px;
  left: 0;
  z-index: 1;
  text-align: center;
  color: #718492;
  font-size: 12px;
  letter-spacing: 0.03em;
}

::v-deep .el-input__inner {
  height: 50px;
  border: 1px solid rgba(144, 214, 221, 0.52);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(237, 249, 255, 0.94));
  color: #355162;
  font-size: 15px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

::v-deep .el-input__inner:focus {
  border-color: #27dcbe;
  background: #fff;
  box-shadow:
    0 0 0 4px rgba(39, 220, 190, 0.14),
    0 12px 22px rgba(39, 189, 195, 0.14);
}

::v-deep .el-checkbox__label {
  color: #667988;
}

@media (max-width: 1100px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-intro {
    min-height: auto;
    padding-right: 18px;
  }
}

@media (max-width: 768px) {
  .login {
    padding: 18px 14px 60px;
  }

  .register-role-grid {
    grid-template-columns: 1fr;
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
    align-items: flex-start;
  }
}

@keyframes ambientFloat {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -12px, 0) scale(1.04);
  }
}

@keyframes glowPulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.08);
    opacity: 0.95;
  }
}

@keyframes shineSweep {
  0% {
    transform: translateX(0) rotate(18deg);
    opacity: 0;
  }
  18% {
    opacity: 0.72;
  }
  48% {
    transform: translateX(250%) rotate(18deg);
    opacity: 0.18;
  }
  100% {
    transform: translateX(250%) rotate(18deg);
    opacity: 0;
  }
}

@keyframes orbitSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes orbitSpinReverse {
  from {
    transform: rotate(360deg);
  }
  to {
    transform: rotate(0deg);
  }
}
</style>
