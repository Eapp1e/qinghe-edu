package com.eapple.common.core.domain.entity;

import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 瑙掕壊琛?sys_role
 * 
 * @author Eapp1e
 */
public class SysRole extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 瑙掕壊ID */
    @Excel(name = "瑙掕壊搴忓彿", cellType = ColumnType.NUMERIC)
    private Long roleId;

    /** 瑙掕壊鍚嶇О */
    @Excel(name = "瑙掕壊鍚嶇О")
    private String roleName;

    /** 瑙掕壊鏉冮檺 */
    @Excel(name = "瑙掕壊鏉冮檺")
    private String roleKey;

    /** 瑙掕壊鎺掑簭 */
    @Excel(name = "瑙掕壊鎺掑簭")
    private Integer roleSort;

    /** 鏁版嵁鑼冨洿锛?锛氭墍鏈夋暟鎹潈闄愶紱2锛氳嚜瀹氫箟鏁版嵁鏉冮檺锛?锛氭湰閮ㄩ棬鏁版嵁鏉冮檺锛?锛氭湰閮ㄩ棬鍙婁互涓嬫暟鎹潈闄愶紱5锛氫粎鏈汉鏁版嵁鏉冮檺锛?*/
    @Excel(name = "鏁版嵁鑼冨洿", readConverterExp = "1=鎵€鏈夋暟鎹潈闄?2=鑷畾涔夋暟鎹潈闄?3=鏈儴闂ㄦ暟鎹潈闄?4=鏈儴闂ㄥ強浠ヤ笅鏁版嵁鏉冮檺,5=浠呮湰浜烘暟鎹潈闄?)
    private String dataScope;

    /** 鑿滃崟鏍戦€夋嫨椤规槸鍚﹀叧鑱旀樉绀猴紙 0锛氱埗瀛愪笉浜掔浉鍏宠仈鏄剧ず 1锛氱埗瀛愪簰鐩稿叧鑱旀樉绀猴級 */
    private boolean menuCheckStrictly;

    /** 閮ㄩ棬鏍戦€夋嫨椤规槸鍚﹀叧鑱旀樉绀猴紙0锛氱埗瀛愪笉浜掔浉鍏宠仈鏄剧ず 1锛氱埗瀛愪簰鐩稿叧鑱旀樉绀?锛?*/
    private boolean deptCheckStrictly;

    /** 瑙掕壊鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?*/
    @Excel(name = "瑙掕壊鐘舵€?, readConverterExp = "0=姝ｅ父,1=鍋滅敤")
    private String status;

    /** 鍒犻櫎鏍囧織锛?浠ｈ〃瀛樺湪 2浠ｈ〃鍒犻櫎锛?*/
    private String delFlag;

    /** 鐢ㄦ埛鏄惁瀛樺湪姝よ鑹叉爣璇?榛樿涓嶅瓨鍦?*/
    private boolean flag = false;

    /** 鑿滃崟缁?*/
    private Long[] menuIds;

    /** 閮ㄩ棬缁勶紙鏁版嵁鏉冮檺锛?*/
    private Long[] deptIds;

    /** 瑙掕壊鑿滃崟鏉冮檺 */
    private Set<String> permissions;

    public SysRole()
    {

    }

    public SysRole(Long roleId)
    {
        this.roleId = roleId;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId)
    {
        return roleId != null && 1L == roleId;
    }

    @NotBlank(message = "瑙掕壊鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 30, message = "瑙掕壊鍚嶇О闀垮害涓嶈兘瓒呰繃30涓瓧绗?)
    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    @NotBlank(message = "鏉冮檺瀛楃涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "鏉冮檺瀛楃闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getRoleKey()
    {
        return roleKey;
    }

    public void setRoleKey(String roleKey)
    {
        this.roleKey = roleKey;
    }

    @NotNull(message = "鏄剧ず椤哄簭涓嶈兘涓虹┖")
    public Integer getRoleSort()
    {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort)
    {
        this.roleSort = roleSort;
    }

    public String getDataScope()
    {
        return dataScope;
    }

    public void setDataScope(String dataScope)
    {
        this.dataScope = dataScope;
    }

    public boolean isMenuCheckStrictly()
    {
        return menuCheckStrictly;
    }

    public void setMenuCheckStrictly(boolean menuCheckStrictly)
    {
        this.menuCheckStrictly = menuCheckStrictly;
    }

    public boolean isDeptCheckStrictly()
    {
        return deptCheckStrictly;
    }

    public void setDeptCheckStrictly(boolean deptCheckStrictly)
    {
        this.deptCheckStrictly = deptCheckStrictly;
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

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public Long[] getMenuIds()
    {
        return menuIds;
    }

    public void setMenuIds(Long[] menuIds)
    {
        this.menuIds = menuIds;
    }

    public Long[] getDeptIds()
    {
        return deptIds;
    }

    public void setDeptIds(Long[] deptIds)
    {
        this.deptIds = deptIds;
    }

    public Set<String> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(Set<String> permissions)
    {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roleId", getRoleId())
            .append("roleName", getRoleName())
            .append("roleKey", getRoleKey())
            .append("roleSort", getRoleSort())
            .append("dataScope", getDataScope())
            .append("menuCheckStrictly", isMenuCheckStrictly())
            .append("deptCheckStrictly", isDeptCheckStrictly())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
