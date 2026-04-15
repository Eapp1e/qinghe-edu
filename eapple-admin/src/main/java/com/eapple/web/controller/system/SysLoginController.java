package com.eapple.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.constant.Constants;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysMenu;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginBody;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.core.text.Convert;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.framework.web.service.SysLoginService;
import com.eapple.framework.web.service.SysPermissionService;
import com.eapple.framework.web.service.TokenService;
import com.eapple.system.service.ISysConfigService;
import com.eapple.system.service.ISysMenuService;

/**
 * 鐧诲綍楠岃瘉
 * 
 * @author Eapp1e
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 鐧诲綍鏂规硶
     * 
     * @param loginBody 鐧诲綍淇℃伅
     * @return 缁撴灉
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 鐢熸垚浠ょ墝
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), loginBody.getLoginRole());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 鑾峰彇鐢ㄦ埛淇℃伅
     * 
     * @return 鐢ㄦ埛淇℃伅
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 瑙掕壊闆嗗悎
        Set<String> roles = permissionService.getRolePermission(user);
        // 鏉冮檺闆嗗悎
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 鑾峰彇璺敱淇℃伅
     * 
     * @return 璺敱淇℃伅
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
    
    // 妫€鏌ュ垵濮嬪瘑鐮佹槸鍚︽彁閱掍慨鏀?
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 妫€鏌ュ瘑鐮佹槸鍚﹁繃鏈?
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 濡傛灉浠庢湭淇敼杩囧垵濮嬪瘑鐮侊紝鐩存帴鎻愰啋杩囨湡
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
