package com.ruoyi.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.edu.EduStudentProfile;
import com.ruoyi.system.mapper.edu.EduStudentProfileMapper;
import com.ruoyi.system.service.edu.IEduStudentProfileService;

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
        return profileMapper.selectProfileById(profileId);
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
        if (profileMapper.selectProfileByStudentUserId(profile.getStudentUserId()) != null)
        {
            throw new ServiceException("该学生档案已存在");
        }
        profile.setCreateBy(SecurityUtils.getUsername());
        return profileMapper.insertProfile(profile);
    }

    @Override
    public int updateProfile(EduStudentProfile profile)
    {
        profile.setUpdateBy(SecurityUtils.getUsername());
        return profileMapper.updateProfile(profile);
    }

    @Override
    public int deleteProfileByIds(Long[] profileIds)
    {
        return profileMapper.deleteProfileByIds(profileIds);
    }
}
