package com.eapple.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.eapple.common.constant.CacheConstants;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.RegisterBody;
import com.eapple.common.core.redis.RedisCache;
import com.eapple.common.exception.user.CaptchaException;
import com.eapple.common.exception.user.CaptchaExpireException;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.MessageUtils;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.framework.manager.AsyncManager;
import com.eapple.framework.manager.factory.AsyncFactory;
import com.eapple.system.service.ISysConfigService;
import com.eapple.system.service.ISysUserService;

/**
 * 娉ㄥ唽鏍￠獙鏂规硶
 * 
 * @author Eapp1e
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 娉ㄥ唽
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 楠岃瘉鐮佸紑鍏?
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "鐢ㄦ埛鍚嶄笉鑳戒负绌?;
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "鐢ㄦ埛瀵嗙爜涓嶈兘涓虹┖";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "璐︽埛闀垮害蹇呴』鍦?鍒?0涓瓧绗︿箣闂?;
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "瀵嗙爜闀垮害蹇呴』鍦?鍒?0涓瓧绗︿箣闂?;
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "淇濆瓨鐢ㄦ埛'" + username + "'澶辫触锛屾敞鍐岃处鍙峰凡瀛樺湪";
        }
        else
        {
            sysUser.setNickName(username);
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "娉ㄥ唽澶辫触,璇疯仈绯荤郴缁熺鐞嗕汉鍛?;
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 鏍￠獙楠岃瘉鐮?
     * 
     * @param username 鐢ㄦ埛鍚?
     * @param code 楠岃瘉鐮?
     * @param uuid 鍞竴鏍囪瘑
     * @return 缁撴灉
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
