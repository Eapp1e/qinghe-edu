package com.qinghe.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.qinghe.common.core.domain.entity.SysDept;

/**
 * 部门管理数据层。
 * 
 * @author Eapp1e
 */
public interface SysDeptMapper
{
    /**
     * 查询部门管理数据。
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据角色 ID 查询部门树信息。
     * 
     * @param roleId 角色 ID
     * @param deptCheckStrictly 是否开启部门树选择项关联显示
     * @return 部门列表
     */
    public List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    /**
     * 根据部门 ID 查询部门信息。
     * 
     * @param deptId 部门 ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 根据 ID 查询所有子部门。
     * 
     * @param deptId 部门 ID
     * @return 子部门列表
     */
    public List<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 根据 ID 查询正常状态的子部门数量。
     * 
     * @param deptId 部门 ID
     * @return 子部门数量
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在子节点。
     * 
     * @param deptId 部门 ID
     * @return 结果
     */
    public int hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户。
     * 
     * @param deptId 部门 ID
     * @return 结果
     */
    public int checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一。
     * 
     * @param deptName 部门名称
     * @param parentId 父部门 ID
     * @return 结果
     */
    public SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

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
     * 修改子元素关系中的部门状态。
     * 
     * @param deptIds 部门 ID 组
     */
    public void updateDeptStatusNormal(Long[] deptIds);

    /**
     * 批量修改子部门信息。
     * 
     * @param depts 子部门列表
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 修改部门排序。
     *
     * @param dept 部门信息
     */
    public void updateDeptSort(SysDept dept);

    /**
     * 删除部门管理信息。
     * 
     * @param deptId 部门 ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);
}
