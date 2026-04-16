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
