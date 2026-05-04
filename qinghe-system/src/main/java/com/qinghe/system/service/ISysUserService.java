package com.qinghe.system.service;

import java.util.Date;
import java.util.List;
import com.qinghe.common.core.domain.entity.SysUser;

/**
 * 用户服务接口
 * 
 * @author Eapp1e
 */
public interface ISysUserService
{
    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 根据条件分页查询已分配角色的用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配角色的用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户 ID 查询用户
     * 
     * @param userId 用户 ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 根据用户名查询所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName);

    /**
     * 根据用户名查询所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserPostGroup(String userName);

    /**
     * 校验用户名是否唯一
     * 
     * @param user 用户信息
     * @return 结果
     */
    public boolean checkUserNameUnique(SysUser user);

    /**
     * 校验手机号是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public boolean checkPhoneUnique(SysUser user);

    /**
     * 校验邮箱是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public boolean checkEmailUnique(SysUser user);

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 校验用户是否具备数据权限
     * 
     * @param userId 用户 ID
     */
    public void checkUserDataScope(Long userId);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 注册用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public boolean registerUser(SysUser user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 用户授权角色
     * 
     * @param userId 用户 ID
     * @param roleIds 角色 ID 组
     */
    public void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     * 
     * @param userId 用户 ID
     * @param avatar 头像地址
     * @return 结果
     */
    public boolean updateUserAvatar(Long userId, String avatar);

    /**
     * 更新用户登录信息（IP 和登录时间）
     * 
     * @param userId 用户 ID
     * @param loginIp 登录 IP 地址
     * @param loginDate 登录时间
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate);

    /**
     * 重置密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user);

    /**
     * 重置用户密码
     * 
     * @param userId 用户 ID
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(Long userId, String password);

    /**
     * 通过用户 ID 删除用户
     * 
     * @param userId 用户 ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户 ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 导入用户数据
     * 
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否支持更新，若已存在则更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);
}
