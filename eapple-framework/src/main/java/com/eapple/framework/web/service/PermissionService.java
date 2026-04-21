package com.eapple.framework.web.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.eapple.common.constant.Constants;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.framework.security.context.PermissionContextHolder;

/**
 * 鑷畾涔夋潈闄愬疄鐜帮紝ss 鍙栬嚜 Spring Security 棣栧瓧姣? * 
 * @author Eapp1e
 */
@Service("ss")
public class PermissionService
{
    /**
     * 楠岃瘉鐢ㄦ埛鏄惁鍏峰鏌愭潈闄?
     * 
     * @param permission 鏉冮檺瀛楃涓?
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愭潈闄?
     */
    public boolean hasPermi(String permission)
    {
        if (StringUtils.isEmpty(permission))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        PermissionContextHolder.setContext(permission);
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 楠岃瘉鐢ㄦ埛鏄惁涓嶅叿澶囨煇鏉冮檺锛屼笌 hasPermi閫昏緫鐩稿弽
     *
     * @param permission 鏉冮檺瀛楃涓?
     * @return 鐢ㄦ埛鏄惁涓嶅叿澶囨煇鏉冮檺
     */
    public boolean lacksPermi(String permission)
    {
        return hasPermi(permission) != true;
    }

    /**
     * 楠岃瘉鐢ㄦ埛鏄惁鍏锋湁浠ヤ笅浠绘剰涓€涓潈闄?
     *
     * @param permissions 浠?PERMISSION_DELIMITER 涓哄垎闅旂鐨勬潈闄愬垪琛?
     * @return 鐢ㄦ埛鏄惁鍏锋湁浠ヤ笅浠绘剰涓€涓潈闄?
     */
    public boolean hasAnyPermi(String permissions)
    {
        if (StringUtils.isEmpty(permissions))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        PermissionContextHolder.setContext(permissions);
        Set<String> authorities = loginUser.getPermissions();
        for (String permission : permissions.split(Constants.PERMISSION_DELIMITER))
        {
            if (permission != null && hasPermissions(authorities, permission))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 鍒ゆ柇鐢ㄦ埛鏄惁鎷ユ湁鏌愪釜瑙掕壊
     * 
     * @param role 瑙掕壊瀛楃涓?
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愯鑹?
     */
    public boolean hasRole(String role)
    {
        if (StringUtils.isEmpty(role))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles()))
        {
            return false;
        }
        for (SysRole sysRole : loginUser.getUser().getRoles())
        {
            String roleKey = sysRole.getRoleKey();
            if (Constants.SUPER_ADMIN.equals(roleKey) || roleKey.equals(StringUtils.trim(role)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 楠岃瘉鐢ㄦ埛鏄惁涓嶅叿澶囨煇瑙掕壊锛屼笌 isRole閫昏緫鐩稿弽銆?
     *
     * @param role 瑙掕壊鍚嶇О
     * @return 鐢ㄦ埛鏄惁涓嶅叿澶囨煇瑙掕壊
     */
    public boolean lacksRole(String role)
    {
        return hasRole(role) != true;
    }

    /**
     * 楠岃瘉鐢ㄦ埛鏄惁鍏锋湁浠ヤ笅浠绘剰涓€涓鑹?
     *
     * @param roles 浠?ROLE_DELIMITER 涓哄垎闅旂鐨勮鑹插垪琛?
     * @return 鐢ㄦ埛鏄惁鍏锋湁浠ヤ笅浠绘剰涓€涓鑹?
     */
    public boolean hasAnyRoles(String roles)
    {
        if (StringUtils.isEmpty(roles))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles()))
        {
            return false;
        }
        for (String role : roles.split(Constants.ROLE_DELIMITER))
        {
            if (hasRole(role))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 鍒ゆ柇鏄惁鍖呭惈鏉冮檺
     * 
     * @param permissions 鏉冮檺鍒楄〃
     * @param permission 鏉冮檺瀛楃涓?
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愭潈闄?
     */
    private boolean hasPermissions(Set<String> permissions, String permission)
    {
        return permissions.contains(Constants.ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission));
    }
}
