<template>
  <div class="navbar" :class="'nav' + navType">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb v-if="navType === 1" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="navType === 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="navType === 3">
      <logo v-show="showLogo" :collapse="false" />
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <search id="header-search" class="right-menu-item search-shell" />
        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>

        <el-tooltip content="平台通知" effect="dark" placement="bottom">
          <header-notice id="header-notice" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="hover">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <div class="user-meta">
            <span class="meta-label">当前账号</span>
            <span class="user-nickname">{{ nickName }}</span>
          </div>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item v-if="setting" @click.native="setLayout">
            <span>界面设置</span>
          </el-dropdown-item>
          <el-dropdown-item @click.native="lockScreen">
            <span>锁定屏幕</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from './TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import HeaderNotice from './HeaderNotice'

export default {
  components: {
    Breadcrumb,
    Logo,
    TopNav,
    TopBar,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    HeaderNotice
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'device', 'nickName']),
    setting() {
      return this.$store.state.settings.showSettings
    },
    navType() {
      return this.$store.state.settings.navType
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    setLayout() {
      this.$emit('setLayout')
    },
    lockScreen() {
      const currentPath = this.$route.fullPath
      this.$store.dispatch('lock/lockScreen', currentPath).then(() => {
        this.$router.push('/lock')
      })
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
.navbar.nav3 {
  .hamburger-container {
    display: none !important;
  }
}

.navbar {
  display: flex;
  align-items: center;
  box-sizing: border-box;
  height: 62px;
  margin: 12px 14px 0;
  padding: 0 16px;
  overflow: hidden;
  border: 1px solid rgba(130, 112, 82, 0.12);
  border-radius: 22px;
  background: rgba(255, 251, 244, 0.82);
  box-shadow: 0 12px 28px rgba(55, 62, 47, 0.08);
  backdrop-filter: blur(10px);

  .hamburger-container {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    height: 42px;
    margin-right: 10px;
    padding: 0 12px;
    border-radius: 14px;
    line-height: 42px;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: rgba(202, 225, 162, 0.22);
    }
  }

  .breadcrumb-container {
    flex-shrink: 0;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .topbar-container {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
    margin-left: 8px;
    overflow: hidden;
  }

  .right-menu {
    display: flex;
    align-items: center;
    height: 100%;
    margin-left: auto;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      height: 42px;
      margin-left: 8px;
      padding: 0 12px;
      border-radius: 14px;
      color: #526054;
      font-size: 18px;
      vertical-align: middle;

      &.hover-effect {
        cursor: pointer;
        transition: all 0.25s ease;

        &:hover {
          background: rgba(202, 225, 162, 0.22);
          color: #314523;
        }
      }
    }

    .search-shell {
      padding: 0 6px;
      background: rgba(246, 241, 229, 0.85);
    }

    .avatar-container {
      padding-right: 10px;
    }

    .avatar-wrapper {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .user-avatar {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      border: 2px solid rgba(95, 143, 78, 0.24);
    }

    .user-meta {
      display: flex;
      flex-direction: column;
      line-height: 1.1;
    }

    .meta-label {
      color: #8b917e;
      font-size: 11px;
    }

    .user-nickname {
      margin-top: 4px;
      color: #2c382d;
      font-size: 14px;
      font-weight: 700;
    }
  }
}
</style>
