package com.qinghe.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.qinghe.system.domain.SysUserRole;

/**
 * 用户与角色关联数据层。
 * 
 * @author Eapp1e
 */
public interface SysUserRoleMapper
{
    /**
     * 根据用户 ID 删除用户与角色关联。
     * 
     * @param userId 用户 ID
     * @return 结果
     */
    public int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户与角色关联。
     * 
     * @param ids 关联 ID 数组
     * @return 结果
     */
    public int deleteUserRole(Long[] ids);

    /**
     * 根据角色 ID 统计用户角色关联数量。
     * 
     * @param roleId 角色 ID
     * @return 结果
     */
    public int countUserRoleByRoleId(Long roleId);

    /**
     * 批量新增用户与角色关联。
     * 
     * @param userRoleList 用户角色关联列表
     * @return 结果
     */
    public int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 删除单条用户角色关联信息。
     * 
     * @param userRole 用户角色关联对象
     * @return 结果
     */
    public int deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 批量取消指定角色下的用户授权。
     * 
     * @param roleId 角色 ID
     * @param userIds 用户 ID 数组
     * @return 结果
     */
    public int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}
