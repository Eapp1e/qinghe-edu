package com.eapple.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.system.domain.SysUserRole;

/**
 * 鐢ㄦ埛涓庤鑹插叧鑱旇〃 鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysUserRoleMapper
{
    /**
     * 閫氳繃鐢ㄦ埛ID鍒犻櫎鐢ㄦ埛鍜岃鑹插叧鑱?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 缁撴灉
     */
    public int deleteUserRoleByUserId(Long userId);

    /**
     * 鎵归噺鍒犻櫎鐢ㄦ埛鍜岃鑹插叧鑱?
     * 
     * @param ids 闇€瑕佸垹闄ょ殑鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteUserRole(Long[] ids);

    /**
     * 閫氳繃瑙掕壊ID鏌ヨ瑙掕壊浣跨敤鏁伴噺
     * 
     * @param roleId 瑙掕壊ID
     * @return 缁撴灉
     */
    public int countUserRoleByRoleId(Long roleId);

    /**
     * 鎵归噺鏂板鐢ㄦ埛瑙掕壊淇℃伅
     * 
     * @param userRoleList 鐢ㄦ埛瑙掕壊鍒楄〃
     * @return 缁撴灉
     */
    public int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 鍒犻櫎鐢ㄦ埛鍜岃鑹插叧鑱斾俊鎭?
     * 
     * @param userRole 鐢ㄦ埛鍜岃鑹插叧鑱斾俊鎭?
     * @return 缁撴灉
     */
    public int deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 鎵归噺鍙栨秷鎺堟潈鐢ㄦ埛瑙掕壊
     * 
     * @param roleId 瑙掕壊ID
     * @param userIds 闇€瑕佸垹闄ょ殑鐢ㄦ埛鏁版嵁ID
     * @return 缁撴灉
     */
    public int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}
