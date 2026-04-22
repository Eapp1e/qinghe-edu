package com.eapple.system.mapper;

import java.util.List;
import com.eapple.common.core.domain.entity.SysRole;

/**
 * 角色管理数据层。
 * 
 * @author Eapp1e
 */
public interface SysRoleMapper
{
    /**
     * 根据条件分页查询角色数据。
     * 
     * @param role 角色信息
     * @return 角色信息集合
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户 ID 查询角色权限。
     * 
     * @param userId 用户 ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色。
     * 
     * @return 角色集合
     */
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户 ID 查询角色 ID 集合。
     * 
     * @param userId 用户 ID
     * @return 角色 ID 集合
     */
    public List<Long> selectRoleListByUserId(Long userId);

    /**
     * 根据角色 ID 查询角色信息。
     * 
     * @param roleId 角色 ID
     * @return 角色信息
     */
    public SysRole selectRoleById(Long roleId);

    /**
     * 根据用户名查询角色列表。
     * 
     * @param userName 用户名
     * @return 角色集合
     */
    public List<SysRole> selectRolesByUserName(String userName);

    /**
     * 校验角色名称是否唯一。
     * 
     * @param roleName 角色名称
     * @return 结果
     */
    public SysRole checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限字符串是否唯一。
     * 
     * @param roleKey 角色权限字符串
     * @return 结果
     */
    public SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息。
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);

    /**
     * 新增角色信息。
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(SysRole role);

    /**
     * 根据角色 ID 删除角色。
     * 
     * @param roleId 角色 ID
     * @return 结果
     */
    public int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息。
     * 
     * @param roleIds 角色 ID 数组
     * @return 结果
     */
    public int deleteRoleByIds(Long[] roleIds);
}
