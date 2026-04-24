<template>
  <div :class="['sidebar-theme-wrapper', { 'has-logo': showLogo }, settings.sideTheme, `sidebar-palette-${sidebarColorMode}`]" :style="{ backgroundColor: sidebarBackgroundColor }">
    <logo v-if="showLogo" :collapse="isCollapse" :palette-mode="sidebarColorMode" />
    <el-scrollbar :class="settings.sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="sidebarMenuBackgroundColor"
        :text-color="settings.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :active-text-color="settings.theme"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="routeKey(route, index)"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/assets/styles/variables.scss'

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapState(['settings']),
    ...mapGetters(['sidebarRouters', 'sidebar']),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    sidebarColorMode() {
      return this.$store.state.settings.sidebarColorMode || 'emerald'
    },
    variables() {
      return variables
    },
    sidebarBackgroundColor() {
      if (this.settings.sideTheme !== 'theme-dark') {
        return this.variables.menuLightBackground
      }
      return this.sidebarColorMode === 'classic' ? '#1f2b23' : '#17372f'
    },
    sidebarMenuBackgroundColor() {
      if (this.settings.sideTheme !== 'theme-dark') {
        return this.variables.menuLightBackground
      }
      return this.sidebarColorMode === 'classic' ? '#1f2b23' : '#17372f'
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  },
  methods: {
    routeKey(route, index) {
      const metaTitle = route && route.meta ? route.meta.title : ''
      const child = Array.isArray(route.children) && route.children.length ? route.children[0] : null
      const childTitle = child && child.meta ? child.meta.title : ''
      const childPath = child && child.path ? child.path : ''
      return [route.path || '', metaTitle, childTitle, childPath, index].join('::')
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar-theme-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;

  ::v-deep .el-scrollbar {
    flex: 1;
    min-height: 0;
  }
}
</style>
