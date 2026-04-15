package com.eapple.framework.web.service;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.eapple.common.constant.CacheConstants;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.core.redis.RedisCache;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.exception.user.BlackListException;
import com.eapple.common.exception.user.CaptchaException;
import com.eapple.common.exception.user.CaptchaExpireException;
import com.eapple.common.exception.user.UserNotExistsException;
import com.eapple.common.exception.user.UserPasswordNotMatchException;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.MessageUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.ip.IpUtils;
import com.eapple.framework.manager.AsyncManager;
import com.eapple.framework.manager.factory.AsyncFactory;
import com.eapple.framework.security.context.AuthenticationContextHolder;
import com.eapple.system.service.ISysConfigService;
import com.eapple.system.service.ISysUserService;

/**
 * 鐧诲綍鏍￠獙鏂规硶
 * 
 * @author Eapp1e
 */
@Component
public class SysLoginService
{
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 鐧诲綍楠岃瘉
     * 
     * @param username 鐢ㄦ埛鍚?
     * @param password 瀵嗙爜
     * @param code 楠岃瘉鐮?
     * @param uuid 鍞竴鏍囪瘑
     * @return 缁撴灉
     */
    public String login(String username, String password, String code, String uuid, String loginRole)
    {
        // 楠岃瘉鐮佹牎楠?
        validateCaptcha(username, code, uuid);
        // 鐧诲綍鍓嶇疆鏍￠獙
        loginPreCheck(username, password);
        // 鐢ㄦ埛楠岃瘉
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 璇ユ柟娉曚細鍘昏皟鐢║serDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        validateLoginRole(loginUser, username, loginRole);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        recordLoginInfo(loginUser.getUserId());
        // 鐢熸垚token
        return tokenService.createToken(loginUser);
    }

    private void validateLoginRole(LoginUser loginUser, String username, String loginRole)
    {
        if (StringUtils.isEmpty(loginRole))
        {
            return;
        }
        List<SysRole> roles = loginUser.getUser().getRoles();
        if (roles == null || roles.isEmpty())
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "鏈垎閰嶅彲鐢ㄨ鑹?));
            throw new ServiceException("褰撳墠璐﹀彿鏈垎閰嶅彲鐢ㄨ鑹诧紝璇疯仈绯荤鐞嗗憳");
        }
        boolean matched = roles.stream()
                .map(SysRole::getRoleKey)
                .filter(StringUtils::isNotEmpty)
                .anyMatch(roleKey -> roleMatches(loginRole, roleKey));
        if (!matched)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "鐧诲綍瑙掕壊涓庤处鍙疯鑹蹭笉鍖归厤"));
            throw new ServiceException("鎵€閫夌櫥褰曡鑹蹭笌褰撳墠璐﹀彿涓嶅尮閰嶏紝璇烽噸鏂伴€夋嫨");
        }
    }

    private boolean roleMatches(String loginRole, String roleKey)
    {
        if (StringUtils.equals(loginRole, roleKey))
        {
            return true;
        }
        return StringUtils.equals("edu_admin", loginRole) && StringUtils.equals(Constants.SUPER_ADMIN, roleKey);
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
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 鐧诲綍鍓嶇疆鏍￠獙
     * @param username 鐢ㄦ埛鍚?
     * @param password 鐢ㄦ埛瀵嗙爜
     */
    public void loginPreCheck(String username, String password)
    {
        // 鐢ㄦ埛鍚嶆垨瀵嗙爜涓虹┖ 閿欒
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 瀵嗙爜濡傛灉涓嶅湪鎸囧畾鑼冨洿鍐?閿欒
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 鐢ㄦ埛鍚嶄笉鍦ㄦ寚瀹氳寖鍥村唴 閿欒
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP榛戝悕鍗曟牎楠?
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 璁板綍鐧诲綍淇℃伅
     *
     * @param userId 鐢ㄦ埛ID
     */
    public void recordLoginInfo(Long userId)
    {
        userService.updateLoginInfo(userId, IpUtils.getIpAddr(), DateUtils.getNowDate());
    }
}
