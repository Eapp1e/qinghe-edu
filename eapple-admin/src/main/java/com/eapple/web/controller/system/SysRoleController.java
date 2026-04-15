package com.eapple.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysDept;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.poi.ExcelUtil;
import com.eapple.framework.web.service.SysPermissionService;
import com.eapple.framework.web.service.TokenService;
import com.eapple.system.domain.SysUserRole;
import com.eapple.system.service.ISysDeptService;
import com.eapple.system.service.ISysRoleService;
import com.eapple.system.service.ISysUserService;

/**
 * 瑙掕壊淇℃伅
 * 
 * @author Eapp1e
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role)
    {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role)
    {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "瑙掕壊鏁版嵁");
    }

    /**
     * 鏍规嵁瑙掕壊缂栧彿鑾峰彇璇︾粏淇℃伅
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId)
    {
        roleService.checkRoleDataScope(roleId);
        return success(roleService.selectRoleById(roleId));
    }

    /**
     * 鏂板瑙掕壊
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRole role)
    {
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("鏂板瑙掕壊'" + role.getRoleName() + "'澶辫触锛岃鑹插悕绉板凡瀛樺湪");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("鏂板瑙掕壊'" + role.getRoleName() + "'澶辫触锛岃鑹叉潈闄愬凡瀛樺湪");
        }
        role.setCreateBy(getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 淇敼淇濆瓨瑙掕壊
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("淇敼瑙掕壊'" + role.getRoleName() + "'澶辫触锛岃鑹插悕绉板凡瀛樺湪");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("淇敼瑙掕壊'" + role.getRoleName() + "'澶辫触锛岃鑹叉潈闄愬凡瀛樺湪");
        }
        role.setUpdateBy(getUsername());
        
        if (roleService.updateRole(role) > 0)
        {
            // 鏇存柊缂撳瓨鐢ㄦ埛鏉冮檺
            LoginUser loginUser = getLoginUser();
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin())
            {
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                tokenService.setLoginUser(loginUser);
            }
            return success();
        }
        return error("淇敼瑙掕壊'" + role.getRoleName() + "'澶辫触锛岃鑱旂郴绠＄悊鍛?);
    }

    /**
     * 淇敼淇濆瓨鏁版嵁鏉冮檺
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 鐘舵€佷慨鏀?
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 鍒犻櫎瑙掕壊
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 鑾峰彇瑙掕壊閫夋嫨妗嗗垪琛?
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return success(roleService.selectRoleAll());
    }

    /**
     * 鏌ヨ宸插垎閰嶇敤鎴疯鑹插垪琛?
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * 鏌ヨ鏈垎閰嶇敤鎴疯鑹插垪琛?
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 鍙栨秷鎺堟潈鐢ㄦ埛
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 鎵归噺鍙栨秷鎺堟潈鐢ㄦ埛
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 鎵归噺閫夋嫨鐢ㄦ埛鎺堟潈
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "瑙掕壊绠＄悊", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds)
    {
        roleService.checkRoleDataScope(roleId);
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 鑾峰彇瀵瑰簲瑙掕壊閮ㄩ棬鏍戝垪琛?
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public AjaxResult deptTree(@PathVariable("roleId") Long roleId)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
        return ajax;
    }
}
