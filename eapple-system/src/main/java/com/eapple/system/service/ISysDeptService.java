package com.eapple.system.service;

import java.util.List;
import com.eapple.common.core.domain.TreeSelect;
import com.eapple.common.core.domain.entity.SysDept;

/**
 * 部门管理服务接口。
 *
 * @author Eapp1e
 */
public interface ISysDeptService
{
    /**
     * 查询部门管理数据。
     *
     * @param dept 部门条件
     * @return 部门集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 查询部门树结构信息。
     *
     * @param dept 部门条件
     * @return 部门树列表
     */
    public List<TreeSelect> selectDeptTreeList(SysDept dept);

    /**
     * 构建前端所需的部门树结构。
     *
     * @param depts 部门列表
     * @return 部门树
     */
    public List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 构建前端所需的下拉树结构。
     *
     * @param depts 部门列表
     * @return 下拉树列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

    /**
     * 根据角色 ID 查询部门树信息。
     *
     * @param roleId 角色 ID
     * @return 已选择的部门列表
     */
    public List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 根据部门 ID 查询信息。
     *
     * @param deptId 部门 ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 根据 ID 查询所有正常子部门数量。
     *
     * @param deptId 部门 ID
     * @return 子部门数量
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 查询部门是否存在子部门。
     *
     * @param deptId 部门 ID
     * @return true 存在，false 不存在
     */
    public boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户。
     *
     * @param deptId 部门 ID
     * @return true 存在，false 不存在
     */
    public boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一。
     *
     * @param dept 部门信息
     * @return 校验结果
     */
    public boolean checkDeptNameUnique(SysDept dept);

    /**
     * 校验部门数据权限。
     *
     * @param deptId 部门 ID
     */
    public void checkDeptDataScope(Long deptId);

    /**
     * 新增部门信息。
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改部门信息。
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 修改部门排序。
     *
     * @param deptIds 部门 ID 数组
     * @param orderNums 排序值数组
     */
    public void updateDeptSort(String[] deptIds, String[] orderNums);

    /**
     * 删除部门信息。
     *
     * @param deptId 部门 ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);
}
