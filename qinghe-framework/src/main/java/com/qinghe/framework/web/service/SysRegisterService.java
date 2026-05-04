package com.qinghe.framework.web.service;

import java.util.regex.Pattern;
import com.qinghe.common.constant.CacheConstants;
import com.qinghe.common.constant.Constants;
import com.qinghe.common.constant.UserConstants;
import com.qinghe.common.core.domain.entity.SysRole;
import com.qinghe.common.core.domain.entity.SysUser;
import com.qinghe.common.core.domain.model.RegisterBody;
import com.qinghe.common.core.redis.RedisCache;
import com.qinghe.common.exception.user.CaptchaException;
import com.qinghe.common.exception.user.CaptchaExpireException;
import com.qinghe.common.utils.DateUtils;
import com.qinghe.common.utils.MessageUtils;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.framework.manager.AsyncManager;
import com.qinghe.framework.manager.factory.AsyncFactory;
import com.qinghe.system.mapper.SysRoleMapper;
import com.qinghe.system.service.ISysConfigService;
import com.qinghe.system.service.ISysUserService;
import com.qinghe.system.util.EduSchoolScopeUtils;
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
    private static final Pattern REGISTER_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

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
        String teacherType = registerBody.getTeacherType();
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
        else if (!REGISTER_USERNAME_PATTERN.matcher(username).matches())
        {
            msg = "注册账号只能由字母和数字组成";
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
            if (StringUtils.equals(loginRole, "edu_teacher") && !StringUtils.equalsAny(teacherType,
                    "science", "humanities", "art", "sports", "computer", "general"))
            {
                return "请选择有效的教师类型";
            }
            sysUser.setNickName(username);
            EduSchoolScopeUtils.bindDefaultSchool(sysUser);
            if (StringUtils.equals(loginRole, "edu_teacher"))
            {
                sysUser.setTeacherType(teacherType);
            }
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
