package com.eapple.common.core.domain.entity;

import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.annotation.Excel.Type;
import com.eapple.common.annotation.Excels;
import com.eapple.common.core.domain.BaseEntity;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.xss.Xss;

/**
 * 鐢ㄦ埛瀵硅薄 sys_user
 * 
 * @author Eapp1e
 */
public class SysUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 鐢ㄦ埛ID */
    @Excel(name = "鐢ㄦ埛搴忓彿", type = Type.EXPORT, cellType = ColumnType.NUMERIC, prompt = "鐢ㄦ埛缂栧彿")
    private Long userId;

    /** 閮ㄩ棬ID */
    @Excel(name = "閮ㄩ棬缂栧彿", type = Type.IMPORT)
    private Long deptId;

    /** 鐢ㄦ埛璐﹀彿 */
    @Excel(name = "鐧诲綍鍚嶇О")
    private String userName;

    /** 鐢ㄦ埛鏄电О */
    @Excel(name = "鐢ㄦ埛鍚嶇О")
    private String nickName;

    /** 鐢ㄦ埛閭 */
    @Excel(name = "鐢ㄦ埛閭")
    private String email;

    /** 鎵嬫満鍙风爜 */
    @Excel(name = "鎵嬫満鍙风爜", cellType = ColumnType.TEXT)
    private String phonenumber;

    /** 鐢ㄦ埛鎬у埆 */
    @Excel(name = "鐢ㄦ埛鎬у埆", readConverterExp = "0=鐢?1=濂?2=鏈煡")
    private String sex;

    /** 鐢ㄦ埛澶村儚 */
    private String avatar;

    /** 瀵嗙爜 */
    private String password;

    /** 璐﹀彿鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?*/
    @Excel(name = "璐﹀彿鐘舵€?, readConverterExp = "0=姝ｅ父,1=鍋滅敤")
    private String status;

    /** 鍒犻櫎鏍囧織锛?浠ｈ〃瀛樺湪 2浠ｈ〃鍒犻櫎锛?*/
    private String delFlag;

    /** 鏈€鍚庣櫥褰旾P */
    @Excel(name = "鏈€鍚庣櫥褰旾P", type = Type.EXPORT)
    private String loginIp;

    /** 鏈€鍚庣櫥褰曟椂闂?*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "鏈€鍚庣櫥褰曟椂闂?, width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    /** 瀵嗙爜鏈€鍚庢洿鏂版椂闂?*/
    private Date pwdUpdateDate;

    /** 閮ㄩ棬瀵硅薄 */
    @Excels({
        @Excel(name = "閮ㄩ棬鍚嶇О", targetAttr = "deptName", type = Type.EXPORT),
        @Excel(name = "閮ㄩ棬璐熻矗浜?, targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDept dept;

    /** 瑙掕壊瀵硅薄 */
    private List<SysRole> roles;

    /** 瑙掕壊缁?*/
    private Long[] roleIds;

    /** 宀椾綅缁?*/
    private Long[] postIds;

    /** 瑙掕壊ID */
    private Long roleId;

    public SysUser()
    {

    }

    public SysUser(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return SecurityUtils.isAdmin(this.userId);
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    @Xss(message = "鐢ㄦ埛鏄电О涓嶈兘鍖呭惈鑴氭湰瀛楃")
    @Size(min = 0, max = 30, message = "鐢ㄦ埛鏄电О闀垮害涓嶈兘瓒呰繃30涓瓧绗?)
    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    @Xss(message = "鐢ㄦ埛璐﹀彿涓嶈兘鍖呭惈鑴氭湰瀛楃")
    @NotBlank(message = "鐢ㄦ埛璐﹀彿涓嶈兘涓虹┖")
    @Size(min = 0, max = 30, message = "鐢ㄦ埛璐﹀彿闀垮害涓嶈兘瓒呰繃30涓瓧绗?)
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Email(message = "閭鏍煎紡涓嶆纭?)
    @Size(min = 0, max = 50, message = "閭闀垮害涓嶈兘瓒呰繃50涓瓧绗?)
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "鎵嬫満鍙风爜闀垮害涓嶈兘瓒呰繃11涓瓧绗?)
    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getLoginIp()
    {
        return loginIp;
    }

    public void setLoginIp(String loginIp)
    {
        this.loginIp = loginIp;
    }

    public Date getLoginDate()
    {
        return loginDate;
    }

    public void setLoginDate(Date loginDate)
    {
        this.loginDate = loginDate;
    }

    public Date getPwdUpdateDate()
    {
        return pwdUpdateDate;
    }

    public void setPwdUpdateDate(Date pwdUpdateDate)
    {
        this.pwdUpdateDate = pwdUpdateDate;
    }

    public SysDept getDept()
    {
        return dept;
    }

    public void setDept(SysDept dept)
    {
        this.dept = dept;
    }

    public List<SysRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<SysRole> roles)
    {
        this.roles = roles;
    }

    public Long[] getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds)
    {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds()
    {
        return postIds;
    }

    public void setPostIds(Long[] postIds)
    {
        this.postIds = postIds;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("email", getEmail())
            .append("phonenumber", getPhonenumber())
            .append("sex", getSex())
            .append("avatar", getAvatar())
            .append("password", getPassword())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("loginIp", getLoginIp())
            .append("loginDate", getLoginDate())
            .append("pwdUpdateDate", getPwdUpdateDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("dept", getDept())
            .toString();
    }
}
