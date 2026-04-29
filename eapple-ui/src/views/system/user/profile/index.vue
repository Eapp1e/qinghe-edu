<template>
  <div class="app-container profile-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Account Settings</span>
        <h1>个人中心</h1>
        <p>维护基础资料、头像与登录密码，让平台账号保持准确、安全、清爽。</p>
      </div>
      <div class="hero-stats hero-stats--compact">
        <div class="stat-card">
          <span>当前身份</span>
          <strong>{{ roleGroup || '平台成员' }}</strong>
        </div>
        <div class="stat-card">
          <span>登录账号</span>
          <strong>{{ user.userName || '--' }}</strong>
        </div>
      </div>
    </section>

    <section class="profile-shell">
      <aside class="profile-card">
        <div class="avatar-block">
          <userAvatar />
          <h2>{{ user.nickName || user.userName || '平台用户' }}</h2>
          <p>{{ roleGroup || '青禾智学平台成员' }}</p>
        </div>
        <div class="identity-strip">
          <span>登录账号</span>
          <strong>{{ user.userName || '--' }}</strong>
        </div>
        <div class="profile-facts">
          <div class="fact-item">
            <span>系统ID</span>
            <strong>{{ user.userId || '--' }}</strong>
          </div>
          <div class="fact-item">
            <span>手机号码</span>
            <strong>{{ user.phonenumber || '--' }}</strong>
          </div>
          <div class="fact-item">
            <span>邮箱地址</span>
            <strong>{{ user.email || '--' }}</strong>
          </div>
        </div>
        <div class="create-time">
          <span>创建时间</span>
          <strong>{{ user.createTime || '--' }}</strong>
        </div>
      </aside>

      <main class="settings-panel">
        <div class="settings-tabs">
          <button type="button" :class="['tab-card', { active: selectedTab === 'userinfo' }]" @click="selectedTab = 'userinfo'">
            <i class="el-icon-user" />
            <span>基本资料</span>
            <strong>昵称、手机、邮箱</strong>
          </button>
          <button type="button" :class="['tab-card', { active: selectedTab === 'resetPwd' }]" @click="selectedTab = 'resetPwd'">
            <i class="el-icon-lock" />
            <span>安全设置</span>
            <strong>修改登录密码</strong>
          </button>
        </div>

        <section class="form-card">
          <div class="form-card-head">
            <h3>{{ selectedTab === 'userinfo' ? '资料维护' : '密码安全' }}</h3>
            <p>{{ selectedTab === 'userinfo' ? '请保持联系方式准确，便于平台通知和身份核对。' : '建议使用 6-20 位不含特殊风险字符的安全密码。' }}</p>
          </div>
          <userInfo v-if="selectedTab === 'userinfo'" :user="user" />
          <resetPwd v-else />
        </section>
      </main>
    </section>
  </div>
</template>

<script>
import userAvatar from './userAvatar'
import userInfo from './userInfo'
import resetPwd from './resetPwd'
import { getUserProfile } from '@/api/system/user'

export default {
  name: 'Profile',
  components: { userAvatar, userInfo, resetPwd },
  data() {
    return {
      user: {},
      roleGroup: '',
      postGroup: '',
      selectedTab: 'userinfo'
    }
  },
  created() {
    const activeTab = this.$route.params && this.$route.params.activeTab
    if (activeTab) {
      this.selectedTab = activeTab
    }
    this.getUser()
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data || {}
        this.roleGroup = response.roleGroup || ''
        this.postGroup = response.postGroup || ''
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 120px);
  padding-top: 8px;
  background:
    radial-gradient(circle at 92% 0%, rgba(118, 201, 183, 0.14), transparent 28%),
    linear-gradient(180deg, #f6f8f1 0%, #eef5ef 100%);
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
  max-width: 720px;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.9;
}

.profile-shell {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  align-items: stretch;
  gap: 22px;
}

.profile-card,
.settings-panel,
.form-card {
  border: 1px solid #dfe8dc;
  border-radius: 28px;
  background: #fffdf8;
  box-shadow: 0 18px 34px rgba(54, 82, 64, 0.08);
}

.profile-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 28px;
}

.settings-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 28px;
}

.avatar-block {
  text-align: center;
}

.avatar-block :deep(.user-info-head) {
  height: auto;
}

.avatar-block :deep(.img-circle) {
  width: 122px;
  height: 122px;
  border: 5px solid #edf5e9;
  box-shadow: 0 12px 26px rgba(54, 82, 64, 0.14);
}

.avatar-block h2 {
  margin: 18px 0 8px;
  color: #203648;
  font-size: 24px;
}

.avatar-block p {
  margin: 0;
  color: #6d7f72;
}

.identity-strip {
  margin-top: 24px;
  padding: 18px;
  border-radius: 20px;
  background: #203b3f;
  color: #fff;
}

.identity-strip span,
.create-time span,
.fact-item span {
  display: block;
  color: #7a8b7d;
  font-size: 12px;
  font-weight: 700;
}

.identity-strip span {
  color: rgba(236, 248, 238, 0.68);
}

.identity-strip strong {
  display: block;
  margin-top: 8px;
  font-size: 22px;
}

.profile-facts {
  display: grid;
  gap: 12px;
  margin-top: 18px;
}

.fact-item {
  padding: 16px;
  border: 1px solid #e4eadf;
  border-radius: 18px;
  background: #fbfcf7;
}

.fact-item strong {
  display: block;
  margin-top: 8px;
  color: #203648;
  word-break: break-all;
}

.create-time {
  margin-top: auto;
  padding-top: 18px;
  border-top: 1px dashed #d7e1d4;
}

.create-time strong {
  display: block;
  margin-top: 8px;
  color: #405447;
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
  display: block;
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  max-width: 150px;
  margin-top: 10px;
  overflow: hidden;
  color: #ffffff;
  font-size: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.settings-tabs {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.tab-card {
  display: grid;
  grid-template-columns: 46px 1fr;
  column-gap: 14px;
  align-items: center;
  min-height: 88px;
  padding: 18px;
  border: 1px solid #dde7db;
  border-radius: 22px;
  background: #fbfcf7;
  text-align: left;
  cursor: pointer;
  transition: all 0.18s ease;
}

.tab-card i {
  grid-row: span 2;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 16px;
  background: #e7f1e2;
  color: #527a5c;
  font-size: 22px;
}

.tab-card span {
  color: #203648;
  font-size: 18px;
  font-weight: 800;
}

.tab-card strong {
  color: #7a8b7d;
  font-size: 13px;
}

.tab-card.active {
  border-color: #7fab78;
  background: #f4f8ed;
  box-shadow: 0 12px 22px rgba(82, 122, 92, 0.12);
}

.form-card {
  flex: 1;
  padding: 26px;
}

.form-card-head {
  margin-bottom: 20px;
}

.form-card h3 {
  margin: 0 0 8px;
  color: #203648;
  font-size: 24px;
}

.form-card p {
  margin: 0;
  color: #7a8b7d;
}

.form-card :deep(.el-form) {
  max-width: 650px;
}

.form-card :deep(.el-input__inner) {
  height: 42px;
  border-color: #d7e1d4;
  border-radius: 14px;
}

.form-card :deep(.el-button--primary) {
  border: none;
  background: linear-gradient(135deg, #4f8d62, #73a866);
}

@media (max-width: 1100px) {
  .profile-shell {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .hero-panel,
  .settings-tabs {
    grid-template-columns: 1fr;
  }

  .hero-panel {
    display: grid;
  }

  .hero-stats {
    min-width: 0;
    grid-template-columns: 1fr;
  }
}
</style>
