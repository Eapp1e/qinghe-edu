import request from '@/utils/request'

export function listAiModels() {
  return request({
    url: '/edu/ai/models',
    method: 'get'
  })
}

export function getCurrentAiModel() {
  return request({
    url: '/edu/ai/currentModel',
    method: 'get'
  })
}

export function updateCurrentAiModel(modelName) {
  return request({
    url: '/edu/ai/currentModel',
    method: 'put',
    data: { modelName }
  })
}

export function recommendOnlineResources(data) {
  return request({
    url: '/edu/ai/online-resource-recommend',
    method: 'post',
    timeout: 120000,
    noErrorMessage: true,
    data
  })
}

export function generateParentDiagnosis(data) {
  return request({
    url: '/edu/ai/parent-diagnosis',
    method: 'post',
    timeout: 120000,
    noErrorMessage: true,
    data
  })
}
