package com.eapple.system.service.impl;

import org.springframework.stereotype.Service;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.SysUserOnline;
import com.eapple.system.service.ISysUserOnlineService;

/**
 * 鍦ㄧ嚎鐢ㄦ埛 鏈嶅姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysUserOnlineServiceImpl implements ISysUserOnlineService
{
    /**
     * 閫氳繃鐧诲綍鍦板潃鏌ヨ淇℃伅
     * 
     * @param ipaddr 鐧诲綍鍦板潃
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛淇℃伅
     */
    @Override
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 閫氳繃鐢ㄦ埛鍚嶇О鏌ヨ淇℃伅
     * 
     * @param userName 鐢ㄦ埛鍚嶇О
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛淇℃伅
     */
    @Override
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user)
    {
        if (StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 閫氳繃鐧诲綍鍦板潃/鐢ㄦ埛鍚嶇О鏌ヨ淇℃伅
     * 
     * @param ipaddr 鐧诲綍鍦板潃
     * @param userName 鐢ㄦ埛鍚嶇О
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛淇℃伅
     */
    @Override
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 璁剧疆鍦ㄧ嚎鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鍦ㄧ嚎鐢ㄦ埛
     */
    @Override
    public SysUserOnline loginUserToUserOnline(LoginUser user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUser()))
        {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpaddr(user.getIpaddr());
        sysUserOnline.setLoginLocation(user.getLoginLocation());
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime());
        if (StringUtils.isNotNull(user.getUser().getDept()))
        {
            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        return sysUserOnline;
    }
}
