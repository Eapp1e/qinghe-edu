import defaultSettings from '@/settings'
import { useDynamicTitle } from '@/utils/dynamicTitle'

const { sideTheme, backgroundMode, showSettings, navType, tagsView, tagsViewPersist, tagsIcon, tagsViewStyle, fixedHeader, sidebarLogo, dynamicTitle, footerVisible, footerContent } = defaultSettings

const storageSetting = JSON.parse(localStorage.getItem('layout-setting')) || ''
const state = {
  title: '',
  theme: storageSetting.theme || '#409EFF',
  sideTheme: storageSetting.sideTheme || sideTheme,
  backgroundMode: storageSetting.backgroundMode || backgroundMode,
  showSettings: showSettings,
  navType: storageSetting.navType === undefined ? navType : storageSetting.navType,
  tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
  tagsViewPersist: storageSetting.tagsViewPersist === undefined ? tagsViewPersist : storageSetting.tagsViewPersist,
  tagsIcon: storageSetting.tagsIcon === undefined ? tagsIcon : storageSetting.tagsIcon,
  tagsViewStyle: storageSetting.tagsViewStyle === undefined ? tagsViewStyle : storageSetting.tagsViewStyle,
  fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
  sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
  dynamicTitle: storageSetting.dynamicTitle === undefined ? dynamicTitle : storageSetting.dynamicTitle,
  footerVisible: storageSetting.footerVisible === undefined ? footerVisible : storageSetting.footerVisible,
  footerContent: footerContent
}
const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
    }
  },
  SET_TITLE: (state, title) => {
    state.title = title
  }
}

const actions = {
  // 修改布局设置
  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data)
    const currentSetting = JSON.parse(localStorage.getItem('layout-setting') || '{}')
    currentSetting[data.key] = data.value
    localStorage.setItem('layout-setting', JSON.stringify(currentSetting))
  },
  // 设置网页标题
  setTitle({ commit }, title) {
    commit('SET_TITLE', title)
    useDynamicTitle()
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

