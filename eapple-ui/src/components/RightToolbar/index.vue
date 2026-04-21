<template>
  <div class="top-right-btn" :style="style">
    <el-row>
      <el-tooltip class="item" effect="dark" content="刷新" placement="top">
        <el-button size="mini" circle icon="el-icon-refresh" @click="refresh" />
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="显隐列" placement="top" v-if="Object.keys(columns).length > 0">
        <el-button size="mini" circle icon="el-icon-menu" @click="showColumn" v-if="showColumnsType === 'transfer'" />
        <el-dropdown trigger="click" :hide-on-click="false" style="padding-left: 12px" v-if="showColumnsType === 'checkbox'">
          <el-button size="mini" circle icon="el-icon-menu" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <el-checkbox :indeterminate="isIndeterminate" v-model="isChecked" @change="toggleCheckAll">列展示</el-checkbox>
            </el-dropdown-item>
            <div class="check-line"></div>
            <template v-for="(item, key) in columns">
              <el-dropdown-item :key="key">
                <el-checkbox v-model="item.visible" @change="checkboxChange($event, key)" :label="item.label" />
              </el-dropdown-item>
            </template>
          </el-dropdown-menu>
        </el-dropdown>
      </el-tooltip>
    </el-row>
    <el-dialog :title="title" :visible.sync="open" append-to-body>
      <el-transfer
        :titles="['显示', '隐藏']"
        v-model="value"
        :data="transferData"
        @change="dataChange"
      />
    </el-dialog>
  </div>
</template>

<script>
import cache from '@/plugins/cache'

export default {
  name: 'RightToolbar',
  data() {
    return {
      value: [],
      title: '显示/隐藏',
      open: false
    }
  },
  props: {
    columns: {
      type: [Array, Object],
      default: () => ({})
    },
    showColumnsType: {
      type: String,
      default: 'checkbox'
    },
    gutter: {
      type: Number,
      default: 10
    },
    storageKey: {
      type: String,
      default: ''
    }
  },
  computed: {
    style() {
      const ret = {}
      if (this.gutter) {
        ret.marginRight = `${this.gutter / 2}px`
      }
      return ret
    },
    isChecked: {
      get() {
        return Array.isArray(this.columns)
          ? this.columns.every(col => col.visible)
          : Object.values(this.columns).every(col => col.visible)
      },
      set() {}
    },
    isIndeterminate() {
      return Array.isArray(this.columns)
        ? this.columns.some(col => col.visible) && !this.isChecked
        : Object.values(this.columns).some(col => col.visible) && !this.isChecked
    },
    transferData() {
      if (Array.isArray(this.columns)) {
        return this.columns.map((item, index) => ({ key: index, label: item.label }))
      }
      return Object.keys(this.columns).map((key, index) => ({ key: index, label: this.columns[key].label }))
    }
  },
  created() {
    if (this.storageKey) {
      try {
        const saved = cache.local.getJSON(this.storageKey)
        if (saved && typeof saved === 'object') {
          if (Array.isArray(this.columns)) {
            this.columns.forEach((col, index) => {
              if (saved[index] !== undefined) col.visible = saved[index]
            })
          } else {
            Object.keys(this.columns).forEach(key => {
              if (saved[key] !== undefined) this.columns[key].visible = saved[key]
            })
          }
        }
      } catch (e) {}
    }
    if (this.showColumnsType === 'transfer') {
      if (Array.isArray(this.columns)) {
        for (const item in this.columns) {
          if (this.columns[item].visible === false) {
            this.value.push(parseInt(item))
          }
        }
      } else {
        Object.keys(this.columns).forEach((key, index) => {
          if (this.columns[key].visible === false) {
            this.value.push(index)
          }
        })
      }
    }
  },
  methods: {
    refresh() {
      this.$emit('queryTable')
    },
    dataChange(data) {
      if (Array.isArray(this.columns)) {
        for (const item in this.columns) {
          const key = this.columns[item].key
          this.columns[item].visible = !data.includes(key)
        }
      } else {
        Object.keys(this.columns).forEach((key, index) => {
          this.columns[key].visible = !data.includes(index)
        })
      }
      this.saveStorage()
    },
    showColumn() {
      this.open = true
    },
    checkboxChange(event, key) {
      if (Array.isArray(this.columns)) {
        this.columns.filter(item => item.key === key)[0].visible = event
      } else {
        this.columns[key].visible = event
      }
      this.saveStorage()
    },
    toggleCheckAll() {
      const newValue = !this.isChecked
      if (Array.isArray(this.columns)) {
        this.columns.forEach(col => {
          col.visible = newValue
        })
      } else {
        Object.values(this.columns).forEach(col => {
          col.visible = newValue
        })
      }
      this.saveStorage()
    },
    saveStorage() {
      if (!this.storageKey) return
      try {
        const state = {}
        if (Array.isArray(this.columns)) {
          this.columns.forEach((col, index) => {
            state[index] = col.visible
          })
        } else {
          Object.keys(this.columns).forEach(key => {
            state[key] = this.columns[key].visible
          })
        }
        cache.local.setJSON(this.storageKey, state)
      } catch (e) {}
    }
  }
}
</script>

<style lang="scss" scoped>
::v-deep .el-transfer__button {
  border-radius: 50%;
  padding: 12px;
  display: block;
  margin-left: 0;
}

::v-deep .el-transfer__button:first-child {
  margin-bottom: 10px;
}

.check-line {
  width: 90%;
  height: 1px;
  background-color: #ccc;
  margin: 3px auto;
}
</style>
