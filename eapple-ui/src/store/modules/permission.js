import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = applySidebarOrder(routes)
    }
  },
  actions: {
    GenerateRoutes({ commit }) {
      return new Promise(resolve => {
        getRouters().then(res => {
          const routeData = appendRoleRoutes(res.data || [])
          const sdata = JSON.parse(JSON.stringify(routeData))
          const rdata = JSON.parse(JSON.stringify(routeData))
          const sidebarRoutes = filterAsyncRouter(sdata)
          const rewriteRoutes = filterAsyncRouter(rdata, false, true)
          const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
          rewriteRoutes.push({ path: '*', redirect: '/404', hidden: true })
          router.addRoutes(asyncRoutes)
          commit('SET_ROUTES', rewriteRoutes)
          commit('SET_SIDEBAR_ROUTERS', constantRoutes.concat(sidebarRoutes))
          commit('SET_DEFAULT_ROUTES', sidebarRoutes)
          commit('SET_TOPBAR_ROUTES', sidebarRoutes)
          resolve(rewriteRoutes)
        })
      })
    }
  }
}

function appendRoleRoutes(routes) {
  const result = Array.isArray(routes) ? routes.slice() : []
  const hasOnlineCourse = result.some(route => hasRoutePath(route, 'edu/online-course'))
  if (!hasOnlineCourse && (auth.hasRole('edu_student') || auth.hasRole('admin') || auth.hasRole('edu_admin'))) {
    result.splice(2, 0, {
      name: '',
      path: '/',
      component: 'Layout',
      hidden: false,
      meta: null,
      children: [
        {
          path: 'edu/online-course',
          component: 'edu/onlineCourse/index',
          name: 'EduOnlineCourse',
          meta: {
            title: '网课中心',
            icon: 'online'
          }
        }
      ]
    })
  }
  return result
}

function hasRoutePath(route, targetPath) {
  if (!route) {
    return false
  }
  const routePath = normalizePath(route.path)
  if (routePath === normalizePath(targetPath)) {
    return true
  }
  return Array.isArray(route.children) && route.children.some(child => hasRoutePath(child, targetPath))
}

function normalizePath(path) {
  return (path || '').toString().replace(/^\/+/, '')
}

function getSidebarSortMode() {
  try {
    const roleSetting = JSON.parse(localStorage.getItem(getSidebarSortStorageKey()) || '{}')
    const layoutSetting = JSON.parse(localStorage.getItem('layout-setting') || '{}')
    return {
      mode: roleSetting.sidebarSortMode || layoutSetting.sidebarSortMode || 'default',
      order: Array.isArray(roleSetting.sidebarCustomOrder)
        ? roleSetting.sidebarCustomOrder
        : (Array.isArray(layoutSetting.sidebarCustomOrder) ? layoutSetting.sidebarCustomOrder : [])
    }
  } catch (error) {
    return {
      mode: 'default',
      order: []
    }
  }
}

function getSidebarSortStorageKey() {
  if (auth.hasRole('edu_teacher')) {
    return 'layout-setting-sidebar-sort-teacher'
  }
  if (auth.hasRole('edu_parent')) {
    return 'layout-setting-sidebar-sort-parent'
  }
  if (auth.hasRole('edu_student')) {
    return 'layout-setting-sidebar-sort-student'
  }
  return 'layout-setting-sidebar-sort-admin'
}

function applySidebarOrder(routes) {
  const { mode, order } = getSidebarSortMode()
  const list = Array.isArray(routes) ? routes.map(route => normalizeRouteOrder(route, mode, order)) : []
  if (mode !== 'custom') {
    return list
  }
  const hiddenRoutes = list.filter(route => route.hidden)
  const visibleRoutes = list.filter(route => !route.hidden)
  visibleRoutes.sort((a, b) => compareRouteByCustomOrder(a, b, order))
  return hiddenRoutes.concat(visibleRoutes)
}

function normalizeRouteOrder(route, mode, order) {
  const nextRoute = { ...route }
  if (Array.isArray(route.children) && route.children.length) {
    const orderedChildren = route.children.map(child => normalizeRouteOrder(child, mode, order))
    if (mode === 'custom') {
      const hiddenChildren = orderedChildren.filter(child => child.hidden)
      const visibleChildren = orderedChildren.filter(child => !child.hidden)
      visibleChildren.sort((a, b) => compareRouteByCustomOrder(a, b, order))
      nextRoute.children = hiddenChildren.concat(visibleChildren)
    } else {
      nextRoute.children = orderedChildren
    }
  }
  return nextRoute
}

function compareRouteByCustomOrder(a, b, order) {
  const keyA = getRouteOrderKey(a)
  const keyB = getRouteOrderKey(b)
  const indexA = order.indexOf(keyA)
  const indexB = order.indexOf(keyB)
  if (indexA === -1 && indexB === -1) {
    return 0
  }
  if (indexA === -1) {
    return 1
  }
  if (indexB === -1) {
    return -1
  }
  return indexA - indexB
}

function getRouteOrderKey(route) {
  const info = getRouteDisplayInfo(route)
  return `${info.title}::${info.path}`
}

function getRouteDisplayInfo(route) {
  if (!route) {
    return {
      title: '',
      path: ''
    }
  }
  const title = normalizeRouteTitle((((route || {}).meta || {}).title || '').toString())
  const path = ((route || {}).path || '').toString()
  if (title) {
    return { title, path }
  }
  const visibleChildren = Array.isArray(route.children) ? route.children.filter(item => !item.hidden) : []
  if (visibleChildren.length === 1) {
    return getRouteDisplayInfo(visibleChildren[0])
  }
  return {
    title: '',
    path
  }
}

function normalizeRouteTitle(title) {
  if (title === '报名记录' && (auth.hasRole('edu_student') || auth.hasRole('edu_parent'))) {
    return '学习记录'
  }
  return title
}

function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route.children
      delete route.redirect
    }
    return true
  })
}

function filterChildren(childrenMap, lastRouter = false) {
  const children = []
  childrenMap.forEach(el => {
    el.path = lastRouter ? lastRouter.path + '/' + el.path : el.path
    if (el.children && el.children.length && el.component === 'ParentView') {
      children.push(...filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

export function filterDynamicRoutes(routes) {
  const res = []
  routes.forEach(route => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route)
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route)
      }
    }
  })
  return res
}

export const loadView = (view) => {
  if (process.env.NODE_ENV === 'development') {
    return (resolve) => require([`@/views/${view}`], resolve)
  }
  return () => import(`@/views/${view}`)
}

export default permission
