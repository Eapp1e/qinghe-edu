package com.eapple.framework.web.service;

import com.eapple.common.constant.CacheConstants;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysRole;
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
import com.eapple.system.mapper.SysRoleMapper;
import com.eapple.system.service.ISysConfigService;
import com.eapple.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册校验方法
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

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "";
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String loginRole = registerBody.getLoginRole();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            SysRole targetRole = resolveRegisterRole(loginRole);
            if (targetRole == null)
            {
                return "请选择有效的注册身份";
            }
            sysUser.setNickName(username);
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "注册失败，请联系系统管理员";
            }
            else
            {
                userService.insertUserAuth(sysUser.getUserId(), new Long[] { targetRole.getRoleId() });
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                        MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    private SysRole resolveRegisterRole(String loginRole)
    {
        if (!StringUtils.equalsAny(loginRole, "edu_student", "edu_parent", "edu_teacher"))
        {
            return null;
        }
        return roleMapper.checkRoleKeyUnique(loginRole);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
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
