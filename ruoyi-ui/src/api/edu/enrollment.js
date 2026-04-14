import request from '@/utils/request'

export function listEnrollment(query) {
  return request({
    url: '/edu/enrollment/list',
    method: 'get',
    params: query
  })
}

export function updateEnrollment(data) {
  return request({
    url: '/edu/enrollment',
    method: 'put',
    data
  })
}
