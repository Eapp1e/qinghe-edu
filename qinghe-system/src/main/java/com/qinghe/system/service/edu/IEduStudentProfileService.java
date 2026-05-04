package com.qinghe.system.service.edu;

import java.util.List;
import com.qinghe.system.domain.edu.EduStudentProfile;

public interface IEduStudentProfileService
{
    List<EduStudentProfile> selectProfileList(EduStudentProfile profile);

    EduStudentProfile selectProfileById(Long profileId);

    List<EduStudentProfile> selectCurrentUserChildren();

    EduStudentProfile selectProfileByStudentUserId(Long studentUserId);

    EduStudentProfile validateManagedStudent(Long studentUserId);

    EduStudentProfile validateManagedParent(Long parentUserId);

    int insertProfile(EduStudentProfile profile);

    int updateProfile(EduStudentProfile profile);

    int deleteProfileByIds(Long[] profileIds);
}
