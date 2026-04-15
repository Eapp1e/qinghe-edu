import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/eapple.scss' // platform css
import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive
import plugins from './plugins' // plugins
import { download } from '@/utils/request'

import './assets/icons' // icon
import './permission' // permission control
import { getDicts } from "@/api/system/dict/data"
import { getConfigKey } from "@/api/system/config"
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, handleTree } from "@/utils/eapple"
// 鍒嗛〉缁勪欢
import Pagination from "@/components/Pagination"
// 鑷畾涔夎〃鏍煎伐鍏风粍浠?
import RightToolbar from "@/components/RightToolbar"
// 瀵屾枃鏈粍浠?
import Editor from "@/components/Editor"
// 鏂囦欢涓婁紶缁勪欢
import FileUpload from "@/components/FileUpload"
// 鍥剧墖涓婁紶缁勪欢
import ImageUpload from "@/components/ImageUpload"
// 鍥剧墖棰勮缁勪欢
import ImagePreview from "@/components/ImagePreview"
// 瀛楀吀鏍囩缁勪欢
import DictTag from '@/components/DictTag'
// 瀛楀吀鏁版嵁缁勪欢
import DictData from '@/components/DictData'

// 鍏ㄥ眬鏂规硶鎸傝浇
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree

// 鍏ㄥ眬缁勪欢鎸傝浇
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
Vue.component('Editor', Editor)
Vue.component('FileUpload', FileUpload)
Vue.component('ImageUpload', ImageUpload)
Vue.component('ImagePreview', ImagePreview)

Vue.use(directive)
Vue.use(plugins)
DictData.install()

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
