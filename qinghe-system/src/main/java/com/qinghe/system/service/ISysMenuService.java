package com.qinghe.system.service;

import java.util.List;
import java.util.Set;
import com.qinghe.common.core.domain.TreeSelect;
import com.qinghe.common.core.domain.entity.SysMenu;
import com.qinghe.system.domain.vo.RouterVo;

/**
 * 菜单管理服务接口。
 *
 * @author Eapp1e
 */
public interface ISysMenuService
{
    /**
     * 根据用户查询系统菜单列表。
     *
     * @param userId 用户 ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户和条件查询系统菜单列表。
     *
     * @param menu 菜单条件
     * @param userId 用户 ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户 ID 查询权限集合。
     *
     * @param userId 用户 ID
     * @return 权限集合
     */
    public Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色 ID 查询权限集合。
     *
     * @param roleId 角色 ID
     * @return 权限集合
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户 ID 查询菜单树信息。
     *
     * @param userId 用户 ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色 ID 查询角色已选菜单。
     *
     * @param roleId 角色 ID
     * @return 已选菜单 ID 列表
     */
    public List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端所需的路由。
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需的菜单树结构。
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需的下拉树结构。
     *
     * @param menus 菜单列表
     * @return 下拉树列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单 ID 查询信息。
     *
     * @param menuId 菜单 ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 查询菜单是否存在子节点。
     *
     * @param menuId 菜单 ID
     * @return true 存在，false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否已分配角色。
     *
     * @param menuId 菜单 ID
     * @return true 存在，false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 新增菜单信息。
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改菜单信息。
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 修改菜单排序。
     *
     * @param menuIds 菜单 ID 数组
     * @param orderNums 排序值数组
     */
    public void updateMenuSort(String[] menuIds, String[] orderNums);

    /**
     * 删除菜单管理信息。
     *
     * @param menuId 菜单 ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一。
     *
     * @param menu 菜单信息
     * @return 校验结果
     */
    public boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 校验路由组合是否唯一。
     *
     * @param menu 菜单信息
     * @return 校验结果
     */
    public boolean checkRouteConfigUnique(SysMenu menu);
}
