package com.eapple.framework.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.eapple.common.utils.ServletUtils;

/**
 * 鏈嶅姟鐩稿叧閰嶇疆
 * 
 * @author Eapp1e
 */
@Component
public class ServerConfig
{
    /**
     * 鑾峰彇瀹屾暣鐨勮姹傝矾寰勶紝鍖呮嫭锛氬煙鍚嶏紝绔彛锛屼笂涓嬫枃璁块棶璺緞
     * 
     * @return 鏈嶅姟鍦板潃
     */
    public String getUrl()
    {
        HttpServletRequest request = ServletUtils.getRequest();
        return getDomain(request);
    }

    public static String getDomain(HttpServletRequest request)
    {
        StringBuffer url = request.getRequestURL();
        String contextPath = request.getServletContext().getContextPath();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
    }
}
