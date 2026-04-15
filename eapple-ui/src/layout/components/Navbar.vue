<template>
  <div class="navbar" :class="'nav' + navType">
    <top-nav v-if="navType === 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="navType === 3">
      <logo v-show="showLogo" :collapse="false" />
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <router-link to="/user/profile" class="action-link">个人中心</router-link>
      <button type="button" class="action-link logout-link" @click="logout">退出登录</button>
    </div>
  </div>
</template>

<script>
import TopNav from './TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'

export default {
  components: {
    Logo,
    TopNav,
    TopBar
  },
  computed: {
    navType() {
      return this.$store.state.settings.navType
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    }
  },
  methods: {
    logout() {
      this.$confirm('确认退出当前平台账号吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-sizing: border-box;
  height: 52px;
  margin: 0;
  padding: 0 12px;
  overflow: hidden;
  border-bottom: 1px solid rgba(95, 222, 214, 0.16);
  border-radius: 0;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: none;
  backdrop-filter: blur(6px);

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .topbar-container {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
    overflow: hidden;
  }

  .right-menu {
    display: flex;
    align-items: center;
    height: 100%;
    margin-left: auto;

    .action-link {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      height: 34px;
      margin-left: 8px;
      padding: 0 14px;
      border: 1px solid rgba(120, 219, 222, 0.22);
      border-radius: 12px;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(239, 253, 255, 0.9));
      color: #2b6275;
      font-size: 13px;
      font-weight: 600;
      text-decoration: none;
      transition: all 0.25s ease;

      &:hover {
        border-color: rgba(31, 228, 190, 0.34);
        color: #11846f;
        box-shadow: 0 12px 22px rgba(39, 133, 146, 0.1);
      }
    }

    .logout-link {
      cursor: pointer;
      outline: none;
      background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
      color: #fff;
      border: none;
      box-shadow: 0 10px 20px rgba(20, 175, 183, 0.18);

      &:hover {
        color: #fff;
        filter: saturate(108%);
      }
    }
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 12px;

    .right-menu {
      .action-link {
        margin-left: 6px;
        padding: 0 10px;
        font-size: 12px;
      }
    }
  }
}
</style>
