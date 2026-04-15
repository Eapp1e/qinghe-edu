package com.eapple.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.service.ISysMenuService;
import com.eapple.system.service.ISysRoleService;

/**
 * 鐢ㄦ埛鏉冮檺澶勭悊
 * 
 * @author Eapp1e
 */
@Component
public class SysPermissionService
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 鑾峰彇瑙掕壊鏁版嵁鏉冮檺
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 瑙掕壊鏉冮檺淇℃伅
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 绠＄悊鍛樻嫢鏈夋墍鏈夋潈闄?
        if (user.isAdmin())
        {
            roles.add(Constants.SUPER_ADMIN);
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 鑾峰彇鑿滃崟鏁版嵁鏉冮檺
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鑿滃崟鏉冮檺淇℃伅
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>();
        // 绠＄悊鍛樻嫢鏈夋墍鏈夋潈闄?
        if (user.isAdmin())
        {
            perms.add(Constants.ALL_PERMISSION);
        }
        else
        {
            List<SysRole> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                // 澶氳鑹茶缃畃ermissions灞炴€э紝浠ヤ究鏁版嵁鏉冮檺鍖归厤鏉冮檺
                for (SysRole role : roles)
                {
                    if (StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && !role.isAdmin())
                    {
                        Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                        role.setPermissions(rolePerms);
                        perms.addAll(rolePerms);
                    }
                }
            }
            else
            {
                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
            }
        }
        return perms;
    }
}
