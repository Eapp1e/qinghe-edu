package com.eapple.system.mapper.edu;

import java.util.List;
import com.eapple.system.domain.edu.EduStudentProfile;

public interface EduStudentProfileMapper
{
    EduStudentProfile selectProfileById(Long profileId);

    EduStudentProfile selectProfileByStudentUserId(Long studentUserId);

    List<EduStudentProfile> selectProfileList(EduStudentProfile profile);

    int insertProfile(EduStudentProfile profile);

    int updateProfile(EduStudentProfile profile);

    int deleteProfileByIds(Long[] profileIds);

    Long countActiveStudents(EduStudentProfile profile);
}
