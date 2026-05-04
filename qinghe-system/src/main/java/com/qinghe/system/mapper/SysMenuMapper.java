package com.qinghe.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.qinghe.common.core.domain.entity.SysMenu;

/**
 * 菜单管理数据层。
 *
 * @author Eapp1e
 */
public interface SysMenuMapper
{
    /**
     * 查询系统菜单列表。
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询系统所有权限标识。
     *
     * @return 权限标识集合
     */
    public List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表。
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuListByUserId(SysMenu menu);

    /**
     * 根据角色 ID 查询权限标识。
     * 
     * @param roleId 角色 ID
     * @return 权限标识集合
     */
    public List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户 ID 查询权限标识。
     *
     * @param userId 用户 ID
     * @return 权限标识集合
     */
    public List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 查询所有菜单。
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户 ID 查询菜单。
     *
     * @param userId 用户 ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色 ID 查询菜单树信息。
     * 
     * @param roleId 角色 ID
     * @param menuCheckStrictly 是否开启菜单树选择项关联显示
     * @return 菜单列表
     */
    public List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 根据菜单 ID 查询信息。
     *
     * @param menuId 菜单 ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在子节点。
     *
     * @param menuId 菜单 ID
     * @return 结果
     */
    public int hasChildByMenuId(Long menuId);

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
     * @param menu 菜单信息
     */
    public void updateMenuSort(SysMenu menu);

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
     * @param menuName 菜单名称
     * @param parentId 父菜单 ID
     * @return 结果
     */
    public SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    /**
     * 根据路由路径或路由名称查询冲突菜单。
     *
     * @param path 路由地址
     * @param routeName 路由名称
     * @return 冲突菜单列表
     */
    public List<SysMenu> selectMenusByPathOrRouteName(@Param("path") String path, @Param("routeName") String routeName);
}
