package com.qinghe.system.util;

import com.qinghe.common.core.domain.BaseEntity;
import com.qinghe.common.core.domain.entity.SysUser;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.common.utils.StringUtils;

public class EduSchoolScopeUtils
{
    public static final Long DEFAULT_SCHOOL_ID = 1L;
    public static final String DEFAULT_SCHOOL_NAME = "青禾学校";

    private EduSchoolScopeUtils()
    {
    }

    public static void bindDefaultSchool(SysUser user)
    {
        if (user == null)
        {
            return;
        }
        if ((user.getUserId() != null && SecurityUtils.isAdmin(user.getUserId()))
                || StringUtils.equals(user.getUserName(), "admin"))
        {
            user.setSchoolId(null);
            user.setSchoolName(null);
            return;
        }
        user.setSchoolId(DEFAULT_SCHOOL_ID);
        user.setSchoolName(DEFAULT_SCHOOL_NAME);
    }

    public static void applySchoolScope(BaseEntity entity)
    {
        if (entity == null || SecurityUtils.isAdmin())
        {
            return;
        }
        Long schoolId = getCurrentSchoolId();
        entity.getParams().put("schoolId", schoolId == null ? -1L : schoolId);
    }

    public static Long getCurrentSchoolId()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        return user == null ? null : user.getSchoolId();
    }
}
