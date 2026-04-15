<template>
  <div class="app-container profile-page">
    <section class="profile-hero">
      <div class="hero-copy">
        <span class="hero-badge">Personal Center</span>
        <h1>个人中心</h1>
        <p>在这里维护个人资料、更新头像和修改密码，让课后服务平台的个人信息更加完整、安全且便于展示。</p>
      </div>
      <div class="hero-note">
        <strong>当前账号</strong>
        <span>{{ user.userName || '--' }}</span>
      </div>
    </section>

    <div class="profile-grid">
      <el-card class="profile-summary" shadow="never">
        <div class="summary-head">
          <div class="avatar-shell">
            <userAvatar />
          </div>
          <div class="summary-meta">
            <h3>{{ user.nickName || user.userName || '平台用户' }}</h3>
            <p>{{ roleGroup || '课后服务平台成员' }}</p>
          </div>
        </div>

        <ul class="info-list">
          <li>
            <span>登录账号</span>
            <strong>{{ user.userName || '--' }}</strong>
          </li>
          <li>
            <span>手机号码</span>
            <strong>{{ user.phonenumber || '--' }}</strong>
          </li>
          <li>
            <span>邮箱地址</span>
            <strong>{{ user.email || '--' }}</strong>
          </li>
          <li>
            <span>所属组织</span>
            <strong v-if="user.dept">{{ user.dept.deptName }} / {{ postGroup || '未设置岗位' }}</strong>
            <strong v-else>--</strong>
          </li>
          <li>
            <span>角色类型</span>
            <strong>{{ roleGroup || '--' }}</strong>
          </li>
          <li>
            <span>创建时间</span>
            <strong>{{ user.createTime || '--' }}</strong>
          </li>
        </ul>
      </el-card>

      <el-card class="profile-main" shadow="never">
        <div slot="header">账户设置</div>
        <el-tabs v-model="selectedTab" class="profile-tabs">
          <el-tab-pane label="基本资料" name="userinfo">
            <userInfo :user="user" />
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="resetPwd">
            <resetPwd />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
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
      roleGroup: {},
      postGroup: {},
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
        this.user = response.data
        this.roleGroup = response.roleGroup
        this.postGroup = response.postGroup
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding-top: 6px;
}

.profile-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 28px;
  border: 1px solid rgba(130, 112, 82, 0.12);
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(207, 225, 186, 0.4), transparent 24%),
    linear-gradient(135deg, #fffaf1 0%, #f1f6ef 100%);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: #e6efd3;
  color: #456b25;
  font-size: 12px;
  font-weight: 700;
}

.hero-copy h1 {
  margin: 16px 0 12px;
  color: #243a24;
  font-size: 34px;
}

.hero-copy p {
  margin: 0;
  color: #68756b;
  line-height: 1.9;
}

.hero-note {
  min-width: 190px;
  padding: 18px;
  border: 1px solid rgba(130, 112, 82, 0.1);
  border-radius: 20px;
  background: rgba(255, 252, 247, 0.86);
}

.hero-note strong {
  display: block;
  color: #748174;
  font-size: 13px;
}

.hero-note span {
  display: block;
  margin-top: 10px;
  color: #253128;
  font-size: 28px;
  font-weight: 700;
}

.profile-grid {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 18px;
}

.summary-head {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(130, 112, 82, 0.1);
}

.avatar-shell :deep(.user-info-head) {
  padding: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(223, 238, 231, 0.9), rgba(230, 239, 211, 0.95));
  box-shadow: 0 16px 34px rgba(58, 76, 66, 0.12);
}

.avatar-shell :deep(.img-circle) {
  border: 4px solid rgba(255, 255, 255, 0.92);
  box-shadow: 0 8px 24px rgba(44, 57, 49, 0.12);
}

.summary-meta {
  margin-top: 16px;
  text-align: center;
}

.summary-meta h3 {
  margin: 0;
  color: #253128;
  font-size: 22px;
}

.summary-meta p {
  margin: 8px 0 0;
  color: #7b857b;
}

.info-list {
  margin: 18px 0 0;
  padding: 0;
  list-style: none;
}

.info-list li {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px dashed rgba(130, 112, 82, 0.12);
}

.info-list li:last-child {
  border-bottom: none;
}

.info-list span {
  color: #7d857b;
  font-size: 13px;
}

.info-list strong {
  color: #2b372c;
  font-size: 13px;
  text-align: right;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(130, 112, 82, 0.1);
}

.profile-tabs :deep(.el-tabs__item) {
  font-weight: 600;
}

.profile-tabs :deep(.el-form) {
  max-width: 520px;
  padding-top: 4px;
}

.profile-tabs :deep(.el-form-item:last-child) {
  margin-top: 14px;
}

@media (max-width: 992px) {
  .profile-grid,
  .profile-hero {
    display: grid;
    grid-template-columns: 1fr;
  }

  .hero-note {
    min-width: 0;
  }
}
</style>
