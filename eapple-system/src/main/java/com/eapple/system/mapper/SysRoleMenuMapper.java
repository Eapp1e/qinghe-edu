package com.eapple.system.mapper;

import java.util.List;
import com.eapple.system.domain.SysRoleMenu;

/**
 * 瑙掕壊涓庤彍鍗曞叧鑱旇〃 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysRoleMenuMapper
{
    /**
     * 鏌ヨ鑿滃崟浣跨敤鏁伴噺
     * 
     * @param menuId 鑿滃崟ID
     * @return 缁撴灉
     */
    public int checkMenuExistRole(Long menuId);

    /**
     * 閫氳繃瑙掕壊ID鍒犻櫎瑙掕壊鍜岃彍鍗曞叧鑱?
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    public int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 鎵归噺鍒犻櫎瑙掕壊鑿滃崟鍏宠仈淇℃伅
     * 
     * @param ids 闇€瑕佸垹闄ょ殑鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteRoleMenu(Long[] ids);

    /**
     * 鎵归噺鏂板瑙掕壊鑿滃崟淇℃伅
     * 
     * @param roleMenuList 瑙掕壊鑿滃崟鍒楄〃
     * @return 缁撴灉
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
