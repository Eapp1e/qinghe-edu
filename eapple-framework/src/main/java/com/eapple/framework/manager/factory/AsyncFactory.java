package com.eapple.framework.manager.factory;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eapple.common.constant.Constants;
import com.eapple.common.utils.LogUtils;
import com.eapple.common.utils.ServletUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.http.UserAgentUtils;
import com.eapple.common.utils.ip.AddressUtils;
import com.eapple.common.utils.ip.IpUtils;
import com.eapple.common.utils.spring.SpringUtils;
import com.eapple.system.domain.SysLogininfor;
import com.eapple.system.domain.SysOperLog;
import com.eapple.system.service.ISysLogininforService;
import com.eapple.system.service.ISysOperLogService;

/**
 * 寮傛宸ュ巶锛堜骇鐢熶换鍔＄敤锛?
 * 
 * @author Eapp1e
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 璁板綍鐧诲綍淇℃伅
     * 
     * @param username 鐢ㄦ埛鍚?
     * @param status 鐘舵€?
     * @param message 娑堟伅
     * @param args 鍒楄〃
     * @return 浠诲姟task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message,
            final Object... args)
    {
        final String userAgent = ServletUtils.getRequest().getHeader("User-Agent");
        final String ip = IpUtils.getIpAddr();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 鎵撳嵃淇℃伅鍒版棩蹇?
                sys_user_logger.info(s.toString(), args);
                // 鑾峰彇瀹㈡埛绔搷浣滅郴缁?
                String os = UserAgentUtils.getOperatingSystem(userAgent);
                // 鑾峰彇瀹㈡埛绔祻瑙堝櫒
                String browser = UserAgentUtils.getBrowser(userAgent);
                // 灏佽瀵硅薄
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setUserName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                // 鏃ュ織鐘舵€?
                if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER))
                {
                    logininfor.setStatus(Constants.SUCCESS);
                }
                else if (Constants.LOGIN_FAIL.equals(status))
                {
                    logininfor.setStatus(Constants.FAIL);
                }
                // 鎻掑叆鏁版嵁
                SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor);
            }
        };
    }

    /**
     * 鎿嶄綔鏃ュ織璁板綍
     * 
     * @param operLog 鎿嶄綔鏃ュ織淇℃伅
     * @return 浠诲姟task
     */
    public static TimerTask recordOper(final SysOperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 杩滅▼鏌ヨ鎿嶄綔鍦扮偣
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }
}
