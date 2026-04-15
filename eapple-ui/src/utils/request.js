import axios from 'axios'
import { Notification, MessageBox, Message, Loading } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { tansParams, blobValidate } from "@/utils/eapple"
import cache from '@/plugins/cache'
import { saveAs } from 'file-saver'

let downloadLoadingInstance
// 鏄惁鏄剧ず閲嶆柊鐧诲綍
export let isRelogin = { show: false }

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 鍒涘缓axios瀹炰緥
const service = axios.create({
  // axios涓姹傞厤缃湁baseURL閫夐」锛岃〃绀鸿姹俇RL鍏叡閮ㄥ垎
  baseURL: process.env.VUE_APP_BASE_API,
  // 瓒呮椂
  timeout: 10000
})

// request鎷︽埅鍣?
service.interceptors.request.use(config => {
  // 鏄惁闇€瑕佽缃?token
  const isToken = (config.headers || {}).isToken === false
  // 鏄惁闇€瑕侀槻姝㈡暟鎹噸澶嶆彁浜?
  const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
  // 闂撮殧鏃堕棿(ms)锛屽皬浜庢鏃堕棿瑙嗕负閲嶅鎻愪氦
  const interval = (config.headers || {}).interval || 1000
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken() // 璁╂瘡涓姹傛惡甯﹁嚜瀹氫箟token 璇锋牴鎹疄闄呮儏鍐佃嚜琛屼慨鏀?
  }
  // get璇锋眰鏄犲皠params鍙傛暟
  if (config.method === 'get' && config.params) {
    let url = config.url + '?' + tansParams(config.params)
    url = url.slice(0, -1)
    config.params = {}
    config.url = url
  }
  if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    const requestObj = {
      url: config.url,
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      time: new Date().getTime()
    }
    const requestSize = Object.keys(JSON.stringify(requestObj)).length // 璇锋眰鏁版嵁澶у皬
    const limitSize = 5 * 1024 * 1024 // 闄愬埗瀛樻斁鏁版嵁5M
    if (requestSize >= limitSize) {
      console.warn(`[${config.url}]: ` + '璇锋眰鏁版嵁澶у皬瓒呭嚭鍏佽鐨?M闄愬埗锛屾棤娉曡繘琛岄槻閲嶅鎻愪氦楠岃瘉銆?)
      return config
    }
    const sessionObj = cache.session.getJSON('sessionObj')
    if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
      cache.session.setJSON('sessionObj', requestObj)
    } else {
      const s_url = sessionObj.url                  // 璇锋眰鍦板潃
      const s_data = sessionObj.data                // 璇锋眰鏁版嵁
      const s_time = sessionObj.time                // 璇锋眰鏃堕棿
      if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
        const message = '鏁版嵁姝ｅ湪澶勭悊锛岃鍕块噸澶嶆彁浜?
        console.warn(`[${s_url}]: ` + message)
        return Promise.reject(new Error(message))
      } else {
        cache.session.setJSON('sessionObj', requestObj)
      }
    }
  }
  return config
}, error => {
    console.log(error)
    Promise.reject(error)
})

// 鍝嶅簲鎷︽埅鍣?
service.interceptors.response.use(res => {
    // 鏈缃姸鎬佺爜鍒欓粯璁ゆ垚鍔熺姸鎬?
    const code = res.data.code || 200
    // 鑾峰彇閿欒淇℃伅
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    // 浜岃繘鍒舵暟鎹垯鐩存帴杩斿洖
    if (res.request.responseType ===  'blob' || res.request.responseType ===  'arraybuffer') {
      return res.data
    }
    if (code === 401) {
      if (!isRelogin.show) {
        isRelogin.show = true
        MessageBox.confirm('鐧诲綍鐘舵€佸凡杩囨湡锛屾偍鍙互缁х画鐣欏湪璇ラ〉闈紝鎴栬€呴噸鏂扮櫥褰?, '绯荤粺鎻愮ず', { confirmButtonText: '閲嶆柊鐧诲綍', cancelButtonText: '鍙栨秷', type: 'warning' }).then(() => {
          isRelogin.show = false
          store.dispatch('LogOut').then(() => {
            location.href = '/index'
          })
      }).catch(() => {
        isRelogin.show = false
      })
    }
      return Promise.reject('鏃犳晥鐨勪細璇濓紝鎴栬€呬細璇濆凡杩囨湡锛岃閲嶆柊鐧诲綍銆?)
    } else if (code === 500) {
      Message({ message: msg, type: 'error' })
      return Promise.reject(new Error(msg))
    } else if (code === 601) {
      Message({ message: msg, type: 'warning' })
      return Promise.reject('error')
    } else if (code !== 200) {
      Notification.error({ title: msg })
      return Promise.reject('error')
    } else {
      return res.data
    }
  },
  error => {
    console.log('err' + error)
    let { message } = error
    if (message == "Network Error") {
      message = "鍚庣鎺ュ彛杩炴帴寮傚父"
    } else if (message.includes("timeout")) {
      message = "绯荤粺鎺ュ彛璇锋眰瓒呮椂"
    } else if (message.includes("Request failed with status code")) {
      message = "绯荤粺鎺ュ彛" + message.slice(-3) + "寮傚父"
    }
    Message({ message: message, type: 'error', duration: 5 * 1000 })
    return Promise.reject(error)
  }
)

// 閫氱敤涓嬭浇鏂规硶
export function download(url, params, filename, config) {
  downloadLoadingInstance = Loading.service({ text: "姝ｅ湪涓嬭浇鏁版嵁锛岃绋嶅€?, spinner: "el-icon-loading", background: "rgba(0, 0, 0, 0.7)", })
  return service.post(url, params, {
    transformRequest: [(params) => { return tansParams(params) }],
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    responseType: 'blob',
    ...config
  }).then(async (data) => {
    const isBlob = blobValidate(data)
    if (isBlob) {
      const blob = new Blob([data])
      saveAs(blob, filename)
    } else {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
      Message.error(errMsg)
    }
    downloadLoadingInstance.close()
  }).catch((r) => {
    console.error(r)
    Message.error('涓嬭浇鏂囦欢鍑虹幇閿欒锛岃鑱旂郴绠＄悊鍛橈紒')
    downloadLoadingInstance.close()
  })
}

export default service
