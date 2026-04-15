import request from '@/utils/request'

export function listAiLog(query) {
  return request({
    url: '/edu/aiLog/list',
    method: 'get',
    params: query
  })
}

export function listMyAiLog() {
  return request({
    url: '/edu/aiLog/myList',
    method: 'get'
  })
}
