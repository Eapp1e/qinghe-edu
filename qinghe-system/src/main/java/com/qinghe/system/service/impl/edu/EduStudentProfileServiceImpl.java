package com.qinghe.system.service.impl.edu;

import java.util.List;
import com.qinghe.common.core.domain.entity.SysRole;
import com.qinghe.common.core.domain.entity.SysUser;
import com.qinghe.common.exception.ServiceException;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.system.domain.edu.EduStudentProfile;
import com.qinghe.system.mapper.edu.EduStudentProfileMapper;
import com.qinghe.system.service.ISysUserService;
import com.qinghe.system.service.edu.IEduStudentProfileService;
import com.qinghe.system.util.EduSchoolScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EduStudentProfileServiceImpl implements IEduStudentProfileService
{
    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Autowired
    private ISysUserService userService;

    @Override
    public List<EduStudentProfile> selectProfileList(EduStudentProfile profile)
    {
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            profile.setParentUserId(SecurityUtils.getUserId());
        }
        else if (SecurityUtils.hasExactRole("edu_student"))
        {
            profile.setStudentUserId(SecurityUtils.getUserId());
        }
        EduSchoolScopeUtils.applySchoolScope(profile);
        return profileMapper.selectProfileList(profile);
    }

    @Override
    public EduStudentProfile selectProfileById(Long profileId)
    {
        EduStudentProfile profile = profileMapper.selectProfileById(profileId);
        ensureProfileAccessible(profile);
        return profile;
    }

    @Override
    public List<EduStudentProfile> selectCurrentUserChildren()
    {
        EduStudentProfile profile = new EduStudentProfile();
        profile.setParentUserId(SecurityUtils.getUserId());
        EduSchoolScopeUtils.applySchoolScope(profile);
        return profileMapper.selectProfileList(profile);
    }

    @Override
    public EduStudentProfile selectProfileByStudentUserId(Long studentUserId)
    {
        return profileMapper.selectProfileByStudentUserId(studentUserId);
    }

    @Override
    public EduStudentProfile validateManagedStudent(Long studentUserId)
    {
        SysUser studentUser = requireUser(studentUserId, "学生账号不存在");
        if (!hasRole(studentUser, "edu_student"))
        {
            throw new ServiceException("所选账号不是学生账号");
        }
        ensureSameSchool(studentUser, "只能为本学校学生账号新建档案");
        if (profileMapper.selectProfileByStudentUserId(studentUserId) != null)
        {
            throw new ServiceException("该学生档案已存在");
        }

        EduStudentProfile profile = new EduStudentProfile();
        profile.setStudentUserId(studentUser.getUserId());
        profile.setStudentName(displayName(studentUser));
        return profile;
    }

    @Override
    public EduStudentProfile validateManagedParent(Long parentUserId)
    {
        SysUser parentUser = requireUser(parentUserId, "家长账号不存在");
        if (!hasRole(parentUser, "edu_parent"))
        {
            throw new ServiceException("所选家长账号不是家长账号");
        }
        ensureSameSchool(parentUser, "只能绑定本学校家长账号");

        EduStudentProfile profile = new EduStudentProfile();
        profile.setParentUserId(parentUser.getUserId());
        profile.setParentName(displayName(parentUser));
        return profile;
    }

    @Override
    public int insertProfile(EduStudentProfile profile)
    {
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            profile.setStudentUserId(SecurityUtils.getUserId());
            if (StringUtils.isEmpty(profile.getStudentName()))
            {
                SysUser currentUser = userService.selectUserById(SecurityUtils.getUserId());
                profile.setStudentName(currentUser == null ? SecurityUtils.getUsername() : displayName(currentUser));
            }
            bindParentByAccount(profile);
            if (profile.getParentUserId() == null)
            {
                throw new ServiceException("首次完善档案时，请先绑定家长账号");
            }
            if (profileMapper.selectProfileByStudentUserId(profile.getStudentUserId()) != null)
            {
                throw new ServiceException("该学生档案已存在");
            }
        }
        else
        {
            prepareManagedProfileForCreate(profile);
        }
        ensureProfileEditable(profile);
        profile.setCreateBy(SecurityUtils.getUsername());
        int rows = profileMapper.insertProfile(profile);
        syncStudentNickname(profile);
        return rows;
    }

    @Override
    public int updateProfile(EduStudentProfile profile)
    {
        EduStudentProfile currentProfile = profileMapper.selectProfileById(profile.getProfileId());
        ensureProfileEditable(currentProfile);
        profile.setStudentUserId(currentProfile.getStudentUserId());
        profile.setParentUserId(currentProfile.getParentUserId());
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            profile.setStatus(currentProfile.getStatus());
            bindParentByAccount(profile);
            if (StringUtils.isEmpty(profile.getParentAccount()))
            {
                profile.setParentUserId(currentProfile.getParentUserId());
                profile.setParentName(currentProfile.getParentName());
            }
        }
        else
        {
            profile.setParentName(currentProfile.getParentName());
        }
        profile.setUpdateBy(SecurityUtils.getUsername());
        int rows = profileMapper.updateProfile(profile);
        syncStudentNickname(profile);
        return rows;
    }

    @Override
    public int deleteProfileByIds(Long[] profileIds)
    {
        for (Long profileId : profileIds)
        {
            ensureProfileEditable(profileMapper.selectProfileById(profileId));
        }
        return profileMapper.deleteProfileByIds(profileIds);
    }

    private void prepareManagedProfileForCreate(EduStudentProfile profile)
    {
        EduStudentProfile studentProfile = validateManagedStudent(profile.getStudentUserId());
        EduStudentProfile parentProfile = validateManagedParent(profile.getParentUserId());

        if (StringUtils.isEmpty(profile.getStudentName()))
        {
            profile.setStudentName(studentProfile.getStudentName());
        }
        profile.setParentName(parentProfile.getParentName());
    }

    private void bindParentByAccount(EduStudentProfile profile)
    {
        if (StringUtils.isEmpty(profile.getParentAccount()))
        {
            return;
        }
        SysUser parentUser = userService.selectUserByUserName(profile.getParentAccount());
        if (parentUser == null)
        {
            throw new ServiceException("未找到对应的家长账号");
        }
        if (!hasRole(parentUser, "edu_parent"))
        {
            throw new ServiceException("绑定失败，所填账号不是家长账号");
        }
        ensureSameSchool(parentUser, "只能绑定本学校的家长账号");
        profile.setParentUserId(parentUser.getUserId());
        profile.setParentName(displayName(parentUser));
    }

    private void syncStudentNickname(EduStudentProfile profile)
    {
        if (!SecurityUtils.hasExactRole("edu_student") || StringUtils.isEmpty(profile.getStudentName()))
        {
            return;
        }
        SysUser user = new SysUser();
        user.setUserId(SecurityUtils.getUserId());
        user.setNickName(profile.getStudentName());
        userService.updateUserProfile(user);
    }

    private SysUser requireUser(Long userId, String message)
    {
        if (userId == null)
        {
            throw new ServiceException(message);
        }
        SysUser user = userService.selectUserById(userId);
        if (user == null)
        {
            throw new ServiceException(message);
        }
        return user;
    }

    private boolean hasRole(SysUser user, String roleKey)
    {
        List<SysRole> roles = user.getRoles();
        if (roles == null)
        {
            return false;
        }
        return roles.stream().anyMatch(role -> roleKey.equals(role.getRoleKey()));
    }

    private String displayName(SysUser user)
    {
        return StringUtils.defaultString(user.getNickName(), user.getUserName());
    }

    private void ensureSameSchool(SysUser user, String message)
    {
        Long currentSchoolId = EduSchoolScopeUtils.getCurrentSchoolId();
        if (!SecurityUtils.isAdmin() && currentSchoolId != null && !currentSchoolId.equals(user.getSchoolId()))
        {
            throw new ServiceException(message);
        }
    }

    private void ensureProfileAccessible(EduStudentProfile profile)
    {
        if (profile == null)
        {
            throw new ServiceException("学生档案不存在");
        }
        if (SecurityUtils.hasExactRole("edu_student") && !SecurityUtils.getUserId().equals(profile.getStudentUserId()))
        {
            throw new ServiceException("只能查看自己的学生档案");
        }
        if (SecurityUtils.hasExactRole("edu_parent") && !SecurityUtils.getUserId().equals(profile.getParentUserId()))
        {
            throw new ServiceException("只能查看自己关联孩子的学生档案");
        }
    }

    private void ensureProfileEditable(EduStudentProfile profile)
    {
        if (SecurityUtils.hasExactRole("edu_teacher") || SecurityUtils.isAdmin())
        {
            return;
        }
        ensureProfileAccessible(profile);
    }
}
