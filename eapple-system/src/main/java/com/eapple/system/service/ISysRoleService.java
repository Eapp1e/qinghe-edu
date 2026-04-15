package com.eapple.system.service;

import java.util.List;
import java.util.Set;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.system.domain.SysUserRole;

/**
 * 瑙掕壊涓氬姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysRoleService
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ瑙掕壊鏁版嵁
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 瑙掕壊鏁版嵁闆嗗悎淇℃伅
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ瑙掕壊鍒楄〃
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 瑙掕壊鍒楄〃
     */
    public List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ瑙掕壊鏉冮檺
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鏉冮檺鍒楄〃
     */
    public Set<String> selectRolePermissionByUserId(Long userId);

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
     * 鏍￠獙瑙掕壊鍚嶇О鏄惁鍞竴
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public boolean checkRoleNameUnique(SysRole role);

    /**
     * 鏍￠獙瑙掕壊鏉冮檺鏄惁鍞竴
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public boolean checkRoleKeyUnique(SysRole role);

    /**
     * 鏍￠獙瑙掕壊鏄惁鍏佽鎿嶄綔
     * 
     * @param role 瑙掕壊淇℃伅
     */
    public void checkRoleAllowed(SysRole role);

    /**
     * 鏍￠獙瑙掕壊鏄惁鏈夋暟鎹潈闄?
     * 
     * @param roleIds 瑙掕壊id
     */
    public void checkRoleDataScope(Long... roleIds);

    /**
     * 閫氳繃瑙掕壊ID鏌ヨ瑙掕壊浣跨敤鏁伴噺
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    public int countUserRoleByRoleId(Long roleId);

    /**
     * 鏂板淇濆瓨瑙掕壊淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public int insertRole(SysRole role);

    /**
     * 淇敼淇濆瓨瑙掕壊淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public int updateRole(SysRole role);

    /**
     * 淇敼瑙掕壊鐘舵€?
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public int updateRoleStatus(SysRole role);

    /**
     * 淇敼鏁版嵁鏉冮檺淇℃伅
     * 
     * @param role 瑙掕壊淇℃伅
     * @return 缁撴灉
     */
    public int authDataScope(SysRole role);

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

    /**
     * 鍙栨秷鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param userRole 鐢ㄦ埛鍜岃鑹插叧鑱斾俊鎭?
     * @return 缁撴灉
     */
    public int deleteAuthUser(SysUserRole userRole);

    /**
     * 鎵归噺鍙栨秷鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @param userIds 闇€瑕佸彇娑堟巿鏉冪殑鐢ㄦ埛鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteAuthUsers(Long roleId, Long[] userIds);

    /**
     * 鎵归噺閫夋嫨鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @param userIds 闇€瑕佸垹闄ょ殑鐢ㄦ埛鏁版嵁ID
     * @return 缁撴灉
     */
    public int insertAuthUsers(Long roleId, Long[] userIds);
}
