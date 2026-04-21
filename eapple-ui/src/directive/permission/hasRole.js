 /**
 * v-hasRole 瑙掕壊鏉冮檺澶勭悊
 * Copyright (c) 2026 Eapp1e
 */

import store from '@/store'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const super_admin = "admin"
    const roles = store.getters && store.getters.roles

    if (value && value instanceof Array && value.length > 0) {
      const roleFlag = value

      const hasRole = roles.some(role => {
        return super_admin === role || roleFlag.includes(role)
      })

      if (!hasRole) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`璇疯缃鑹叉潈闄愭爣绛惧€?`)
    }
  }
}

