package com.eapple.framework.security.handle;

import java.io.IOException;
import java.io.Serializable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.eapple.common.constant.HttpStatus;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.utils.ServletUtils;
import com.eapple.common.utils.StringUtils;

/**
 * 璁よ瘉澶辫触澶勭悊绫?杩斿洖鏈巿鏉?
 * 
 * @author Eapp1e
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable
{
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException
    {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("璇锋眰璁块棶锛歿}锛岃璇佸け璐ワ紝鏃犳硶璁块棶绯荤粺璧勬簮", request.getRequestURI());
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
    }
}
