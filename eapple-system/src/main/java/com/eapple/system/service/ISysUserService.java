package com.eapple.system.service;

import java.util.Date;
import java.util.List;
import com.eapple.common.core.domain.entity.SysUser;

/**
 * 鐢ㄦ埛 涓氬姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysUserService
{
    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ鐢ㄦ埛鍒楄〃
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ宸插垎閰嶇敤鎴疯鑹插垪琛?
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
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鐢ㄦ埛鎵€灞炶鑹茬粍
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 缁撴灉
     */
    public String selectUserRoleGroup(String userName);

    /**
     * 鏍规嵁鐢ㄦ埛ID鏌ヨ鐢ㄦ埛鎵€灞炲矖浣嶇粍
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 缁撴灉
     */
    public String selectUserPostGroup(String userName);

    /**
     * 鏍￠獙鐢ㄦ埛鍚嶇О鏄惁鍞竴
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public boolean checkUserNameUnique(SysUser user);

    /**
     * 鏍￠獙鎵嬫満鍙风爜鏄惁鍞竴
     *
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public boolean checkPhoneUnique(SysUser user);

    /**
     * 鏍￠獙email鏄惁鍞竴
     *
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public boolean checkEmailUnique(SysUser user);

    /**
     * 鏍￠獙鐢ㄦ埛鏄惁鍏佽鎿嶄綔
     * 
     * @param user 鐢ㄦ埛淇℃伅
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 鏍￠獙鐢ㄦ埛鏄惁鏈夋暟鎹潈闄?
     * 
     * @param userId 鐢ㄦ埛id
     */
    public void checkUserDataScope(Long userId);

    /**
     * 鏂板鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int insertUser(SysUser user);

    /**
     * 娉ㄥ唽鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public boolean registerUser(SysUser user);

    /**
     * 淇敼鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int updateUser(SysUser user);

    /**
     * 鐢ㄦ埛鎺堟潈瑙掕壊
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑙掕壊缁?
     */
    public void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 淇敼鐢ㄦ埛鐘舵€?
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int updateUserStatus(SysUser user);

    /**
     * 淇敼鐢ㄦ埛鍩烘湰淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int updateUserProfile(SysUser user);

    /**
     * 淇敼鐢ㄦ埛澶村儚
     * 
     * @param userId 鐢ㄦ埛ID
     * @param avatar 澶村儚鍦板潃
     * @return 缁撴灉
     */
    public boolean updateUserAvatar(Long userId, String avatar);

    /**
     * 鏇存柊鐢ㄦ埛鐧诲綍淇℃伅锛圛P鍜岀櫥褰曟椂闂达級
     * 
     * @param userId 鐢ㄦ埛ID
     * @param loginIp 鐧诲綍IP鍦板潃
     * @param loginDate 鐧诲綍鏃堕棿
     * @return 缁撴灉
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate);

    /**
     * 閲嶇疆鐢ㄦ埛瀵嗙爜
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    public int resetPwd(SysUser user);

    /**
     * 閲嶇疆鐢ㄦ埛瀵嗙爜
     * 
     * @param userId 鐢ㄦ埛ID
     * @param password 瀵嗙爜
     * @return 缁撴灉
     */
    public int resetUserPwd(Long userId, String password);

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
     * 瀵煎叆鐢ㄦ埛鏁版嵁
     * 
     * @param userList 鐢ㄦ埛鏁版嵁鍒楄〃
     * @param isUpdateSupport 鏄惁鏇存柊鏀寔锛屽鏋滃凡瀛樺湪锛屽垯杩涜鏇存柊鏁版嵁
     * @param operName 鎿嶄綔鐢ㄦ埛
     * @return 缁撴灉
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);
}
