package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysRoleMenu;

/**
 * 角色与菜单关联数据层。
 * 
 * @author Eapp1e
 */
public interface SysRoleMenuMapper
{
    /**
     * 查询菜单是否存在角色关联。
     * 
     * @param menuId 菜单 ID
     * @return 结果
     */
    public int checkMenuExistRole(Long menuId);

    /**
     * 根据角色 ID 删除角色与菜单关联。
     * 
     * @param roleId 角色 ID
     * @return 结果
     */
    public int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 批量删除角色与菜单关联。
     * 
     * @param ids 关联 ID 数组
     * @return 结果
     */
    public int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色与菜单关联。
     * 
     * @param roleMenuList 角色菜单关联列表
     * @return 结果
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
