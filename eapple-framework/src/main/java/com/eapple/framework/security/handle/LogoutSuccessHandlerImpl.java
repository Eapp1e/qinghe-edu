package com.eapple.framework.security.handle;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import com.eapple.common.constant.Constants;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.utils.MessageUtils;
import com.eapple.common.utils.ServletUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.framework.manager.AsyncManager;
import com.eapple.framework.manager.factory.AsyncFactory;
import com.eapple.framework.web.service.TokenService;

/**
 * 鑷畾涔夐€€鍑哄鐞嗙被 杩斿洖鎴愬姛
 * 
 * @author Eapp1e
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{
    @Autowired
    private TokenService tokenService;

    /**
     * 閫€鍑哄鐞?
     * 
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            String userName = loginUser.getUsername();
            // 鍒犻櫎鐢ㄦ埛缂撳瓨璁板綍
            tokenService.delLoginUser(loginUser.getToken());
            // 璁板綍鐢ㄦ埛閫€鍑烘棩蹇?
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, MessageUtils.message("user.logout.success")));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success(MessageUtils.message("user.logout.success"))));
    }
}
