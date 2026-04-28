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
    <div class="sidebar-role-row" :style="{ backgroundColor: sidebarEmptyBackgroundColor }">
      <div class="sidebar-role-badge" :class="{ 'is-collapse': isCollapse }">{{ roleTerminalText }}</div>
    </div>
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
    ...mapGetters(['sidebarRouters', 'sidebar', 'roles']),
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
    sidebarEmptyBackgroundColor() {
      if (this.settings.sideTheme !== 'theme-dark') {
        return this.variables.menuLightBackground
      }
      return this.sidebarColorMode === 'classic' ? '#1b2820' : '#133027'
    },
    isCollapse() {
      return !this.sidebar.opened
    },
    roleTerminalText() {
      const roleKeys = this.roles || []
      if (roleKeys.includes('edu_student')) return '学生端'
      if (roleKeys.includes('edu_parent')) return '家长端'
      if (roleKeys.includes('edu_teacher')) return '教师端'
      if (roleKeys.includes('admin') || roleKeys.includes('edu_admin')) return '管理端'
      return '平台端'
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

.sidebar-role-row {
  flex-shrink: 0;
}

.sidebar-role-badge {
  margin: 8px 14px 6px;
  padding: 7px 12px;
  border: 1px solid rgba(230, 246, 241, 0.16);
  border-radius: 999px;
  background: rgba(23, 55, 47, 0.82);
  color: rgba(231, 246, 241, 0.82);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
  font-family: "KaiTi", "STKaiti", "Kaiti SC", serif;
  font-size: 13px;
  font-weight: 600;
  line-height: 1;
  letter-spacing: 0.06em;
  text-align: center;
  backdrop-filter: blur(8px);
}

.sidebar-role-badge.is-collapse {
  margin: 8px 9px 6px;
  padding: 7px 0;
  background: rgba(23, 55, 47, 0.82);
  color: rgba(231, 246, 241, 0.7);
  font-size: 12px;
  text-align: center;
}

</style>
