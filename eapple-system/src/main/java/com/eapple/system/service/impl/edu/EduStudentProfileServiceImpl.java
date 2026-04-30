package com.eapple.system.service.impl.edu;

import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.ISysUserService;
import com.eapple.system.service.edu.IEduStudentProfileService;
import com.eapple.system.util.EduSchoolScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public int insertProfile(EduStudentProfile profile)
    {
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            profile.setStudentUserId(SecurityUtils.getUserId());
            if (StringUtils.isEmpty(profile.getStudentName()))
            {
                SysUser currentUser = userService.selectUserById(SecurityUtils.getUserId());
                profile.setStudentName(currentUser == null
                        ? SecurityUtils.getUsername()
                        : StringUtils.defaultString(currentUser.getNickName(), currentUser.getUserName()));
            }
            bindParentByAccount(profile);
            if (profile.getParentUserId() == null)
            {
                throw new ServiceException("首次完善档案时，请先绑定家长账号");
            }
        }
        if (profileMapper.selectProfileByStudentUserId(profile.getStudentUserId()) != null)
        {
            throw new ServiceException("该学生档案已存在");
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
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            profile.setStudentUserId(currentProfile.getStudentUserId());
            profile.setStatus(currentProfile.getStatus());
            bindParentByAccount(profile);
            if (StringUtils.isEmpty(profile.getParentAccount()))
            {
                profile.setParentUserId(currentProfile.getParentUserId());
                profile.setParentName(currentProfile.getParentName());
            }
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
        if (!hasParentRole(parentUser))
        {
            throw new ServiceException("绑定失败，所填账号不是家长账号");
        }
        Long currentSchoolId = EduSchoolScopeUtils.getCurrentSchoolId();
        if (!SecurityUtils.isAdmin() && currentSchoolId != null && !currentSchoolId.equals(parentUser.getSchoolId()))
        {
            throw new ServiceException("只能绑定本学校的家长账号");
        }
        profile.setParentUserId(parentUser.getUserId());
        profile.setParentName(StringUtils.defaultString(parentUser.getNickName(), parentUser.getUserName()));
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

    private boolean hasParentRole(SysUser user)
    {
        List<SysRole> roles = user.getRoles();
        if (roles == null)
        {
            return false;
        }
        return roles.stream().anyMatch(role -> "edu_parent".equals(role.getRoleKey()));
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
