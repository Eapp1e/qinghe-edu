package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysRoleDept;

/**
 * 角色与部门关联数据层。
 * 
 * @author Eapp1e
 */
public interface SysRoleDeptMapper
{
    /**
     * 根据角色 ID 删除角色与部门关联。
     * 
     * @param roleId 角色 ID
     * @return 结果
     */
    public int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色与部门关联。
     * 
     * @param ids 关联 ID 数组
     * @return 结果
     */
    public int deleteRoleDept(Long[] ids);

    /**
     * 根据部门 ID 查询角色部门关联数量。
     * 
     * @param deptId 部门 ID
     * @return 结果
     */
    public int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 批量新增角色与部门关联。
     * 
     * @param roleDeptList 角色部门关联列表
     * @return 结果
     */
    public int batchRoleDept(List<SysRoleDept> roleDeptList);
}
