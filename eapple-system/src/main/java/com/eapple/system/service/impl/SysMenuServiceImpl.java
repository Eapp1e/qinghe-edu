package com.eapple.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.TreeSelect;
import com.eapple.common.core.domain.entity.SysMenu;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.text.Convert;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.vo.MetaVo;
import com.eapple.system.domain.vo.RouterVo;
import com.eapple.system.mapper.SysMenuMapper;
import com.eapple.system.mapper.SysRoleMapper;
import com.eapple.system.mapper.SysRoleMenuMapper;
import com.eapple.system.service.ISysMenuService;

/**
 * 鑿滃崟 涓氬姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService
{
    private static final Logger log = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    public static final Long MENU_ROOT_ID = 0L;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 鏍规嵁鐢ㄦ埛鏌ヨ绯荤粺鑿滃崟鍒楄〃
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鑿滃崟鍒楄〃
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId)
    {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 鏌ヨ绯荤粺鑿滃崟鍒楄〃
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 鑿滃崟鍒楄〃
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId)
    {
        List<SysMenu> menuList = null;
        // 绠＄悊鍛樻樉绀烘墍鏈夎彍鍗曚俊鎭?
        if (SecurityUtils.isAdmin(userId))
        {
            menuList = menuMapper.selectMenuList(menu);
        }
        else
        {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鏉冮檺
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鏉冮檺鍒楄〃
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId)
    {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ鏉冮檺
     * 
     * @param roleId 瑙掕壊ID
     * @return 鏉冮檺鍒楄〃
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId)
    {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鑿滃崟
     * 
     * @param userId 鐢ㄦ埛鍚嶇О
     * @return 鑿滃崟鍒楄〃
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId)
    {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId))
        {
            menus = menuMapper.selectMenuTreeAll();
        }
        else
        {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, MENU_ROOT_ID);
    }

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ鑿滃崟鏍戜俊鎭?
     * 
     * @param roleId 瑙掕壊ID
     * @return 閫変腑鑿滃崟鍒楄〃
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
    }

    /**
     * 鏋勫缓鍓嶇璺敱鎵€闇€瑕佺殑鑿滃崟
     * 
     * @param menus 鑿滃崟鍒楄〃
     * @return 璺敱鍒楄〃
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus)
    {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus)
        {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (StringUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType()))
            {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            }
            else if (isMenuFrame(menu))
            {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu.getRouteName(), menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            else if (menu.getParentId().intValue() == MENU_ROOT_ID && isInnerLink(menu))
            {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(getRouteName(menu.getRouteName(), routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佹爲缁撴瀯
     * 
     * @param menus 鑿滃崟鍒楄〃
     * @return 鏍戠粨鏋勫垪琛?
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext();)
        {
            SysMenu menu = (SysMenu) iterator.next();
            // 濡傛灉鏄《绾ц妭鐐? 閬嶅巻璇ョ埗鑺傜偣鐨勬墍鏈夊瓙鑺傜偣
            if (!tempList.contains(menu.getParentId()))
            {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佷笅鎷夋爲缁撴瀯
     * 
     * @param menus 鑿滃崟鍒楄〃
     * @return 涓嬫媺鏍戠粨鏋勫垪琛?
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus)
    {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 鏍规嵁鑿滃崟ID鏌ヨ淇℃伅
     * 
     * @param menuId 鑿滃崟ID
     * @return 鑿滃崟淇℃伅
     */
    @Override
    public SysMenu selectMenuById(Long menuId)
    {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * 鏄惁瀛樺湪鑿滃崟瀛愯妭鐐?
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉
     */
    @Override
    public boolean hasChildByMenuId(Long menuId)
    {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 鏌ヨ鑿滃崟浣跨敤鏁伴噺
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉
     */
    @Override
    public boolean checkMenuExistRole(Long menuId)
    {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 鏂板淇濆瓨鑿滃崟淇℃伅
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int insertMenu(SysMenu menu)
    {
        return menuMapper.insertMenu(menu);
    }

    /**
     * 淇敼淇濆瓨鑿滃崟淇℃伅
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateMenu(SysMenu menu)
    {
        return menuMapper.updateMenu(menu);
    }

    /**
     * 淇濆瓨鑿滃崟鎺掑簭
     * 
     * @param menuIds 鑿滃崟ID
     * @param orderNums 鎺掑簭ID
     */
    @Override
    @Transactional
    public void updateMenuSort(String[] menuIds, String[] orderNums)
    {
        try
        {
            for (int i = 0; i < menuIds.length; i++)
            {
                SysMenu menu = new SysMenu();
                menu.setMenuId(Convert.toLong(menuIds[i]));
                menu.setOrderNum(Convert.toInt(orderNums[i]));
                menuMapper.updateMenuSort(menu);
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("淇濆瓨鎺掑簭寮傚父锛岃鑱旂郴绠＄悊鍛?);
        }
    }

    /**
     * 鍒犻櫎鑿滃崟绠＄悊淇℃伅
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉
     */
    @Override
    public int deleteMenuById(Long menuId)
    {
        return menuMapper.deleteMenuById(menuId);
    }

    /**
     * 鏍￠獙鑿滃崟鍚嶇О鏄惁鍞竴
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙璺敱鍚嶇О鏄惁鍞竴
     *
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkRouteConfigUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        Long parentId = menu.getParentId();
        String path = menu.getPath();
        String routeName = StringUtils.isEmpty(menu.getRouteName()) ? path : menu.getRouteName();
        List<SysMenu> sysMenuList = menuMapper.selectMenusByPathOrRouteName(path, routeName);
        for (SysMenu sysMenu : sysMenuList)
        {
            if (sysMenu.getMenuId().longValue() != menuId.longValue())
            {
                Long dbParentId = sysMenu.getParentId();
                String dbPath = sysMenu.getPath();
                String dbRouteName = StringUtils.isEmpty(sysMenu.getRouteName()) ? dbPath : sysMenu.getRouteName();
                if (StringUtils.equalsAnyIgnoreCase(path, dbPath) && parentId.longValue() == dbParentId.longValue())
                {
                    log.warn("[鍚岀骇璺敱鍐茬獊] 鍚岀骇涓嬪凡瀛樺湪鐩稿悓璺敱璺緞 '{}'锛屽啿绐佽彍鍗曪細{}", dbPath, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (StringUtils.equalsAnyIgnoreCase(path, dbPath) && parentId.longValue() == MENU_ROOT_ID)
                {
                    log.warn("[鏍圭洰褰曡矾鐢卞啿绐乚 鏍圭洰褰曚笅璺敱 '{}' 蹇呴』鍞竴锛屽凡琚彍鍗?'{}' 鍗犵敤", path, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (StringUtils.equalsAnyIgnoreCase(routeName, dbRouteName))
                {
                    log.warn("[璺敱鍚嶇О鍐茬獊] 璺敱鍚嶇О '{}' 闇€鍏ㄥ眬鍞竴锛屽凡琚彍鍗?'{}' 浣跨敤", routeName, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
            }
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鑾峰彇璺敱鍚嶇О
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 璺敱鍚嶇О
     */
    public String getRouteName(SysMenu menu)
    {
        // 闈炲閾惧苟涓旀槸涓€绾х洰褰曪紙绫诲瀷涓虹洰褰曪級
        if (isMenuFrame(menu))
        {
            return StringUtils.EMPTY;
        }
        return getRouteName(menu.getRouteName(), menu.getPath());
    }

    /**
     * 鑾峰彇璺敱鍚嶇О锛屽娌℃湁閰嶇疆璺敱鍚嶇О鍒欏彇璺敱鍦板潃
     * 
     * @param name 璺敱鍚嶇О
     * @param path 璺敱鍦板潃
     * @return 璺敱鍚嶇О锛堥┘宄版牸寮忥級
     */
    public String getRouteName(String name, String path)
    {
        String routerName = StringUtils.isNotEmpty(name) ? name : path;
        return StringUtils.capitalize(routerName);
    }

    /**
     * 鑾峰彇璺敱鍦板潃
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 璺敱鍦板潃
     */
    public String getRouterPath(SysMenu menu)
    {
        String routerPath = menu.getPath();
        // 鍐呴摼鎵撳紑澶栫綉鏂瑰紡
        if (menu.getParentId().intValue() != MENU_ROOT_ID && isInnerLink(menu))
        {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 闈炲閾惧苟涓旀槸涓€绾х洰褰曪紙绫诲瀷涓虹洰褰曪級
        if (MENU_ROOT_ID == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame()))
        {
            routerPath = "/" + menu.getPath();
        }
        // 闈炲閾惧苟涓旀槸涓€绾х洰褰曪紙绫诲瀷涓鸿彍鍗曪級
        else if (isMenuFrame(menu))
        {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 鑾峰彇缁勪欢淇℃伅
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁勪欢淇℃伅
     */
    public String getComponent(SysMenu menu)
    {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu))
        {
            component = menu.getComponent();
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != MENU_ROOT_ID && isInnerLink(menu))
        {
            component = UserConstants.INNER_LINK;
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu))
        {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 鏄惁涓鸿彍鍗曞唴閮ㄨ烦杞?
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public boolean isMenuFrame(SysMenu menu)
    {
        return menu.getParentId().intValue() == MENU_ROOT_ID && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 鏄惁涓簆arent_view缁勪欢
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public boolean isParentView(SysMenu menu)
    {
        return menu.getParentId().intValue() != MENU_ROOT_ID && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 鏄惁涓哄唴閾剧粍浠?
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public boolean isInnerLink(SysMenu menu)
    {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 鏍规嵁鐖惰妭鐐圭殑ID鑾峰彇鎵€鏈夊瓙鑺傜偣
     * 
     * @param list 鍒嗙被琛?
     * @param parentId 浼犲叆鐨勭埗鑺傜偣ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, long parentId)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();)
        {
            SysMenu t = (SysMenu) iterator.next();
            // 涓€銆佹牴鎹紶鍏ョ殑鏌愪釜鐖惰妭鐐笽D,閬嶅巻璇ョ埗鑺傜偣鐨勬墍鏈夊瓙鑺傜偣
            if (t.getParentId() == parentId)
            {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 閫掑綊鍒楄〃
     * 
     * @param list 鍒嗙被琛?
     * @param t 瀛愯妭鐐?
     */
    private void recursionFn(List<SysMenu> list, SysMenu t)
    {
        // 寰楀埌瀛愯妭鐐瑰垪琛?
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 寰楀埌瀛愯妭鐐瑰垪琛?
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t)
    {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext())
        {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 鍒ゆ柇鏄惁鏈夊瓙鑺傜偣
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t)
    {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 鍐呴摼鍩熷悕鐗规畩瀛楃鏇挎崲
     * 
     * @return 鏇挎崲鍚庣殑鍐呴摼鍩熷悕
     */
    public String innerLinkReplaceEach(String path)
    {
        return StringUtils.replaceEach(path, new String[] { Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":" },
                new String[] { "", "", "", "/", "/" });
    }
}
