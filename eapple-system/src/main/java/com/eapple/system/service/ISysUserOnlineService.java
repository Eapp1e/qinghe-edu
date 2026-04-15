package com.eapple.system.service;

import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.system.domain.SysUserOnline;

/**
 * 鍦ㄧ嚎鐢ㄦ埛 鏈嶅姟灞?
 * 
 * @author Eapp1e
 */
public interface ISysUserOnlineService
{
    /**
     * 閫氳繃鐧诲綍鍦板潃鏌ヨ淇℃伅
     * 
     * @param ipaddr 鐧诲綍鍦板潃
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛淇℃伅
     */
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user);

    /**
     * 閫氳繃鐢ㄦ埛鍚嶇О鏌ヨ淇℃伅
     * 
     * @param userName 鐢ㄦ埛鍚嶇О
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛淇℃伅
     */
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user);

    /**
     * 閫氳繃鐧诲綍鍦板潃/鐢ㄦ埛鍚嶇О鏌ヨ淇℃伅
     * 
     * @param ipaddr 鐧诲綍鍦板潃
     * @param userName 鐢ㄦ埛鍚嶇О
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛淇℃伅
     */
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user);

    /**
     * 璁剧疆鍦ㄧ嚎鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛
     */
    public SysUserOnline loginUserToUserOnline(LoginUser user);
}
