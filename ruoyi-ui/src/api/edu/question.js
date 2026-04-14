import request from '@/utils/request'

export function listQuestion(query) {
  return request({
    url: '/edu/question/list',
    method: 'get',
    params: query
  })
}

export function getQuestion(questionId) {
  return request({
    url: '/edu/question/' + questionId,
    method: 'get'
  })
}

export function addQuestion(data) {
  return request({
    url: '/edu/question',
    method: 'post',
    data
  })
}

export function delQuestion(questionId) {
  return request({
    url: '/edu/question/' + questionId,
    method: 'delete'
  })
}

export function regenerateQuestionAnswer(questionId) {
  return request({
    url: '/edu/question/regenerate/' + questionId,
    method: 'post'
  })
}
