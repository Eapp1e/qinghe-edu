package com.eapple.framework.aspectj;

import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.eapple.common.annotation.DataScope;
import com.eapple.common.constant.Constants;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.BaseEntity;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.core.text.Convert;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.framework.security.context.PermissionContextHolder;

/**
 * 鏁版嵁杩囨护澶勭悊
 *
 * @author Eapp1e
 */
@Aspect
@Component
public class DataScopeAspect
{
    /**
     * 鏁版嵁鏉冮檺杩囨护鍏抽敭瀛?
     */
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        // 鑾峰彇褰撳墠鐨勭敤鎴?
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getUser();
            // 濡傛灉鏄秴绾х鐞嗗憳锛屽垯涓嶈繃婊ゆ暟鎹?
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), PermissionContextHolder.getContext());
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.userAlias(), controllerDataScope.deptAlias(), controllerDataScope.userField(), controllerDataScope.deptField(), permission);
            }
        }
    }

    /**
     * 鏁版嵁鑼冨洿杩囨护
     *
     * @param joinPoint 鍒囩偣
     * @param user 鐢ㄦ埛
     * @param deptAlias 閮ㄩ棬鍒悕
     * @param userAlias 鐢ㄦ埛鍒悕
     * @param permission 鏉冮檺瀛楃
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String userAlias, String deptAlias, String userField, String deptField, String permission)
    {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<String>();
        List<String> scopeCustomIds = new ArrayList<String>();
        user.getRoles().forEach(role -> {
            if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(role.getDataScope()) && StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && (StringUtils.isEmpty(permission) || StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission))))
            {
                scopeCustomIds.add(Convert.toStr(role.getRoleId()));
            }
        });

        for (SysRole role : user.getRoles())
        {
            String dataScope = role.getDataScope();
            if (conditions.contains(dataScope) || StringUtils.equals(role.getStatus(), UserConstants.ROLE_DISABLE))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(permission) && !StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission)))
            {
                continue;
            }
            if (Constants.Dept.DATA_SCOPE_ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            }
            else if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                if (scopeCustomIds.size() > 1)
                {
                    // 澶氫釜鑷畾鏁版嵁鏉冮檺浣跨敤in鏌ヨ锛岄伩鍏嶅娆℃嫾鎺ャ€?
                    sqlString.append(StringUtils.format(" OR {}.{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ", deptAlias, deptField, String.join(",", scopeCustomIds)));
                }
                else
                {
                    sqlString.append(StringUtils.format(" OR {}.{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias, deptField, role.getRoleId()));
                }
            }
            else if (Constants.Dept.DATA_SCOPE_DEPT.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.{} = {} ", deptAlias, deptField, user.getDeptId()));
            }
            else if (Constants.Dept.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.{} IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )", deptAlias, deptField, user.getDeptId(), user.getDeptId()));
            }
            else if (Constants.Dept.DATA_SCOPE_SELF.equals(dataScope))
            {
                if (StringUtils.isNotBlank(userAlias))
                {
                    sqlString.append(StringUtils.format(" OR {}.{} = {} ", userAlias, userField, user.getUserId()));
                }
                else
                {
                    // 鏁版嵁鏉冮檺涓轰粎鏈汉涓旀病鏈塽serAlias鍒悕涓嶆煡璇换浣曟暟鎹?
                    sqlString.append(StringUtils.format(" OR {}.{} = 0 ", deptAlias, deptField));
                }
            }
            conditions.add(dataScope);
        }

        // 瑙掕壊閮戒笉鍖呭惈浼犻€掕繃鏉ョ殑鏉冮檺瀛楃锛岃繖涓椂鍊檚qlString涔熶細涓虹┖锛屾墍浠ヨ闄愬埗涓€涓?涓嶆煡璇换浣曟暟鎹?
        if (StringUtils.isEmpty(conditions))
        {
            sqlString.append(StringUtils.format(" OR {}.{} = 0 ", deptAlias, deptField));
        }

        if (StringUtils.isNotBlank(sqlString.toString()))
        {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 鎷兼帴鏉冮檺sql鍓嶅厛娓呯┖params.dataScope鍙傛暟闃叉娉ㄥ叆
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}
