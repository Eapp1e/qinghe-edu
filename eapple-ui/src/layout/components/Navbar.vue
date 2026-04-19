<template>
  <div class="navbar" :class="'nav' + navType">
    <top-nav v-if="navType === 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="navType === 3">
      <logo v-show="showLogo" :collapse="false" />
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <div class="brand-watermark">QINGHE SMART LEARNING</div>
      <router-link to="/user/profile" class="action-link">个人中心</router-link>
      <el-dropdown trigger="click" @command="handleCommand">
        <button type="button" class="action-link settings-link icon-only" title="设置">
          <i class="el-icon-setting" />
        </button>
        <el-dropdown-menu slot="dropdown" class="settings-dropdown">
          <el-dropdown-item command="background">切换背景色</el-dropdown-item>
          <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
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
    },
    backgroundMode() {
      return this.$store.state.settings.backgroundMode || 'warm'
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'background') {
        const nextMode = this.backgroundMode === 'classic' ? 'warm' : 'classic'
        this.$store.dispatch('settings/changeSetting', {
          key: 'backgroundMode',
          value: nextMode
        })
        return
      }
      if (command === 'logout') {
        this.logout()
      }
    },
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
  padding: 0 14px;
  overflow: hidden;
  border-bottom: 1px solid rgba(129, 224, 224, 0.16);
  border-radius: 0;
  background: var(--nav-bg);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 10px 24px rgba(15, 35, 46, 0.06);

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

    .brand-watermark {
      display: inline-flex;
      align-items: center;
      height: 100%;
      margin-right: 10px;
      color: rgba(45, 89, 101, 0.42);
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 0.24em;
      white-space: nowrap;
      text-transform: uppercase;
      pointer-events: none;
      user-select: none;
    }

    .action-link {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      height: 34px;
      margin-left: 8px;
      padding: 0 14px;
      border: 1px solid rgba(140, 226, 227, 0.2);
      border-radius: 12px;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.34), rgba(255, 255, 255, 0.12));
      color: #204f60;
      font-size: 13px;
      font-weight: 600;
      text-decoration: none;
      box-shadow:
        inset 0 1px 0 rgba(255, 255, 255, 0.18),
        0 8px 18px rgba(15, 35, 46, 0.05);
      transition: all 0.25s ease;

      &:hover {
        border-color: rgba(31, 228, 190, 0.3);
        color: #0f8570;
        background: linear-gradient(180deg, rgba(216, 252, 246, 0.26), rgba(180, 239, 236, 0.14));
        box-shadow:
          inset 0 1px 0 rgba(255, 255, 255, 0.22),
          0 12px 22px rgba(39, 133, 146, 0.08);
      }
    }

    .settings-link {
      cursor: pointer;
      outline: none;
      background: linear-gradient(135deg, rgba(18, 224, 169, 0.92) 0%, rgba(16, 199, 196, 0.86) 52%, rgba(42, 152, 255, 0.88) 100%);
      color: #fff;
      border: none;
      box-shadow:
        inset 0 1px 0 rgba(255, 255, 255, 0.18),
        0 10px 20px rgba(20, 175, 183, 0.16);

      &:hover {
        color: #fff;
        background: linear-gradient(135deg, rgba(20, 214, 170, 0.96) 0%, rgba(17, 191, 191, 0.9) 52%, rgba(39, 141, 240, 0.92) 100%);
        box-shadow:
          inset 0 1px 0 rgba(255, 255, 255, 0.22),
          0 12px 22px rgba(20, 175, 183, 0.2);
        filter: none;
      }
    }

    .icon-only {
      width: 34px;
      min-width: 34px;
      padding: 0;

      i {
        margin: 0;
        font-size: 14px;
      }
    }
  }
}

::v-deep .settings-dropdown {
  border-radius: 14px;
  border: 1px solid rgba(160, 205, 184, 0.28);
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 16px 32px rgba(38, 76, 82, 0.12);
  overflow: hidden;
}

::v-deep .settings-dropdown .el-dropdown-menu__item {
  color: #214a58;
  font-weight: 600;
}

::v-deep .settings-dropdown .el-dropdown-menu__item:focus,
::v-deep .settings-dropdown .el-dropdown-menu__item:not(.is-disabled):hover {
  background: rgba(226, 246, 237, 0.72);
  color: #0f8570;
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 12px;

    .right-menu {
      .brand-watermark {
        display: none;
      }

      .action-link {
        margin-left: 6px;
        padding: 0 10px;
        font-size: 12px;
      }
    }
  }
}
</style>
