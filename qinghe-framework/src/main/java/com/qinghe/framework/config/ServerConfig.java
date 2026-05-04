package com.qinghe.framework.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.qinghe.common.utils.ServletUtils;

/**
 * 服务地址配置。
 *
 * @author Eapp1e
 */
@Component
public class ServerConfig
{
    /**
     * 获取完整的请求地址，包含域名、端口与上下文路径。
     *
     * @return 服务访问地址
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
