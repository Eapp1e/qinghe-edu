package com.eapple.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.eapple.common.annotation.DataScope;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.entity.SysRole;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.bean.BeanValidators;
import com.eapple.common.utils.spring.SpringUtils;
import com.eapple.system.domain.SysPost;
import com.eapple.system.domain.SysUserPost;
import com.eapple.system.domain.SysUserRole;
import com.eapple.system.mapper.SysPostMapper;
import com.eapple.system.mapper.SysRoleMapper;
import com.eapple.system.mapper.SysUserMapper;
import com.eapple.system.mapper.SysUserPostMapper;
import com.eapple.system.mapper.SysUserRoleMapper;
import com.eapple.system.service.ISysConfigService;
import com.eapple.system.service.ISysDeptService;
import com.eapple.system.service.ISysUserService;

/**
 * 鐢ㄦ埛 涓氬姟灞傚鐞?
 * 
 * @author Eapp1e
 */
@Service
public class SysUserServiceImpl implements ISysUserService
{
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    protected Validator validator;

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ鐢ㄦ埛鍒楄〃
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ宸插垎閰嶇敤鎴疯鑹插垪琛?
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user)
    {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 鏍规嵁鏉′欢鍒嗛〉鏌ヨ鏈垎閰嶇敤鎴疯鑹插垪琛?
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 鐢ㄦ埛淇℃伅闆嗗悎淇℃伅
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 閫氳繃鐢ㄦ埛鍚嶆煡璇㈢敤鎴?
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 鐢ㄦ埛瀵硅薄淇℃伅
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 閫氳繃鐢ㄦ埛ID鏌ヨ鐢ㄦ埛
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鐢ㄦ埛瀵硅薄淇℃伅
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    /**
     * 鏌ヨ鐢ㄦ埛鎵€灞炶鑹茬粍
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 缁撴灉
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 鏌ヨ鐢ㄦ埛鎵€灞炲矖浣嶇粍
     * 
     * @param userName 鐢ㄦ埛鍚?
     * @return 缁撴灉
     */
    @Override
    public String selectUserPostGroup(String userName)
    {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 鏍￠獙鐢ㄦ埛鍚嶇О鏄惁鍞竴
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙鎵嬫満鍙风爜鏄惁鍞竴
     *
     * @param user 鐢ㄦ埛淇℃伅
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙email鏄惁鍞竴
     *
     * @param user 鐢ㄦ埛淇℃伅
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 鏍￠獙鐢ㄦ埛鏄惁鍏佽鎿嶄綔
     * 
     * @param user 鐢ㄦ埛淇℃伅
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin())
        {
            throw new ServiceException("涓嶅厑璁告搷浣滆秴绾х鐞嗗憳鐢ㄦ埛");
        }
    }

    /**
     * 鏍￠獙鐢ㄦ埛鏄惁鏈夋暟鎹潈闄?
     * 
     * @param userId 鐢ㄦ埛id
     */
    @Override
    public void checkUserDataScope(Long userId)
    {
        if (!SecurityUtils.isAdmin())
        {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users))
            {
                throw new ServiceException("娌℃湁鏉冮檺璁块棶鐢ㄦ埛鏁版嵁锛?);
            }
        }
    }

    /**
     * 鏂板淇濆瓨鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int insertUser(SysUser user)
    {
        // 鏂板鐢ㄦ埛淇℃伅
        int rows = userMapper.insertUser(user);
        // 鏂板鐢ㄦ埛宀椾綅鍏宠仈
        insertUserPost(user);
        // 鏂板鐢ㄦ埛涓庤鑹茬鐞?
        insertUserRole(user);
        return rows;
    }

    /**
     * 娉ㄥ唽鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    public boolean registerUser(SysUser user)
    {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 淇敼淇濆瓨鐢ㄦ埛淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        // 鍒犻櫎鐢ㄦ埛涓庤鑹插叧鑱?
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 鏂板鐢ㄦ埛涓庤鑹茬鐞?
        insertUserRole(user);
        // 鍒犻櫎鐢ㄦ埛涓庡矖浣嶅叧鑱?
        userPostMapper.deleteUserPostByUserId(userId);
        // 鏂板鐢ㄦ埛涓庡矖浣嶇鐞?
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 鐢ㄦ埛鎺堟潈瑙掕壊
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑙掕壊缁?
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds)
    {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 淇敼鐢ㄦ埛鐘舵€?
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateUserStatus(SysUser user)
    {
        return userMapper.updateUserStatus(user.getUserId(), user.getStatus());
    }

    /**
     * 淇敼鐢ㄦ埛鍩烘湰淇℃伅
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 淇敼鐢ㄦ埛澶村儚
     * 
     * @param userId 鐢ㄦ埛ID
     * @param avatar 澶村儚鍦板潃
     * @return 缁撴灉
     */
    @Override
    public boolean updateUserAvatar(Long userId, String avatar)
    {
        return userMapper.updateUserAvatar(userId, avatar) > 0;
    }

    /**
     * 鏇存柊鐢ㄦ埛鐧诲綍淇℃伅锛圛P鍜岀櫥褰曟椂闂达級
     * 
     * @param userId 鐢ㄦ埛ID
     * @param loginIp 鐧诲綍IP鍦板潃
     * @param loginDate 鐧诲綍鏃堕棿
     * @return 缁撴灉
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate)
    {
        userMapper.updateLoginInfo(userId, loginIp, loginDate);
    }

    /**
     * 閲嶇疆鐢ㄦ埛瀵嗙爜
     * 
     * @param user 鐢ㄦ埛淇℃伅
     * @return 缁撴灉
     */
    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.resetUserPwd(user.getUserId(), user.getPassword());
    }

    /**
     * 閲嶇疆鐢ㄦ埛瀵嗙爜
     * 
     * @param userId 鐢ㄦ埛ID
     * @param password 瀵嗙爜
     * @return 缁撴灉
     */
    @Override
    public int resetUserPwd(Long userId, String password)
    {
        return userMapper.resetUserPwd(userId, password);
    }

    /**
     * 鏂板鐢ㄦ埛瑙掕壊淇℃伅
     * 
     * @param user 鐢ㄦ埛瀵硅薄
     */
    public void insertUserRole(SysUser user)
    {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 鏂板鐢ㄦ埛宀椾綅淇℃伅
     * 
     * @param user 鐢ㄦ埛瀵硅薄
     */
    public void insertUserPost(SysUser user)
    {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts))
        {
            // 鏂板鐢ㄦ埛涓庡矖浣嶇鐞?
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
            for (Long postId : posts)
            {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 鏂板鐢ㄦ埛瑙掕壊淇℃伅
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑙掕壊缁?
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotEmpty(roleIds))
        {
            // 鏂板鐢ㄦ埛涓庤鑹茬鐞?
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 閫氳繃鐢ㄦ埛ID鍒犻櫎鐢ㄦ埛
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId)
    {
        // 鍒犻櫎鐢ㄦ埛涓庤鑹插叧鑱?
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 鍒犻櫎鐢ㄦ埛涓庡矖浣嶈〃
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 鎵归噺鍒犻櫎鐢ㄦ埛淇℃伅
     * 
     * @param userIds 闇€瑕佸垹闄ょ殑鐢ㄦ埛ID
     * @return 缁撴灉
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds)
    {
        for (Long userId : userIds)
        {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 鍒犻櫎鐢ㄦ埛涓庤鑹插叧鑱?
        userRoleMapper.deleteUserRole(userIds);
        // 鍒犻櫎鐢ㄦ埛涓庡矖浣嶅叧鑱?
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 瀵煎叆鐢ㄦ埛鏁版嵁
     * 
     * @param userList 鐢ㄦ埛鏁版嵁鍒楄〃
     * @param isUpdateSupport 鏄惁鏇存柊鏀寔锛屽鏋滃凡瀛樺湪锛屽垯杩涜鏇存柊鏁版嵁
     * @param operName 鎿嶄綔鐢ㄦ埛
     * @return 缁撴灉
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            throw new ServiceException("瀵煎叆鐢ㄦ埛鏁版嵁涓嶈兘涓虹┖锛?);
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (SysUser user : userList)
        {
            try
            {
                // 楠岃瘉鏄惁瀛樺湪杩欎釜鐢ㄦ埛
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u))
                {
                    BeanValidators.validateWithException(validator, user);
                    deptService.checkDeptDataScope(user.getDeptId());
                    String password = configService.selectConfigByKey("sys.user.initPassword");
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    userMapper.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "銆佽处鍙?" + user.getUserName() + " 瀵煎叆鎴愬姛");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, user);
                    checkUserAllowed(u);
                    checkUserDataScope(u.getUserId());
                    deptService.checkDeptDataScope(user.getDeptId());
                    user.setUserId(u.getUserId());
                    user.setDeptId(u.getDeptId());
                    user.setUpdateBy(operName);
                    userMapper.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "銆佽处鍙?" + user.getUserName() + " 鏇存柊鎴愬姛");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "銆佽处鍙?" + user.getUserName() + " 宸插瓨鍦?);
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "銆佽处鍙?" + user.getUserName() + " 瀵煎叆澶辫触锛?;
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "寰堟姳姝夛紝瀵煎叆澶辫触锛佸叡 " + failureNum + " 鏉℃暟鎹牸寮忎笉姝ｇ‘锛岄敊璇涓嬶細");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "鎭枩鎮紝鏁版嵁宸插叏閮ㄥ鍏ユ垚鍔燂紒鍏?" + successNum + " 鏉★紝鏁版嵁濡備笅锛?);
        }
        return successMsg.toString();
    }
}
