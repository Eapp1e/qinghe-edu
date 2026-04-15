package com.eapple.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.service.ISysUserService;

/**
 * 棣栭〉
 *
 * @author Eapp1e
 */
@RestController
public class SysIndexController
{
    /** 绯荤粺鍩虹閰嶇疆 */
    @Autowired
    private PlatformConfig ruoyiConfig;

    @Autowired
    private ISysUserService userService;

    /**
     * 璁块棶棣栭〉锛屾彁绀鸿
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("娆㈣繋浣跨敤{}鍚庡彴绠＄悊妗嗘灦锛屽綋鍓嶇増鏈細v{}锛岃閫氳繃鍓嶇鍦板潃璁块棶銆?, ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }

    /**
     * 瑙ｉ攣灞忓箷
     */
    @PostMapping("/unlockscreen")
    public AjaxResult unlockScreen(@RequestBody Map<String, String> body)
    {
        String password = body.get("password");
        if (StringUtils.isEmpty(password))
        {
            return AjaxResult.error("瀵嗙爜涓嶈兘涓虹┖");
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return AjaxResult.error("鏈嶅姟鍣ㄨ秴鏃讹紝璇烽噸鏂扮櫥褰?);
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            return AjaxResult.error("瀵嗙爜閿欒锛岃閲嶆柊杈撳叆");
        }

        return AjaxResult.success("瑙ｉ攣鎴愬姛");
    }
}

