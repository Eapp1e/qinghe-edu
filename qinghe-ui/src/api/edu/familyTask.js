import request from '@/utils/request'

export function listFamilyTask(query) {
  return request({
    url: '/edu/familyTask/list',
    method: 'get',
    params: query
  })
}

export function getFamilyTask(taskId) {
  return request({
    url: '/edu/familyTask/' + taskId,
    method: 'get'
  })
}

export function getFamilyTaskSummary() {
  return request({
    url: '/edu/familyTask/summary',
    method: 'get'
  })
}

export function addFamilyTask(data) {
  return request({
    url: '/edu/familyTask',
    method: 'post',
    data
  })
}

export function updateFamilyTask(data) {
  return request({
    url: '/edu/familyTask',
    method: 'put',
    data
  })
}

export function submitFamilyTask(data) {
  return request({
    url: '/edu/familyTask/submit',
    method: 'put',
    data
  })
}

export function reviewFamilyTask(data) {
  return request({
    url: '/edu/familyTask/review',
    method: 'put',
    data
  })
}

export function delFamilyTask(taskId) {
  return request({
    url: '/edu/familyTask/' + taskId,
    method: 'delete'
  })
}
