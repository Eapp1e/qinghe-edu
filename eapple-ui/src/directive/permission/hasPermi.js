 /**
 * v-hasPermi 鎿嶄綔鏉冮檺澶勭悊
 * Copyright (c) 2026 Eapp1e
 */

import store from '@/store'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const all_permission = "*:*:*"
    const permissions = store.getters && store.getters.permissions

    if (value && value instanceof Array && value.length > 0) {
      const permissionFlag = value

      const hasPermissions = permissions.some(permission => {
        return all_permission === permission || permissionFlag.includes(permission)
      })

      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`璇疯缃搷浣滄潈闄愭爣绛惧€糮)
    }
  }
}

