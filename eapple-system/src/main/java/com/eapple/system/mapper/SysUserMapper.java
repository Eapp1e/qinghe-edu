package com.eapple.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.common.core.domain.entity.SysUser;

/**
 * 鐢ㄦ埛琛?鏁版嵁灞?
 * 
 * @author Eapp1e
 */
public interface SysUserMapper
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ鐢ㄦ埛鍒楄〃
     * 
     * @param sysUser 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    public List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ宸查厤鐢ㄦ埛瑙掕壊鍒楄〃
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ鏈垎閰嶇敤鎴疯鑹插垪琛?
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 閫氳繃鐢ㄦ埛鍚嶆煡璇㈢敤鎴?
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 鐢ㄦ埛瀵硅薄淇℃伅
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 閫氳繃鐢ㄦ埛ID鏌ヨ鐢ㄦ埛
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鐢ㄦ埛瀵硅薄淇℃伅
     */
    public SysUser selectUserById(Long userId);

    /**
     * 鏂板鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int insertUser(SysUser user);

    /**
     * 淇敼鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int updateUser(SysUser user);

    /**
     * 淇敼鐢ㄦ埛澶村儚
     * 
     * @param userId 鐢ㄦ埛ID
     * @param avatar 澶村儚鍦板潃
     * @return 缁撴灉
     */
    public int updateUserAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    /**
     * 淇敼鐢ㄦ埛鐘舵€?
     * 
     * @param userId 鐢ㄦ埛ID
     * @param status 鐘舵€?
     * @return 缁撴灉
     */
    public int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 鏇存柊鐢ㄦ埛鐧诲綍淇℃伅锛圛P鍜岀櫥褰曟椂闂达級
     * 
     * @param userId 鐢ㄦ埛ID
     * @param loginIp 鐧诲綍IP鍦板潃
     * @param loginDate 鐧诲綍鏃堕棿
     * @return 缁撴灉
     */
    public int updateLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp, @Param("loginDate") Date loginDate);

    /**
     * 閲嶇疆鐢ㄦ埛瀵嗙爜
     * 
     * @param userId 鐢ㄦ埛ID
     * @param password 瀵嗙爜
     * @return 缁撴灉
     */
    public int resetUserPwd(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 閫氳繃鐢ㄦ埛ID鍒犻櫎鐢ㄦ埛
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 缁撴灉
     */
    public int deleteUserById(Long userId);

    /**
     * 鎵归噺鍒犻櫎鐢ㄦ埛淇℃伅
     * 
     * @param userIds 闇€瑕佸垹闄ょ殑鐢ㄦ埛ID
     * @return 缁撴灉
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 鏍￠獙鐢ㄦ埛鍚嶇О鏄惁鍞竴
     * 
     * @param userName 鐢ㄦ埛鍚嶇О
     * @return 缁撴灉
     */
    public SysUser checkUserNameUnique(String userName);

    /**
     * 鏍￠獙鎵嬫満鍙风爜鏄惁鍞竴
     *
     * @param phonenumber 鎵嬫満鍙风爜
     * @return 缁撴灉
     */
    public SysUser checkPhoneUnique(String phonenumber);

    /**
     * 鏍￠獙email鏄惁鍞竴
     *
     * @param email 鐢ㄦ埛閭
     * @return 缁撴灉
     */
    public SysUser checkEmailUnique(String email);
}
