<template>
  <div :class="[classObj, `bg-mode-${backgroundMode}`]" class="app-wrapper" :style="layoutStyle">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <sidebar v-if="!sidebar.hide" class="sidebar-container"/>
    <div :class="{sidebarHide:sidebar.hide}" class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar @setLayout="setLayout"/>
      </div>
      <app-main/>
      <settings ref="settingRef"/>
    </div>
  </div>
</template>

<script>
import { AppMain, Navbar, Settings, Sidebar } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    Settings,
    Sidebar
  },
  mixins: [ResizeMixin],
  computed: {
    ...mapState({
      theme: state => state.settings.theme,
      sideTheme: state => state.settings.sideTheme,
      backgroundMode: state => state.settings.backgroundMode,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      fixedHeader: state => state.settings.fixedHeader
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables
    },
    layoutStyle() {
      const warmShell = '#d9e2cc'
      const warmSurface = '#d9e2cc'
      const classicShell = '#d9e2cc'
      const classicSurface = '#f7f7f5'
      const isWarm = this.backgroundMode !== 'classic'
      return {
        '--current-color': this.theme,
        '--current-color-light': this.theme + '1a',
        '--current-color-dark-bg': this.theme + '33',
        '--shell-bg': isWarm ? warmShell : classicShell,
        '--surface-bg': isWarm ? warmSurface : classicSurface,
        '--nav-bg': isWarm ? warmSurface : '#ffffff',
        '--tags-bg': isWarm ? warmSurface : '#ffffff'
      }
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    setLayout() {
      this.$refs.settingRef.openSetting()
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    background: var(--shell-bg);

    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
  }

  .main-container {
    background: transparent;
  }

  .main-container:has(.fixed-header) {
    height: 100vh;
    overflow: hidden;
  }

  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$base-sidebar-width});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px);
  }

  .sidebarHide .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }
</style>
