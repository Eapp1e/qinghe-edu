package com.eapple.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.eapple.common.annotation.Log;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.file.FileUploadUtils;
import com.eapple.common.utils.file.FileUtils;
import com.eapple.common.utils.file.MimeTypeUtils;
import com.eapple.framework.web.service.TokenService;
import com.eapple.system.service.ISysUserService;

/**
 * 涓汉淇℃伅 涓氬姟澶勭悊
 * 
 * @author Eapp1e
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 涓汉淇℃伅
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 淇敼鐢ㄦ埛
     */
    @Log(title = "涓汉淇℃伅", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("淇敼鐢ㄦ埛'" + loginUser.getUsername() + "'澶辫触锛屾墜鏈哄彿鐮佸凡瀛樺湪");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("淇敼鐢ㄦ埛'" + loginUser.getUsername() + "'澶辫触锛岄偖绠辫处鍙峰凡瀛樺湪");
        }
        if (userService.updateUserProfile(currentUser) > 0)
        {
            // 鏇存柊缂撳瓨鐢ㄦ埛淇℃伅
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("淇敼涓汉淇℃伅寮傚父锛岃鑱旂郴绠＄悊鍛?);
    }

    /**
     * 閲嶇疆瀵嗙爜
     */
    @Log(title = "涓汉淇℃伅", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestBody Map<String, String> params)
    {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        SysUser user = userService.selectUserById(userId);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("淇敼瀵嗙爜澶辫触锛屾棫瀵嗙爜閿欒");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("鏂板瘑鐮佷笉鑳戒笌鏃у瘑鐮佺浉鍚?);
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userId, newPassword) > 0)
        {
            // 鏇存柊缂撳瓨鐢ㄦ埛瀵嗙爜&瀵嗙爜鏈€鍚庢洿鏂版椂闂?
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("淇敼瀵嗙爜寮傚父锛岃鑱旂郴绠＄悊鍛?);
    }

    /**
     * 澶村儚涓婁紶
     */
    @Log(title = "鐢ㄦ埛澶村儚", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(PlatformConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            if (userService.updateUserAvatar(loginUser.getUserId(), avatar))
            {
                String oldAvatar = loginUser.getUser().getAvatar();
                if (StringUtils.isNotEmpty(oldAvatar))
                {
                    FileUtils.deleteFile(PlatformConfig.getProfile() + FileUtils.stripPrefix(oldAvatar));
                }
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                // 鏇存柊缂撳瓨鐢ㄦ埛澶村儚
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return error("涓婁紶鍥剧墖寮傚父锛岃鑱旂郴绠＄悊鍛?);
    }
}

