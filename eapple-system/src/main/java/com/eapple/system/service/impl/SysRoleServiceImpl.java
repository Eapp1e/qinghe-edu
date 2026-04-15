package com.eapple.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eapple.common.annotation.DataScope;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.spring.SpringUtils;
import com.eapple.system.domain.SysRoleDept;
import com.eapple.system.domain.SysRoleMenu;
import com.eapple.system.domain.SysUserRole;
import com.eapple.system.mapper.SysRoleDeptMapper;
import com.eapple.system.mapper.SysRoleMapper;
import com.eapple.system.mapper.SysRoleMenuMapper;
import com.eapple.system.mapper.SysUserRoleMapper;
import com.eapple.system.service.ISysRoleService;

/**
 * 瑙掕壊 涓氬姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService
{
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瑙掕壊鏁版嵁
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 瑙掕壊鏁版嵁闆嗗悎淇℃伅
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysRole> selectRoleList(SysRole role)
    {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ瑙掕壊
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 瑙掕壊鍒楄〃
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId)
    {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles)
        {
            for (SysRole userRole : userRoles)
            {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue())
                {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鏉冮檺
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鏉冮檺鍒楄〃
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId)
    {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms)
        {
            if (StringUtils.isNotNull(perm))
            {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 鏌ヨ鎵€鏈夎鑹?
     * 
     * @return 瑙掕壊鍒楄〃
     */
    @Override
    public List<SysRole> selectRoleAll()
    {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    /**
     * 鏍规嵁鐢ㄦ埛ID鑾峰彇瑙掕壊閫夋嫨妗嗗垪琛?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 閫変腑瑙掕壊ID鍒楄〃
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId)
    {
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * 閫氳繃瑙掕壊ID鏌ヨ瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @return 瑙掕壊瀵硅薄淇℃伅
     */
    @Override
    public SysRole selectRoleById(Long roleId)
    {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 鏍￠獙瑙掕壊鍚嶇О鏄惁鍞竴
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkRoleNameUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙瑙掕壊鏉冮檺鏄惁鍞竴
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkRoleKeyUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙瑙掕壊鏄惁鍏佽鎿嶄綔
     * 
     * @param role 瑙掕壊淇℃伅
     */
    @Override
    public void checkRoleAllowed(SysRole role)
    {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin())
        {
            throw new ServiceException("涓嶅厑璁告搷浣滆秴绾х鐞嗗憳瑙掕壊");
        }
    }

    /**
     * 鏍￠獙瑙掕壊鏄惁鏈夋暟鎹潈闄?
     * 
     * @param roleIds 瑙掕壊id
     */
    @Override
    public void checkRoleDataScope(Long... roleIds)
    {
        if (!SecurityUtils.isAdmin())
        {
            for (Long roleId : roleIds)
            {
                SysRole role = new SysRole();
                role.setRoleId(roleId);
                List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
                if (StringUtils.isEmpty(roles))
                {
                    throw new ServiceException("娌℃湁鏉冮檺璁块棶瑙掕壊鏁版嵁锛?);
                }
            }
        }
    }

    /**
     * 閫氳繃瑙掕壊ID鏌ヨ瑙掕壊浣跨敤鏁伴噺
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    @Override
    public int countUserRoleByRoleId(Long roleId)
    {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 鏂板淇濆瓨瑙掕壊淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int insertRole(SysRole role)
    {
        // 鏂板瑙掕壊淇℃伅
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    /**
     * 淇敼淇濆瓨瑙掕壊淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int updateRole(SysRole role)
    {
        // 淇敼瑙掕壊淇℃伅
        roleMapper.updateRole(role);
        // 鍒犻櫎瑙掕壊涓庤彍鍗曞叧鑱?
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    /**
     * 淇敼瑙掕壊鐘舵€?
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateRoleStatus(SysRole role)
    {
        return roleMapper.updateRole(role);
    }

    /**
     * 淇敼鏁版嵁鏉冮檺淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int authDataScope(SysRole role)
    {
        // 淇敼瑙掕壊淇℃伅
        roleMapper.updateRole(role);
        // 鍒犻櫎瑙掕壊涓庨儴闂ㄥ叧鑱?
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        // 鏂板瑙掕壊鍜岄儴闂ㄤ俊鎭紙鏁版嵁鏉冮檺锛?
        return insertRoleDept(role);
    }

    /**
     * 鏂板瑙掕壊鑿滃崟淇℃伅
     * 
     * @param role 瑙掕壊瀵硅薄
     */
    public int insertRoleMenu(SysRole role)
    {
        int rows = 1;
        // 鏂板鐢ㄦ埛涓庤鑹茬鐞?
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds())
        {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0)
        {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    /**
     * 鏂板瑙掕壊閮ㄩ棬淇℃伅(鏁版嵁鏉冮檺)
     *
     * @param role 瑙掕壊瀵硅薄
     */
    public int insertRoleDept(SysRole role)
    {
        int rows = 1;
        // 鏂板瑙掕壊涓庨儴闂紙鏁版嵁鏉冮檺锛夌鐞?
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (Long deptId : role.getDeptIds())
        {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (list.size() > 0)
        {
            rows = roleDeptMapper.batchRoleDept(list);
        }
        return rows;
    }

    /**
     * 閫氳繃瑙掕壊ID鍒犻櫎瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int deleteRoleById(Long roleId)
    {
        // 鍒犻櫎瑙掕壊涓庤彍鍗曞叧鑱?
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        // 鍒犻櫎瑙掕壊涓庨儴闂ㄥ叧鑱?
        roleDeptMapper.deleteRoleDeptByRoleId(roleId);
        return roleMapper.deleteRoleById(roleId);
    }

    /**
     * 鎵归噺鍒犻櫎瑙掕壊淇℃伅
     * 
     * @param roleIds 闇€瑕佸垹闄ょ殑瑙掕壊ID
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds)
    {
        for (Long roleId : roleIds)
        {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0)
            {
                throw new ServiceException(String.format("%1$s宸插垎閰?涓嶈兘鍒犻櫎", role.getRoleName()));
            }
        }
        // 鍒犻櫎瑙掕壊涓庤彍鍗曞叧鑱?
        roleMenuMapper.deleteRoleMenu(roleIds);
        // 鍒犻櫎瑙掕壊涓庨儴闂ㄥ叧鑱?
        roleDeptMapper.deleteRoleDept(roleIds);
        return roleMapper.deleteRoleByIds(roleIds);
    }

    /**
     * 鍙栨秷鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param userRole 鐢ㄦ埛鍜岃鑹插叧鑱斾俊鎭?
     * @return 缁撴灉
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole)
    {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    /**
     * 鎵归噺鍙栨秷鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @param userIds 闇€瑕佸彇娑堟巿鏉冪殑鐢ㄦ埛鏁版嵁ID
     * @return 缁撴灉
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds)
    {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    /**
     * 鎵归噺閫夋嫨鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @param userIds 闇€瑕佹巿鏉冪殑鐢ㄦ埛鏁版嵁ID
     * @return 缁撴灉
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds)
    {
        // 鏂板鐢ㄦ埛涓庤鑹茬鐞?
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (Long userId : userIds)
        {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
}
