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
          <el-dropdown-item command="tableHeaderColor">表头颜色</el-dropdown-item>
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
        {{ sidebarSortForm.mode === 'custom' ? '拖动下面的模块卡片，调整侧栏显示顺序。' : '使用当前角色的默认侧栏顺序。' }}
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

    <el-dialog
      title="表头颜色"
      :visible.sync="tableHeaderDialogVisible"
      width="500px"
      append-to-body
      custom-class="table-header-color-dialog"
    >
      <div class="color-dialog-body">
        <p class="color-tip">直接点击下面的绿色系色块即可切换，预览效果会同步更新。</p>

        <div class="header-preview">
          <span>表头示例</span>
          <div class="header-preview-bar" :style="{ backgroundColor: tableHeaderDraftColor || tableHeaderColor }">
            表头示例
          </div>
        </div>

        <div class="rgb-spectrum">
          <span class="rgb-spectrum-label">RGB 色板</span>
          <input
            class="rgb-color-input"
            type="color"
            :value="tableHeaderDraftColor || tableHeaderColor"
            @input="handleColorPanelInput($event.target.value)"
          />
        </div>

        <div class="color-swatches">
          <button
            v-for="color in tableHeaderPresetColors"
            :key="color"
            type="button"
            class="color-swatch"
            :class="{ active: (tableHeaderDraftColor || tableHeaderColor) === color }"
            :style="{ backgroundColor: color }"
            :title="color"
            @click="selectTableHeaderColor(color)"
          />
        </div>

        <div class="selected-rgb">{{ selectedRgbText }}</div>
      </div>
      <div slot="footer">
        <el-button @click="tableHeaderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTableHeaderColor">应用颜色</el-button>
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
      tableHeaderDialogVisible: false,
      tableHeaderDraftColor: '',
      tableHeaderDraftRgb: { r: 215, g: 221, b: 215 },
      tableHeaderPresetColors: ['#dfe4df', '#d7ddd7', '#d1d8d0', '#cad3ca', '#c3cec2', '#bcc8bb', '#b5c1b2', '#aebaa9'],
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
    tableHeaderColor() {
      return this.$store.state.settings.tableHeaderColor || '#d7ddd7'
    },
    sidebarSortMode() {
      return this.getSidebarSortSetting().sidebarSortMode || 'default'
    },
    sidebarCustomOrder() {
      return this.getSidebarSortSetting().sidebarCustomOrder || []
    },
    selectedRgbText() {
      const { r, g, b } = this.tableHeaderDraftRgb
      return `RGB(${r}, ${g}, ${b})`
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
      if (command === 'tableHeaderColor') {
        this.selectTableHeaderColor(this.tableHeaderColor)
        this.tableHeaderDialogVisible = true
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
      const normalizedOrder = this.sidebarCustomOrder.map(key => this.resolveSidebarOrderKey(key, orderMap))
      const ordered = normalizedOrder.map(key => orderMap.get(key)).filter(Boolean)
      const remainder = modules.filter(item => !normalizedOrder.includes(item.key))
      return ordered.concat(remainder)
    },
    resolveSidebarOrderKey(key, orderMap) {
      if (orderMap.has(key)) {
        return key
      }
      const aliases = []
      if (key.indexOf('上课记录::') === 0) {
        aliases.push(key.replace('上课记录::', '学习记录::'), key.replace('上课记录::', '报名记录::'))
      }
      if (key.indexOf('学习记录::') === 0) {
        aliases.push(key.replace('学习记录::', '上课记录::'), key.replace('学习记录::', '报名记录::'))
      }
      const hit = aliases.find(item => orderMap.has(item))
      return hit || key
    },
    getSidebarSortStorageKey() {
      if (this.$auth.hasExactRole('admin') || this.$auth.hasExactRole('edu_admin')) return 'layout-setting-sidebar-sort-admin'
      if (this.$auth.hasExactRole('edu_teacher')) return 'layout-setting-sidebar-sort-teacher'
      if (this.$auth.hasExactRole('edu_parent')) return 'layout-setting-sidebar-sort-parent'
      if (this.$auth.hasExactRole('edu_student')) return 'layout-setting-sidebar-sort-student'
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
        if (!route || route.hidden) return
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
        return { title: '', path: '' }
      }
      const title = this.resolveRouteTitle(route)
      const path = (route.path || '').toString()
      if (title) {
        return { title, path }
      }
      const children = Array.isArray(route.children) ? route.children.filter(item => !item.hidden) : []
      if (children.length === 1) {
        return this.getRouteDisplayInfo(children[0])
      }
      return { title: '', path: '' }
    },
    resolveRouteTitle(route) {
      const title = ((route.meta || {}).title || '').toString()
      const normalizedTitle = title.trim()
      const enrollmentTitles = ['报名记录', '上课记录', '学习记录', '鎶ュ悕璁板綍', '涓婅璁板綍']
      if (enrollmentTitles.includes(normalizedTitle)) {
        return (this.$auth.hasExactRole('edu_student') || this.$auth.hasExactRole('edu_parent')) ? '学习记录' : '上课记录'
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
    selectTableHeaderColor(color) {
      this.tableHeaderDraftColor = color || '#d7ddd7'
      this.tableHeaderDraftRgb = this.hexToRgb(this.tableHeaderDraftColor)
    },
    handleColorPanelInput(color) {
      this.selectTableHeaderColor(color)
    },
    hexToRgb(hex) {
      const value = (hex || '').replace('#', '').trim()
      if (!/^[0-9a-fA-F]{6}$/.test(value)) {
        return { r: 215, g: 221, b: 215 }
      }
      return {
        r: parseInt(value.slice(0, 2), 16),
        g: parseInt(value.slice(2, 4), 16),
        b: parseInt(value.slice(4, 6), 16)
      }
    },
    rgbToHex({ r, g, b }) {
      const format = value => Math.max(0, Math.min(255, value)).toString(16).padStart(2, '0')
      return `#${format(r)}${format(g)}${format(b)}`
    },
    saveTableHeaderColor() {
      this.$store.dispatch('settings/changeSetting', {
        key: 'tableHeaderColor',
        value: this.tableHeaderDraftColor || '#d7ddd7'
      })
      this.tableHeaderDialogVisible = false
      this.$message.success('表头颜色已更新')
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

.sort-mode-group {
  margin-bottom: 12px;
}

.sort-tip {
  margin: 0 0 18px;
  color: #6c776f;
  line-height: 1.8;
}

.sort-list {
  display: grid;
  gap: 12px;
}

.sort-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid rgba(160, 205, 184, 0.26);
  border-radius: 14px;
  background: #fffdfa;
  color: #31473a;
}

.sort-item.disabled {
  opacity: 0.72;
}

.drag-handle {
  color: #6f8778;
  cursor: move;
}

.sort-title {
  font-weight: 600;
}

.color-dialog-body {
  display: grid;
  gap: 18px;
}

.color-tip {
  margin: 0;
  color: #6b756d;
  line-height: 1.8;
}

.header-preview {
  display: grid;
  gap: 10px;
}

.header-preview span {
  color: #435048;
  font-weight: 600;
}

.header-preview-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  border: 1px solid rgba(120, 136, 122, 0.22);
  border-radius: 14px;
  color: #3f4a42;
  font-weight: 700;
}

.rgb-spectrum {
  display: grid;
  gap: 10px;
}

.rgb-spectrum-label {
  color: #435048;
  font-size: 12px;
  font-weight: 700;
}

.rgb-color-input {
  width: 100%;
  height: 44px;
  padding: 0;
  border: 1px solid rgba(120, 136, 122, 0.22);
  border-radius: 12px;
  background: #ffffff;
  cursor: pointer;
}

.rgb-color-input::-webkit-color-swatch-wrapper {
  padding: 6px;
}

.rgb-color-input::-webkit-color-swatch {
  border: none;
  border-radius: 8px;
}

.color-swatches {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.color-swatch {
  height: 42px;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.color-swatch:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(75, 94, 72, 0.12);
}

.color-swatch.active {
  border-color: #5f8f4e;
  box-shadow: 0 0 0 3px rgba(95, 143, 78, 0.14);
}

.selected-rgb {
  justify-self: start;
  color: #5b6b5f;
  font-size: 12px;
  font-weight: 700;
}
</style>
