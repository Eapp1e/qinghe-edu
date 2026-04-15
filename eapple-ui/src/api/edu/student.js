import request from '@/utils/request'

export function listStudent(query) {
  return request({
    url: '/edu/student/list',
    method: 'get',
    params: query
  })
}

export function listMyChildren() {
  return request({
    url: '/edu/student/myChildren',
    method: 'get'
  })
}

export function getStudent(profileId) {
  return request({
    url: '/edu/student/' + profileId,
    method: 'get'
  })
}

export function addStudent(data) {
  return request({
    url: '/edu/student',
    method: 'post',
    data
  })
}

export function updateStudent(data) {
  return request({
    url: '/edu/student',
    method: 'put',
    data
  })
}

export function delStudent(profileId) {
  return request({
    url: '/edu/student/' + profileId,
    method: 'delete'
  })
}
