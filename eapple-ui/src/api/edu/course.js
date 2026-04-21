import request from '@/utils/request'
import { download } from '@/utils/request'

export function listCourse(query) {
  return request({
    url: '/edu/course/list',
    method: 'get',
    params: query
  })
}

export function listMyCourse() {
  return request({
    url: '/edu/course/myList',
    method: 'get'
  })
}

export function recommendCourse(studentUserId) {
  return request({
    url: '/edu/course/recommend',
    method: 'get',
    params: { studentUserId },
    timeout: 120000,
    noErrorMessage: true
  })
}

export function getCourse(courseId) {
  return request({
    url: '/edu/course/' + courseId,
    method: 'get'
  })
}

export function addCourse(data) {
  return request({
    url: '/edu/course',
    method: 'post',
    data
  })
}

export function updateCourse(data) {
  return request({
    url: '/edu/course',
    method: 'put',
    data
  })
}

export function delCourse(courseId) {
  return request({
    url: '/edu/course/' + courseId,
    method: 'delete'
  })
}

export function enrollCourse(courseId, studentUserId) {
  return request({
    url: '/edu/course/enroll/' + courseId,
    method: 'post',
    params: { studentUserId }
  })
}

export function cancelEnrollCourse(courseId, studentUserId) {
  return request({
    url: '/edu/course/cancelEnroll/' + courseId,
    method: 'post',
    params: { studentUserId }
  })
}

export function generateCourseNotice(courseId) {
  return request({
    url: '/edu/course/notice/' + courseId,
    method: 'post',
    timeout: 120000,
    noErrorMessage: true
  })
}

export function generateTeachingSuggestion(courseId) {
  return request({
    url: '/edu/course/suggestion/' + courseId,
    method: 'post',
    timeout: 120000,
    noErrorMessage: true
  })
}

export function exportCourse(query) {
  return download('/edu/course/export', query, `course_center_${new Date().getTime()}.xlsx`)
}
