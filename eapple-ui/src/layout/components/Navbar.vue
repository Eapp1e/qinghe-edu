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
          <el-dropdown-item command="sidebarColor">切换侧栏颜色</el-dropdown-item>
          <el-dropdown-item command="sidebarSort">侧栏排序</el-dropdown-item>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <el-dialog
      title="侧栏排序"
      :visible.sync="sidebarSortDialogVisible"
      width="520px"
      append-to-body
      custom-class="sidebar-sort-dialog"
    >
      <div class="sort-mode-group">
        <el-radio-group v-model="sidebarSortForm.mode" size="small">
          <el-radio-button label="default">默认排序</el-radio-button>
          <el-radio-button label="custom">自定义排序</el-radio-button>
        </el-radio-group>
      </div>

      <p class="sort-tip">
        {{ sidebarSortForm.mode === 'custom' ? '拖动下面的模块卡片，调整侧栏显示顺序。' : '使用系统当前默认的侧栏顺序。' }}
      </p>

      <draggable
        v-model="sidebarSortItems"
        :disabled="sidebarSortForm.mode !== 'custom'"
        handle=".drag-handle"
        animation="180"
        ghost-class="sort-item-ghost"
        class="sort-list"
      >
        <transition-group type="transition" name="sort-list">
          <div
            v-for="item in sidebarSortItems"
            :key="item.key"
            class="sort-item"
            :class="{ disabled: sidebarSortForm.mode !== 'custom' }"
          >
            <span class="drag-handle el-icon-rank"></span>
            <span class="sort-title">{{ item.title }}</span>
          </div>
        </transition-group>
      </draggable>

      <div slot="footer">
        <el-button @click="sidebarSortDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSidebarSort">保存设置</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import TopNav from './TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'

