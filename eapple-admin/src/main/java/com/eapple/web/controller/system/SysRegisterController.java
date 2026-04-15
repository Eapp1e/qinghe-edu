package com.eapple.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.model.RegisterBody;
import com.eapple.common.utils.StringUtils;
import com.eapple.framework.web.service.SysRegisterService;
import com.eapple.system.service.ISysConfigService;

/**
 * 娉ㄥ唽楠岃瘉
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
            return error("褰撳墠绯荤粺娌℃湁寮€鍚敞鍐屽姛鑳斤紒");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
