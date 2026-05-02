package com.eapple.system.service.edu;

import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.exception.ServiceException;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.ISysUserService;
import com.eapple.system.service.impl.edu.EduStudentProfileServiceImpl;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class EduStudentProfileServiceImplTest
{
    private EduStudentProfileMapper profileMapper;

    private ISysUserService userService;

    private EduStudentProfileServiceImpl service;

    @BeforeEach
    void setUp()
    {
        profileMapper = Mockito.mock(EduStudentProfileMapper.class);
        userService = Mockito.mock(ISysUserService.class);
        service = new EduStudentProfileServiceImpl();
        ReflectionTestUtils.setField(service, "profileMapper", profileMapper);
        ReflectionTestUtils.setField(service, "userService", userService);
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldKeepStudentAndParentUserIdsWhenAdminUpdatesProfile()
    {
        authenticate("edu_admin");

        EduStudentProfile current = new EduStudentProfile();
        current.setProfileId(8L);
        current.setStudentUserId(101L);
        current.setParentUserId(201L);
        current.setStatus("0");
        Mockito.when(profileMapper.selectProfileById(8L)).thenReturn(current);
        Mockito.when(profileMapper.updateProfile(Mockito.any(EduStudentProfile.class))).thenReturn(1);

        EduStudentProfile update = new EduStudentProfile();
        update.setProfileId(8L);
        update.setStudentUserId(102L);
        update.setParentUserId(202L);
        update.setStudentName("Demo Student");
        update.setParentName("Demo Parent");
        update.setGradeName("Grade 3");

        Assertions.assertEquals(1, service.updateProfile(update));

        ArgumentCaptor<EduStudentProfile> captor = ArgumentCaptor.forClass(EduStudentProfile.class);
        Mockito.verify(profileMapper).updateProfile(captor.capture());
        Assertions.assertEquals(101L, captor.getValue().getStudentUserId());
        Assertions.assertEquals(201L, captor.getValue().getParentUserId());
    }

    @Test
    void shouldCreateManagedProfileOnlyForExistingStudentAndParentWithoutProfile()
    {
        authenticate("edu_admin");
        Mockito.when(userService.selectUserById(101L)).thenReturn(user(101L, "student_101", "Student 101", "edu_student"));
        Mockito.when(userService.selectUserById(201L)).thenReturn(user(201L, "parent_201", "Parent 201", "edu_parent"));
        Mockito.when(profileMapper.selectProfileByStudentUserId(101L)).thenReturn(null);
        Mockito.when(profileMapper.insertProfile(Mockito.any(EduStudentProfile.class))).thenReturn(1);

        EduStudentProfile profile = new EduStudentProfile();
        profile.setStudentUserId(101L);
        profile.setParentUserId(201L);
        profile.setGradeName("Grade 3");

        Assertions.assertEquals(1, service.insertProfile(profile));

        ArgumentCaptor<EduStudentProfile> captor = ArgumentCaptor.forClass(EduStudentProfile.class);
        Mockito.verify(profileMapper).insertProfile(captor.capture());
        Assertions.assertEquals("Student 101", captor.getValue().getStudentName());
        Assertions.assertEquals("Parent 201", captor.getValue().getParentName());
    }

    @Test
    void shouldValidateManagedStudentBeforeParentBinding()
    {
        authenticate("edu_admin");
        Mockito.when(userService.selectUserById(101L)).thenReturn(user(101L, "student_101", "Student 101", "edu_student"));
        Mockito.when(profileMapper.selectProfileByStudentUserId(101L)).thenReturn(null);

        EduStudentProfile profile = service.validateManagedStudent(101L);

        Assertions.assertEquals(101L, profile.getStudentUserId());
        Assertions.assertEquals("Student 101", profile.getStudentName());
        Assertions.assertNull(profile.getParentUserId());
    }

    @Test
    void shouldValidateManagedParentAfterStudentBinding()
    {
        authenticate("edu_admin");
        Mockito.when(userService.selectUserById(201L)).thenReturn(user(201L, "parent_201", "Parent 201", "edu_parent"));

        EduStudentProfile profile = service.validateManagedParent(201L);

        Assertions.assertEquals(201L, profile.getParentUserId());
        Assertions.assertEquals("Parent 201", profile.getParentName());
    }

    @Test
    void shouldRejectManagedProfileWhenStudentAccountDoesNotExist()
    {
        authenticate("edu_admin");
        EduStudentProfile profile = new EduStudentProfile();
        profile.setStudentUserId(101L);
        profile.setParentUserId(201L);

        Assertions.assertThrows(ServiceException.class, () -> service.insertProfile(profile));
    }

    @Test
    void shouldRejectManagedProfileWhenStudentAlreadyHasProfile()
    {
        authenticate("edu_admin");
        Mockito.when(userService.selectUserById(101L)).thenReturn(user(101L, "student_101", "Student 101", "edu_student"));
        Mockito.when(profileMapper.selectProfileByStudentUserId(101L)).thenReturn(new EduStudentProfile());

        EduStudentProfile profile = new EduStudentProfile();
        profile.setStudentUserId(101L);
        profile.setParentUserId(201L);

        Assertions.assertThrows(ServiceException.class, () -> service.insertProfile(profile));
        Mockito.verify(profileMapper, Mockito.never()).insertProfile(Mockito.any(EduStudentProfile.class));
    }

    @Test
    void shouldRejectManagedProfileWhenParentAccountDoesNotExist()
    {
        authenticate("edu_admin");
        Mockito.when(userService.selectUserById(101L)).thenReturn(user(101L, "student_101", "Student 101", "edu_student"));
        Mockito.when(profileMapper.selectProfileByStudentUserId(101L)).thenReturn(null);

        EduStudentProfile profile = new EduStudentProfile();
        profile.setStudentUserId(101L);
        profile.setParentUserId(201L);

        Assertions.assertThrows(ServiceException.class, () -> service.insertProfile(profile));
    }

    private void authenticate(String username)
    {
        SysUser user = new SysUser();
        user.setUserId(9L);
        user.setUserName(username);
        user.setSchoolId(1L);
        SysRole role = new SysRole();
        role.setRoleKey(username);
        user.setRoles(java.util.List.of(role));
        LoginUser loginUser = new LoginUser(9L, 100L, user, Set.of());
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(loginUser, null));
    }

    private SysUser user(Long userId, String userName, String nickName, String roleKey)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setSchoolId(1L);
        SysRole role = new SysRole();
        role.setRoleKey(roleKey);
        user.setRoles(java.util.List.of(role));
        return user;
    }
}