export default {
  components: {
    draggable,
    Logo,
    TopNav,
    TopBar
  },
  data() {
    return {
      sidebarSortDialogVisible: false,
      sidebarSortForm: {
        mode: 'default'
      },
      sidebarSortItems: [],
      defaultSidebarItems: [],
      currentSidebarItems: []
    }
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
    },
    sidebarColorMode() {
      return this.$store.state.settings.sidebarColorMode || 'emerald'
    },
    sidebarSortMode() {
      return this.getSidebarSortSetting().sidebarSortMode || 'default'
    },
    sidebarCustomOrder() {
      return this.getSidebarSortSetting().sidebarCustomOrder || []
    }
  },
  watch: {
    'sidebarSortForm.mode'(val) {
      if (!this.sidebarSortDialogVisible) {
        return
      }
      this.sidebarSortItems = this.getDialogSidebarItems(val)
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
      if (command === 'sidebarColor') {
        const nextMode = this.sidebarColorMode === 'classic' ? 'emerald' : 'classic'
        this.$store.dispatch('settings/changeSetting', {
          key: 'sidebarColorMode',
          value: nextMode
        })
        this.$message.success(nextMode === 'classic' ? '已切换为经典侧栏色' : '已切换为青禾侧栏色')
        return
      }
      if (command === 'sidebarSort') {
        this.openSidebarSortDialog()
        return
      }
      if (command === 'logout') {
        this.logout()
      }
    },
    openSidebarSortDialog() {
      this.sidebarSortForm.mode = this.sidebarSortMode
      this.defaultSidebarItems = this.extractSidebarModules(this.$store.state.permission.defaultRoutes || [])
      this.currentSidebarItems = this.extractSidebarModules(this.$store.getters.sidebarRouters || [])
      this.sidebarSortItems = this.getDialogSidebarItems(this.sidebarSortForm.mode)
      this.sidebarSortDialogVisible = true
    },
    getDialogSidebarItems(mode) {
      if (mode !== 'custom') {
        return this.defaultSidebarItems.slice()
      }
      if (this.currentSidebarItems.length) {
        return this.currentSidebarItems.slice()
      }
      const modules = this.defaultSidebarItems.slice()
      const orderMap = new Map(modules.map(item => [item.key, item]))
      const ordered = this.sidebarCustomOrder.map(key => orderMap.get(key)).filter(Boolean)
      const remainder = modules.filter(item => !this.sidebarCustomOrder.includes(item.key))
      return ordered.concat(remainder)
    },
    getSidebarSortStorageKey() {
      if (this.$auth.hasRole('edu_teacher')) {
        return 'layout-setting-sidebar-sort-teacher'
      }
      if (this.$auth.hasRole('edu_parent')) {
        return 'layout-setting-sidebar-sort-parent'
      }
      if (this.$auth.hasRole('edu_student')) {
        return 'layout-setting-sidebar-sort-student'
      }
      return 'layout-setting-sidebar-sort-admin'
    },
    getSidebarSortSetting() {
      try {
        const setting = JSON.parse(localStorage.getItem(this.getSidebarSortStorageKey()) || '{}')
        return {
          sidebarSortMode: setting.sidebarSortMode || 'default',
          sidebarCustomOrder: Array.isArray(setting.sidebarCustomOrder) ? setting.sidebarCustomOrder : []
        }
      } catch (error) {
        return {
          sidebarSortMode: 'default',
          sidebarCustomOrder: []
        }
      }
    },
    saveSidebarSortSetting(mode, order) {
      localStorage.setItem(this.getSidebarSortStorageKey(), JSON.stringify({
        sidebarSortMode: mode,
        sidebarCustomOrder: Array.isArray(order) ? order : []
      }))
      this.$store.commit('settings/CHANGE_SETTING', {
        key: 'sidebarSortMode',
        value: mode
      })
      this.$store.commit('settings/CHANGE_SETTING', {
        key: 'sidebarCustomOrder',
        value: Array.isArray(order) ? order : []
      })
    },
    extractSidebarModules(routes = []) {
      const modules = []
      routes.forEach(route => {
        if (!route || route.hidden) {
          return
        }
        const title = this.resolveRouteTitle(route)
        if (title) {
          modules.push({
            key: this.buildRouteOrderKey(route),
            title
          })
          return
        }
        const children = Array.isArray(route.children) ? route.children.filter(item => !item.hidden) : []
        if (children.length) {
          modules.push(...this.extractSidebarModules(children))
        }
      })
      return modules
    },
    buildRouteOrderKey(route) {
      const info = this.getRouteDisplayInfo(route)
      return `${info.title}::${info.path}`
    },
    getRouteDisplayInfo(route) {
      if (!route) {
        return {
          title: '',
          path: ''
        }
      }
      const title = this.resolveRouteTitle(route)
      const path = (route.path || '').toString()
      if (title) {
        return {
          title,
          path
        }
      }
      const children = Array.isArray(route.children) ? route.children.filter(item => !item.hidden) : []
      if (children.length === 1) {
        return this.getRouteDisplayInfo(children[0])
      }
      return {
        title: '',
        path
      }
    },
    resolveRouteTitle(route) {
      const title = ((route.meta || {}).title || '').toString()
      if (title === '报名记录' && (this.$auth.hasRole('edu_student') || this.$auth.hasRole('edu_parent'))) {
        return '学习记录'
      }
      return title
    },
    saveSidebarSort() {
      const order = this.sidebarSortForm.mode === 'custom'
        ? this.sidebarSortItems.map(item => item.key)
        : []
      this.saveSidebarSortSetting(this.sidebarSortForm.mode, order)
      this.$store.commit('SET_SIDEBAR_ROUTERS', this.$store.state.permission.defaultRoutes)
      this.currentSidebarItems = this.extractSidebarModules(this.$store.getters.sidebarRouters || [])
      this.sidebarSortDialogVisible = false
      this.$message.success('侧栏排序已更新')
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
  display: flex;
  align-items: center;
  min-height: 48px;
  line-height: 48px;
  padding: 0 22px;
  color: #214a58;
  font-weight: 600;
}

::v-deep .settings-dropdown .el-dropdown-menu__item + .el-dropdown-menu__item {
  border-top: 1px solid rgba(214, 230, 228, 0.86);
}

::v-deep .settings-dropdown .el-dropdown-menu__item:focus,
::v-deep .settings-dropdown .el-dropdown-menu__item:not(.is-disabled):hover {
  background: rgba(226, 246, 237, 0.72);
  color: #0f8570;
}

::v-deep .sidebar-sort-dialog {
  border-radius: 22px;
  overflow: hidden;
}

.sort-mode-group {
  margin-bottom: 12px;
}

.sort-tip {
  margin: 0 0 16px;
  color: #6c8591;
  line-height: 1.7;
}

.sort-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 188px;
}

.sort-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid rgba(171, 227, 223, 0.45);
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.86), rgba(237, 249, 248, 0.76));
  box-shadow: 0 10px 22px rgba(33, 92, 102, 0.06);
}

.sort-item.disabled {
  opacity: 0.74;
}

.drag-handle {
  color: #58b7ae;
  font-size: 16px;
  cursor: move;
}

.sort-item.disabled .drag-handle {
  cursor: not-allowed;
}

.sort-title {
  color: #214a58;
  font-size: 14px;
  font-weight: 600;
}

.sort-item-ghost {
  opacity: 0.55;
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
