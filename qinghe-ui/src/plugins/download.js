import axios from 'axios'
import { Message } from 'element-ui'
import { saveAs } from 'file-saver'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { blobValidate } from '@/utils/qinghe'

const baseURL = process.env.VUE_APP_BASE_API

export default {
  name(name, isDelete = true) {
    const url = baseURL + '/common/download?fileName=' + encodeURIComponent(name) + '&delete=' + isDelete
    axios({
      method: 'get',
      url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() }
    }).then(res => {
      const isBlob = blobValidate(res.data)
      if (isBlob) {
        const blob = new Blob([res.data])
        this.saveAs(blob, decodeURIComponent(res.headers['download-filename']))
      } else {
        this.printErrMsg(res.data)
      }
    }).catch(() => {
      Message.error('下载失败，请稍后重试')
    })
  },
  resource(resource) {
    const url = baseURL + '/common/download/resource?resource=' + encodeURIComponent(resource)
    axios({
      method: 'get',
      url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() }
    }).then(res => {
      const isBlob = blobValidate(res.data)
      if (isBlob) {
        const blob = new Blob([res.data])
        this.saveAs(blob, decodeURIComponent(res.headers['download-filename']))
      } else {
        this.printErrMsg(res.data)
      }
    }).catch(() => {
      Message.error('下载失败，请稍后重试')
    })
  },
  zip(url, name) {
    axios({
      method: 'get',
      url: baseURL + url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() }
    }).then(res => {
      const isBlob = blobValidate(res.data)
      if (isBlob) {
        const blob = new Blob([res.data], { type: 'application/zip' })
        this.saveAs(blob, name)
      } else {
        this.printErrMsg(res.data)
      }
    }).catch(() => {
      Message.error('下载失败，请稍后重试')
    })
  },
  saveAs(text, name, opts) {
    saveAs(text, name, opts)
  },
  async printErrMsg(data) {
    const resText = await data.text()
    const rspObj = JSON.parse(resText)
    const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode.default
    Message.error(errMsg)
  }
}
