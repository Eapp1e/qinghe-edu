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
 * 安全工具类。
 *
 * @author Eapp1e
 */
public class SecurityUtils
{
    /**
     * 获取当前用户编号。
     **/
    public static Long getUserId()
    {
        try
        {
            return getLoginUser().getUserId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取当前用户 ID 失败", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前用户所属部门编号。
     **/
    public static Long getDeptId()
    {
        try
        {
            return getLoginUser().getDeptId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取当前部门 ID 失败", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前用户名。
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取当前用户名失败", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前登录用户。
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取当前登录用户信息失败", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前认证信息。
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 使用 BCrypt 对密码进行加密。
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 校验明文密码与加密密码是否匹配。
     *
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断当前用户是否为管理员。
     *
     * @return 是否为管理员
     */
    public static boolean isAdmin()
    {
        return isAdmin(getUserId());
    }

    /**
     * 根据用户编号判断是否为管理员。
     *
     * @param userId 用户编号
     * @return 是否为管理员
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 判断当前用户是否具备指定权限。
     *
     * @param permission 权限标识
     * @return 是否具备权限
     */
    public static boolean hasPermi(String permission)
    {
        return hasPermi(getLoginUser().getPermissions(), permission);
    }

    /**
     * 判断权限集合中是否包含指定权限。
     *
     * @param authorities 权限集合
     * @param permission 权限标识
     * @return 是否具备权限
     */
    public static boolean hasPermi(Collection<String> authorities, String permission)
    {
        return authorities.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.ALL_PERMISSION.equals(x) || PatternMatchUtils.simpleMatch(x, permission));
    }

    /**
     * 判断当前用户是否拥有指定角色。
     *
     * @param role 角色标识
     * @return 是否拥有指定角色
     */
    public static boolean hasRole(String role)
    {
        List<SysRole> roleList = getLoginUser().getUser().getRoles();
        Collection<String> roles = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
        return hasRole(roles, role);
    }

    /**
     * 精确判断当前用户是否拥有指定角色，不将超级管理员视为其他业务角色。
     *
     * @param role 角色标识
     * @return 是否拥有指定角色
     */
    public static boolean hasExactRole(String role)
    {
        List<SysRole> roleList = getLoginUser().getUser().getRoles();
        Collection<String> roles = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
        return hasExactRole(roles, role);
    }

    /**
     * 判断角色集合中是否包含指定角色。
     *
     * @param roles 角色集合
     * @param role 角色标识
     * @return 是否拥有指定角色
     */
    public static boolean hasRole(Collection<String> roles, String role)
    {
        return roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.SUPER_ADMIN.equals(x) || PatternMatchUtils.simpleMatch(x, role));
    }

    /**
     * 精确判断角色集合中是否包含指定角色。
     *
     * @param roles 角色集合
     * @param role 角色标识
     * @return 是否精确包含
     */
    public static boolean hasExactRole(Collection<String> roles, String role)
    {
        return roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(x, role));
    }
}
