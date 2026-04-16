package com.eapple.system.service.impl.edu;

import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.edu.IEduStudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EduStudentProfileServiceImpl implements IEduStudentProfileService
{
    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Override
    public List<EduStudentProfile> selectProfileList(EduStudentProfile profile)
    {
        if (SecurityUtils.hasRole("edu_parent"))
        {
            profile.setParentUserId(SecurityUtils.getUserId());
        }
        else if (SecurityUtils.hasRole("edu_student"))
        {
            profile.setStudentUserId(SecurityUtils.getUserId());
        }
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
        if (SecurityUtils.hasRole("edu_student"))
        {
            profile.setStudentUserId(SecurityUtils.getUserId());
            if (StringUtils.isEmpty(profile.getStudentName()))
            {
                profile.setStudentName(SecurityUtils.getUsername());
            }
        }
        if (profileMapper.selectProfileByStudentUserId(profile.getStudentUserId()) != null)
        {
            throw new ServiceException("该学生档案已存在");
        }
        ensureProfileEditable(profile);
        profile.setCreateBy(SecurityUtils.getUsername());
        return profileMapper.insertProfile(profile);
    }

    @Override
    public int updateProfile(EduStudentProfile profile)
    {
        EduStudentProfile currentProfile = profileMapper.selectProfileById(profile.getProfileId());
        ensureProfileEditable(currentProfile);
        if (SecurityUtils.hasRole("edu_student"))
        {
            profile.setStudentUserId(currentProfile.getStudentUserId());
            profile.setParentUserId(currentProfile.getParentUserId());
            profile.setParentName(currentProfile.getParentName());
            profile.setStatus(currentProfile.getStatus());
        }
        profile.setUpdateBy(SecurityUtils.getUsername());
        return profileMapper.updateProfile(profile);
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

    private void ensureProfileAccessible(EduStudentProfile profile)
    {
        if (profile == null)
        {
            throw new ServiceException("学生档案不存在");
        }
        if (SecurityUtils.hasRole("edu_student") && !SecurityUtils.getUserId().equals(profile.getStudentUserId()))
        {
            throw new ServiceException("只能查看自己的学生档案");
        }
        if (SecurityUtils.hasRole("edu_parent") && !SecurityUtils.getUserId().equals(profile.getParentUserId()))
        {
            throw new ServiceException("只能查看自己关联孩子的学生档案");
        }
    }

    private void ensureProfileEditable(EduStudentProfile profile)
    {
        if (SecurityUtils.hasRole("edu_teacher") || SecurityUtils.isAdmin())
        {
            return;
        }
        ensureProfileAccessible(profile);
    }
}
