package com.eapple.framework.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.enums.UserStatus;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.MessageUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.service.ISysUserService;

/**
 * 鐢ㄦ埛楠岃瘉澶勭悊
 *
 * @author Eapp1e
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user))
        {
            log.info("鐧诲綍鐢ㄦ埛锛歿} 涓嶅瓨鍦?", username);
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("鐧诲綍鐢ㄦ埛锛歿} 宸茶鍒犻櫎.", username);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("鐧诲綍鐢ㄦ埛锛歿} 宸茶鍋滅敤.", username);
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        passwordService.validate(user);

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
