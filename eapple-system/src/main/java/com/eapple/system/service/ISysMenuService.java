package com.eapple.system.service;

import java.util.List;
import java.util.Set;
import com.eapple.common.core.domain.TreeSelect;
import com.eapple.common.core.domain.entity.SysMenu;
import com.eapple.system.domain.vo.RouterVo;

/**
 * 鑿滃崟 涓氬姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysMenuService
{
    /**
     * 鏍规嵁鐢ㄦ埛鏌ヨ绯荤粺鑿滃崟鍒楄〃
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鑿滃崟鍒楄〃
     */
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 鏍规嵁鐢ㄦ埛鏌ヨ绯荤粺鑿滃崟鍒楄〃
     * 
     * @param menu 鑿滃崟淇℃伅
     * @param userId 鐢ㄦ埛ID
     * @return 鑿滃崟鍒楄〃
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鏉冮檺
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鏉冮檺鍒楄〃
     */
    public Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ鏉冮檺
     * 
     * @param roleId 瑙掕壊ID
     * @return 鏉冮檺鍒楄〃
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鑿滃崟鏍戜俊鎭?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鑿滃崟鍒楄〃
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 鏍规嵁瑙掕壊ID鏌ヨ鑿滃崟鏍戜俊鎭?
     * 
     * @param roleId 瑙掕壊ID
     * @return 閫変腑鑿滃崟鍒楄〃
     */
    public List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 鏋勫缓鍓嶇璺敱鎵€闇€瑕佺殑鑿滃崟
     * 
     * @param menus 鑿滃崟鍒楄〃
     * @return 璺敱鍒楄〃
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佹爲缁撴瀯
     * 
     * @param menus 鑿滃崟鍒楄〃
     * @return 鏍戠粨鏋勫垪琛?
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 鏋勫缓鍓嶇鎵€闇€瑕佷笅鎷夋爲缁撴瀯
     * 
     * @param menus 鑿滃崟鍒楄〃
     * @return 涓嬫媺鏍戠粨鏋勫垪琛?
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 鏍规嵁鑿滃崟ID鏌ヨ淇℃伅
     * 
     * @param menuId 鑿滃崟ID
     * @return 鑿滃崟淇℃伅
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 鏄惁瀛樺湪鑿滃崟瀛愯妭鐐?
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉 true 瀛樺湪 false 涓嶅瓨鍦?
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 鏌ヨ鑿滃崟鏄惁瀛樺湪瑙掕壊
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉 true 瀛樺湪 false 涓嶅瓨鍦?
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 鏂板淇濆瓨鑿滃崟淇℃伅
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public int insertMenu(SysMenu menu);

    /**
     * 淇敼淇濆瓨鑿滃崟淇℃伅
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public int updateMenu(SysMenu menu);

    /**
     * 淇濆瓨鑿滃崟鎺掑簭
     * 
     * @param menuIds 鑿滃崟ID
     * @param orderNums 鎺掑簭ID
     */
    public void updateMenuSort(String[] menuIds, String[] orderNums);

    /**
     * 鍒犻櫎鑿滃崟绠＄悊淇℃伅
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉
     */
    public int deleteMenuById(Long menuId);

    /**
     * 鏍￠獙鑿滃崟鍚嶇О鏄惁鍞竴
     * 
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 鏍￠獙璺敱缁勫悎鏄惁鍞竴
     *
     * @param menu 鑿滃崟淇℃伅
     * @return 缁撴灉
     */
    public boolean checkRouteConfigUnique(SysMenu menu);
}
