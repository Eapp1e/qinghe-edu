package com.ruoyi.system.service.edu;

import java.util.List;
import com.ruoyi.system.domain.edu.EduStudentProfile;

public interface IEduStudentProfileService
{
    List<EduStudentProfile> selectProfileList(EduStudentProfile profile);

    EduStudentProfile selectProfileById(Long profileId);

    List<EduStudentProfile> selectCurrentUserChildren();

    EduStudentProfile selectProfileByStudentUserId(Long studentUserId);

    int insertProfile(EduStudentProfile profile);

    int updateProfile(EduStudentProfile profile);

    int deleteProfileByIds(Long[] profileIds);
}
