import request from '@/utils/request'

export function listAiLog(query) {
  return request({
    url: '/edu/aiLog/list',
    method: 'get',
    params: query
  })
}

export function listMyAiLog(query) {
  return request({
    url: '/edu/aiLog/myList',
    method: 'get',
    params: query
  })
}

export function getAiLogSummary(query) {
  return request({
    url: '/edu/aiLog/summary',
    method: 'get',
    params: query
  })
}

export function getMyAiLogSummary(query) {
  return request({
    url: '/edu/aiLog/mySummary',
    method: 'get',
    params: query
  })
}

export function delAiLog(logId) {
  return request({
    url: '/edu/aiLog/' + logId,
    method: 'delete'
  })
}

export function delMyAiLog(logId) {
  return request({
    url: '/edu/aiLog/my/' + logId,
    method: 'delete'
  })
}
