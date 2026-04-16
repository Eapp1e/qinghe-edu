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
 * 首页控制器
 *
 * @author Eapp1e
 */
@RestController
public class SysIndexController
{
    @Autowired
    private PlatformConfig platformConfig;

    @Autowired
    private ISysUserService userService;

    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问系统。", platformConfig.getName(), platformConfig.getVersion());
    }

    @PostMapping("/unlockscreen")
    public AjaxResult unlockScreen(@RequestBody Map<String, String> body)
    {
        String password = body.get("password");
        if (StringUtils.isEmpty(password))
        {
            return AjaxResult.error("解锁密码不能为空");
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return AjaxResult.error("当前用户不存在，请重新登录");
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            return AjaxResult.error("解锁密码错误");
        }

        return AjaxResult.success("解锁成功");
    }
}