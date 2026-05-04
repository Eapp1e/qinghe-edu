package com.qinghe.web.controller.system;

import com.qinghe.common.core.controller.BaseController;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.core.domain.model.RegisterBody;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.framework.web.service.SysRegisterService;
import com.qinghe.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 *
 * @author Eapp1e
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前平台暂未开放注册，请联系管理员创建账号");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success("注册成功，请返回登录") : error(msg);
    }
}
