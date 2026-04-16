<template>
  <div class="app-container profile-page">
    <section class="profile-hero">
      <div class="hero-copy">
        <span class="hero-badge">Personal Center</span>
        <h1>个人中心</h1>
        <p>在这里维护个人资料、更新头像与密码设置，让账号信息更完整、安全，也更方便日常使用。</p>
      </div>
      <div class="hero-account">
        <span>当前账号</span>
        <strong>{{ user.userName || '--' }}</strong>
        <p>{{ roleGroup || '平台成员' }}</p>
      </div>
    </section>

    <section class="profile-layout">
      <el-card shadow="never" class="profile-aside">
        <div class="aside-top">
          <div class="avatar-shell">
            <userAvatar />
          </div>
          <div class="aside-meta">
            <h3>{{ user.nickName || user.userName || '平台用户' }}</h3>
            <p>{{ roleGroup || '课后服务平台成员' }}</p>
          </div>
        </div>

        <div class="summary-panel">
          <div class="summary-title">资料概览</div>
          <div class="summary-grid">
            <div class="summary-item">
              <span>登录账号</span>
              <strong>{{ user.userName || '--' }}</strong>
            </div>
            <div class="summary-item">
              <span>手机号码</span>
              <strong>{{ user.phonenumber || '--' }}</strong>
            </div>
            <div class="summary-item">
              <span>邮箱地址</span>
              <strong>{{ user.email || '--' }}</strong>
            </div>
            <div class="summary-item">
              <span>角色类型</span>
              <strong>{{ roleGroup || '--' }}</strong>
            </div>
          </div>
        </div>

        <ul class="detail-list">
          <li>
            <span>所属组织</span>
            <strong v-if="user.dept">{{ user.dept.deptName }}</strong>
            <strong v-else>--</strong>
          </li>
          <li>
            <span>岗位信息</span>
            <strong>{{ postGroup || '--' }}</strong>
          </li>
          <li>
            <span>创建时间</span>
            <strong>{{ user.createTime || '--' }}</strong>
          </li>
        </ul>
      </el-card>

      <el-card shadow="never" class="profile-main">
        <div slot="header" class="main-header">
          <div>
            <strong>账户设置</strong>
            <span>维护基础资料与安全信息</span>
          </div>
        </div>

        <el-tabs v-model="selectedTab" class="profile-tabs">
          <el-tab-pane label="基本资料" name="userinfo">
            <div class="panel-wrap">
              <div class="panel-intro">
                <strong>资料维护</strong>
                <span>更新昵称、联系方式等基础信息，保持个人资料准确完整。</span>
              </div>
              <userInfo :user="user" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="resetPwd">
            <div class="panel-wrap">
              <div class="panel-intro">
                <strong>安全设置</strong>
                <span>定期更新登录密码，提升账号安全性。</span>
              </div>
              <resetPwd />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
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
  display: grid;
  gap: 20px;
}

.profile-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 32px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(21, 34, 46, 0.96), rgba(29, 73, 82, 0.92));
  color: #fff;
}

.hero-badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(32, 224, 182, 0.14);
  color: #8ff6dc;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.hero-copy h1 {
  margin: 14px 0 10px;
  font-size: 34px;
}

.hero-copy p {
  margin: 0;
  max-width: 760px;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.8;
}

.hero-account {
  min-width: 220px;
  padding: 18px 20px;
  border-radius: 20px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16);
}

.hero-account span {
  display: block;
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.hero-account strong {
  display: block;
  margin-top: 10px;
  color: #ffffff;
  font-size: 28px;
  font-weight: 700;
}

.hero-account p {
  margin: 10px 0 0;
  color: rgba(222, 240, 243, 0.72);
}

.profile-layout {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 18px;
}

.profile-aside,
.profile-main {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 16px 40px rgba(31, 107, 127, 0.08);
}

.profile-aside ::v-deep .el-card__body,
.profile-main ::v-deep .el-card__body {
  padding: 24px;
}

.aside-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 22px;
  border-bottom: 1px solid rgba(121, 156, 168, 0.12);
}

.avatar-shell :deep(.user-info-head) {
  padding: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(219, 245, 238, 0.92), rgba(227, 241, 214, 0.92));
  box-shadow: 0 16px 34px rgba(58, 76, 66, 0.12);
}

.avatar-shell :deep(.img-circle) {
  border: 4px solid rgba(255, 255, 255, 0.92);
  box-shadow: 0 8px 24px rgba(44, 57, 49, 0.12);
}

.aside-meta {
  margin-top: 16px;
  text-align: center;
}

.aside-meta h3 {
  margin: 0;
  color: #22384a;
  font-size: 22px;
}

.aside-meta p {
  margin: 8px 0 0;
  color: #7a919f;
}

.summary-panel {
  margin-top: 20px;
  padding: 18px;
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(237, 251, 252, 0.96), rgba(246, 250, 253, 0.98));
  border: 1px solid rgba(77, 171, 192, 0.16);
}

.summary-title {
  color: #22384a;
  font-size: 16px;
  font-weight: 700;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.summary-item {
  padding: 14px 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.86);
}

.summary-item span {
  display: block;
  color: #7a919f;
  font-size: 12px;
}

.summary-item strong {
  display: block;
  margin-top: 8px;
  color: #22384a;
  font-size: 14px;
}

.detail-list {
  margin: 20px 0 0;
  padding: 0;
  list-style: none;
}

.detail-list li {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px dashed rgba(121, 156, 168, 0.14);
}

.detail-list li:last-child {
  border-bottom: none;
}

.detail-list span {
  color: #7a919f;
  font-size: 13px;
}

.detail-list strong {
  color: #22384a;
  font-size: 13px;
  text-align: right;
}

.main-header {
  display: flex;
  align-items: center;
}

.main-header strong {
  display: block;
  color: #22384a;
  font-size: 18px;
}

.main-header span {
  display: block;
  margin-top: 6px;
  color: #7a919f;
  font-size: 13px;
}

.profile-main ::v-deep .el-card__header {
  padding: 22px 24px 0;
  border-bottom: none;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(121, 156, 168, 0.12);
}

.profile-tabs :deep(.el-tabs__item) {
  height: 44px;
  line-height: 44px;
  font-weight: 700;
}

.profile-tabs :deep(.el-tabs__active-bar) {
  border-radius: 999px;
  background: linear-gradient(90deg, #20e0b6, #3095ff);
}

.panel-wrap {
  padding-top: 6px;
}

.panel-intro {
  margin-bottom: 18px;
  padding: 16px 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(237, 251, 252, 0.96), rgba(246, 250, 253, 0.98));
  border: 1px solid rgba(77, 171, 192, 0.14);
}

.panel-intro strong {
  display: block;
  color: #22384a;
  font-size: 16px;
}

.panel-intro span {
  display: block;
  margin-top: 8px;
  color: #7a919f;
  line-height: 1.7;
}

.profile-tabs :deep(.el-form) {
  max-width: 620px;
}

@media (max-width: 1100px) {
  .profile-layout,
  .profile-hero {
    grid-template-columns: 1fr;
    display: grid;
  }

  .hero-account {
    min-width: 0;
  }
}

@media (max-width: 768px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .profile-hero {
    padding: 24px;
  }
}
</style>