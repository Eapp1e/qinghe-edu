package com.eapple.common.core.domain.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.alibaba.fastjson2.annotation.JSONField;
import com.eapple.common.core.domain.entity.SysUser;

/**
 * 鐧诲綍鐢ㄦ埛韬唤鏉冮檺
 * 
 * @author Eapp1e
 */
public class LoginUser implements UserDetails
{
    private static final long serialVersionUID = 1L;

    /**
     * 鐢ㄦ埛ID
     */
    private Long userId;

    /**
     * 閮ㄩ棬ID
     */
    private Long deptId;

    /**
     * 鐢ㄦ埛鍞竴鏍囪瘑
     */
    private String token;

    /**
     * 鐧诲綍鏃堕棿
     */
    private Long loginTime;

    /**
     * 杩囨湡鏃堕棿
     */
    private Long expireTime;

    /**
     * 鐧诲綍IP鍦板潃
     */
    private String ipaddr;

    /**
     * 鐧诲綍鍦扮偣
     */
    private String loginLocation;

    /**
     * 娴忚鍣ㄧ被鍨?
     */
    private String browser;

    /**
     * 鎿嶄綔绯荤粺
     */
    private String os;

    /**
     * 鏉冮檺鍒楄〃
     */
    private Set<String> permissions;

    /**
     * 鐢ㄦ埛淇℃伅
     */
    private SysUser user;

    public LoginUser()
    {
    }

    public LoginUser(SysUser user, Set<String> permissions)
    {
        this.user = user;
        this.permissions = permissions;
    }

    public LoginUser(Long userId, Long deptId, SysUser user, Set<String> permissions)
    {
        this.userId = userId;
        this.deptId = deptId;
        this.user = user;
        this.permissions = permissions;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    @JSONField(serialize = false)
    @Override
    public String getPassword()
    {
        return user.getPassword();
    }

    @Override
    public String getUsername()
    {
        return user.getUserName();
    }

    /**
     * 璐︽埛鏄惁鏈繃鏈?杩囨湡鏃犳硶楠岃瘉
     */
    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /**
     * 鎸囧畾鐢ㄦ埛鏄惁瑙ｉ攣,閿佸畾鐨勭敤鎴锋棤娉曡繘琛岃韩浠介獙璇?
     * 
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    /**
     * 鎸囩ず鏄惁宸茶繃鏈熺殑鐢ㄦ埛鐨勫嚟鎹?瀵嗙爜),杩囨湡鐨勫嚟鎹槻姝㈣璇?
     * 
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /**
     * 鏄惁鍙敤 ,绂佺敤鐨勭敤鎴蜂笉鑳借韩浠介獙璇?
     * 
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isEnabled()
    {
        return true;
    }

    public Long getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Long loginTime)
    {
        this.loginTime = loginTime;
    }

    public String getIpaddr()
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation()
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation)
    {
        this.loginLocation = loginLocation;
    }

    public String getBrowser()
    {
        return browser;
    }

    public void setBrowser(String browser)
    {
        this.browser = browser;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public Long getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Long expireTime)
    {
        this.expireTime = expireTime;
    }

    public Set<String> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(Set<String> permissions)
    {
        this.permissions = permissions;
    }

    public SysUser getUser()
    {
        return user;
    }

    public void setUser(SysUser user)
    {
        this.user = user;
    }

    @Override
    @JSONField(serialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if (permissions == null || permissions.isEmpty())
        {
            return Collections.emptyList();
        }
        return permissions.stream()
            .filter(Objects::nonNull)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
