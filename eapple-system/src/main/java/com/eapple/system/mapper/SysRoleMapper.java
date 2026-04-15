package com.eapple.system.mapper;

import java.util.List;
import com.eapple.common.core.domain.entity.SysRole;

/**
 * 瑙掕壊琛?鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysRoleMapper
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瑙掕壊鏁版嵁
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 瑙掕壊鏁版嵁闆嗗悎淇℃伅
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ瑙掕壊
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 瑙掕壊鍒楄〃
     */
    public List<SysRole> selectRolePermissionByUserId(Long userId);

    /**
     * 鏌ヨ鎵€鏈夎鑹?
     * 
     * @return 瑙掕壊鍒楄〃
     */
    public List<SysRole> selectRoleAll();

    /**
     * 鏍规嵁鐢ㄦ埛ID鑾峰彇瑙掕壊閫夋嫨妗嗗垪琛?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 閫変腑瑙掕壊ID鍒楄〃
     */
    public List<Long> selectRoleListByUserId(Long userId);

    /**
     * 閫氳繃瑙掕壊ID鏌ヨ瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @return 瑙掕壊瀵硅薄淇℃伅
     */
    public SysRole selectRoleById(Long roleId);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ瑙掕壊
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 瑙掕壊鍒楄〃
     */
    public List<SysRole> selectRolesByUserName(String userName);

    /**
     * 鏍￠獙瑙掕壊鍚嶇О鏄惁鍞竴
     * 
     * @param roleName 瑙掕壊鍚嶇О
     * @return 瑙掕壊淇℃伅
     */
    public SysRole checkRoleNameUnique(String roleName);

    /**
     * 鏍￠獙瑙掕壊鏉冮檺鏄惁鍞竴
     * 
     * @param roleKey 瑙掕壊鏉冮檺
     * @return 瑙掕壊淇℃伅
     */
    public SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 淇敼瑙掕壊淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public int updateRole(SysRole role);

    /**
     * 鏂板瑙掕壊淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public int insertRole(SysRole role);

    /**
     * 閫氳繃瑙掕壊ID鍒犻櫎瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    public int deleteRoleById(Long roleId);

    /**
     * 鎵归噺鍒犻櫎瑙掕壊淇℃伅
     * 
     * @param roleIds 闇€瑕佸垹闄ょ殑瑙掕壊ID
     * @return 缁撴灉
     */
    public int deleteRoleByIds(Long[] roleIds);
}
