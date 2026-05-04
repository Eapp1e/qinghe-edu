import axios from 'axios'
import { Notification, MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { tansParams, blobValidate } from '@/utils/qinghe'
import cache from '@/plugins/cache'
import { saveAs } from 'file-saver'

// 是否显示重新登录
export let isRelogin = { show: false }

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 10000
})

function normalizeRequestMessage(message) {
  if (!message) {
    return '系统处理异常，请稍后重试'
  }
  if (message === 'Network Error') {
    return '服务连接异常，请确认后端服务已经启动'
  }
  if (message.includes('timeout')) {
    return '请求处理超时，请稍后重试'
  }
  if (message.includes('status code 403')) {
    return '当前请求暂不可用，请检查权限或 AI 模型配置'
  }
  if (message.includes('status code 500')) {
    return '服务处理异常，请稍后重试'
  }
  if (message.includes('Request failed with status code')) {
    return '接口请求异常，请稍后重试'
  }
  return message
}

service.interceptors.request.use(
  config => {
    const isToken = (config.headers || {}).isToken === false
    const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
    const interval = (config.headers || {}).interval || 1000

    if (getToken() && !isToken) {
      config.headers.Authorization = 'Bearer ' + getToken()
    }

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
      const requestSize = Object.keys(JSON.stringify(requestObj)).length
      const limitSize = 5 * 1024 * 1024
      if (requestSize >= limitSize) {
        console.warn(`[${config.url}]: 请求数据大小超出允许的 5M 限制，跳过重复提交校验`)
        return config
      }
      const sessionObj = cache.session.getJSON('sessionObj')
      if (!sessionObj) {
        cache.session.setJSON('sessionObj', requestObj)
      } else {
        const sUrl = sessionObj.url
        const sData = sessionObj.data
        const sTime = sessionObj.time
        if (sData === requestObj.data && requestObj.time - sTime < interval && sUrl === requestObj.url) {
          const message = '数据正在处理中，请勿重复提交'
          console.warn(`[${sUrl}]: ${message}`)
          return Promise.reject(new Error(message))
        }
        cache.session.setJSON('sessionObj', requestObj)
      }
    }

    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  res => {
    const code = res.data.code || 200
    const msg = errorCode[code] || res.data.msg || errorCode.default

    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data
    }

    const silentError = res.config && res.config.noErrorMessage
    if (code === 401) {
      if (!isRelogin.show) {
        isRelogin.show = true
        MessageBox.confirm('登录状态已过期，您可以继续停留在当前页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            isRelogin.show = false
            store.dispatch('LogOut').then(() => {
              location.href = '/index'
            })
          })
          .catch(() => {
            isRelogin.show = false
          })
      }
      return Promise.reject(new Error('无效的会话，或会话已过期，请重新登录'))
    }

    if (code === 500) {
      if (!silentError) {
        Message({ message: msg, type: 'error' })
      }
      return Promise.reject(new Error(msg))
    }

    if (code === 601) {
      if (!silentError) {
        Message({ message: msg, type: 'warning' })
      }
      return Promise.reject(new Error(msg))
    }

    if (code !== 200) {
      if (!silentError) {
        Notification.error({ title: msg })
      }
      return Promise.reject(new Error(msg))
    }

    return res.data
  },
  error => {
    console.log('err' + error)
    const silentError = error.config && error.config.noErrorMessage
    const message = normalizeRequestMessage(error && error.message)
    if (!silentError) {
      Message({ message, type: 'error', duration: 5 * 1000 })
    }
    return Promise.reject(error)
  }
)

export function download(url, params, filename, config) {
  return service
    .post(
      url,
      params,
      {
        transformRequest: [requestParams => tansParams(requestParams)],
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        responseType: 'blob',
        ...config
      }
    )
    .then(async data => {
      const isBlob = blobValidate(data)
      if (isBlob) {
        const blob = new Blob([data])
        saveAs(blob, filename)
      } else {
        const resText = await data.text()
        const rspObj = JSON.parse(resText)
        const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode.default
        Message.error(errMsg)
      }
    })
    .catch(error => {
      console.error(error)
      Message.error('导出失败，请稍后重试')
    })
}

export default service
