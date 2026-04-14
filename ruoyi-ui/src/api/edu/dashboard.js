import request from '@/utils/request'

export function getEduDashboard() {
  return request({
    url: '/edu/dashboard',
    method: 'get'
  })
}
