package com.eapple.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.PatternMatchUtils;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.HttpStatus;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.exception.ServiceException;

/**
 * 瀹夊叏鏈嶅姟宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class SecurityUtils
{

    /**
     * 鐢ㄦ埛ID
     **/
    public static Long getUserId()
    {
        try
        {
            return getLoginUser().getUserId();
        }
        catch (Exception e)
        {
            throw new ServiceException("鑾峰彇鐢ㄦ埛ID寮傚父", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 鑾峰彇閮ㄩ棬ID
     **/
    public static Long getDeptId()
    {
        try
        {
            return getLoginUser().getDeptId();
        }
        catch (Exception e)
        {
            throw new ServiceException("鑾峰彇閮ㄩ棬ID寮傚父", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 鑾峰彇鐢ㄦ埛璐︽埛
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("鑾峰彇鐢ㄦ埛璐︽埛寮傚父", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 鑾峰彇鐢ㄦ埛
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("鑾峰彇鐢ㄦ埛淇℃伅寮傚父", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 鑾峰彇Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 鐢熸垚BCryptPasswordEncoder瀵嗙爜
     *
     * @param password 瀵嗙爜
     * @return 鍔犲瘑瀛楃涓?
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 鍒ゆ柇瀵嗙爜鏄惁鐩稿悓
     *
     * @param rawPassword 鐪熷疄瀵嗙爜
     * @param encodedPassword 鍔犲瘑鍚庡瓧绗?
     * @return 缁撴灉
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 鏄惁涓虹鐞嗗憳
     * 
     * @return 缁撴灉
     */
    public static boolean isAdmin()
    {
        return isAdmin(getUserId());
    }

    /**
     * 鏄惁涓虹鐞嗗憳
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 缁撴灉
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 楠岃瘉鐢ㄦ埛鏄惁鍏峰鏌愭潈闄?
     * 
     * @param permission 鏉冮檺瀛楃涓?
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愭潈闄?
     */
    public static boolean hasPermi(String permission)
    {
        return hasPermi(getLoginUser().getPermissions(), permission);
    }

    /**
     * 鍒ゆ柇鏄惁鍖呭惈鏉冮檺
     * 
     * @param authorities 鏉冮檺鍒楄〃
     * @param permission 鏉冮檺瀛楃涓?
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愭潈闄?
     */
    public static boolean hasPermi(Collection<String> authorities, String permission)
    {
        return authorities.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.ALL_PERMISSION.equals(x) || PatternMatchUtils.simpleMatch(x, permission));
    }

    /**
     * 楠岃瘉鐢ㄦ埛鏄惁鎷ユ湁鏌愪釜瑙掕壊
     * 
     * @param role 瑙掕壊鏍囪瘑
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愯鑹?
     */
    public static boolean hasRole(String role)
    {
        List<SysRole> roleList = getLoginUser().getUser().getRoles();
        Collection<String> roles = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
        return hasRole(roles, role);
    }

    /**
     * 鍒ゆ柇鏄惁鍖呭惈瑙掕壊
     * 
     * @param roles 瑙掕壊鍒楄〃
     * @param role 瑙掕壊
     * @return 鐢ㄦ埛鏄惁鍏峰鏌愯鑹叉潈闄?
     */
    public static boolean hasRole(Collection<String> roles, String role)
    {
        return roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.SUPER_ADMIN.equals(x) || PatternMatchUtils.simpleMatch(x, role));
    }

}
