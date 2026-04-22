package com.eapple.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.common.core.domain.entity.SysUser;

/**
 * 用户管理数据层。
 * 
 * @author Eapp1e
 */
public interface SysUserMapper
{
    /**
     * 根据条件分页查询用户列表。
     * 
     * @param sysUser 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询已分配角色用户列表。
     * 
     * @param user 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配角色用户列表。
     * 
     * @param user 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 通过用户名查询用户。
     * 
     * @param userName 用户名
     * @return 用户对象
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户 ID 查询用户。
     * 
     * @param userId 用户 ID
     * @return 用户对象
     */
    public SysUser selectUserById(Long userId);

    /**
     * 新增用户信息。
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息。
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户头像。
     * 
     * @param userId 用户 ID
     * @param avatar 头像路径
     * @return 结果
     */
    public int updateUserAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    /**
     * 修改用户状态。
     * 
     * @param userId 用户 ID
     * @param status 状态
     * @return 结果
     */
    public int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 记录用户登录信息。
     * 
     * @param userId 用户 ID
     * @param loginIp 登录 IP 地址
     * @param loginDate 登录时间
     * @return 结果
     */
    public int updateLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp, @Param("loginDate") Date loginDate);

    /**
     * 重置用户密码。
     * 
     * @param userId 用户 ID
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 通过用户 ID 删除用户。
     * 
     * @param userId 用户 ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息。
     * 
     * @param userIds 用户 ID 数组
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 校验用户名是否唯一。
     * 
     * @param userName 用户名
     * @return 结果
     */
    public SysUser checkUserNameUnique(String userName);

    /**
     * 校验手机号是否唯一。
     *
     * @param phonenumber 手机号
     * @return 结果
     */
    public SysUser checkPhoneUnique(String phonenumber);

    /**
     * 校验邮箱是否唯一。
     *
     * @param email 邮箱
     * @return 结果
     */
    public SysUser checkEmailUnique(String email);
}
