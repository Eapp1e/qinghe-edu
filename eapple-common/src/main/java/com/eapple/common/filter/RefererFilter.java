package com.eapple.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 闃茬洍閾捐繃婊ゅ櫒
 * 
 * @author Eapp1e
 */
public class RefererFilter implements Filter
{
    /**
     * 鍏佽鐨勫煙鍚嶅垪琛?
     */
    public List<String> allowedDomains;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        String domains = filterConfig.getInitParameter("allowedDomains");
        this.allowedDomains = Arrays.asList(domains.split(","));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String referer = req.getHeader("Referer");

        // 濡傛灉Referer涓虹┖锛屾嫆缁濊闂?
        if (referer == null || referer.isEmpty())
        {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Referer header is required");
            return;
        }

        // 妫€鏌eferer鏄惁鍦ㄥ厑璁哥殑鍩熷悕鍒楄〃涓?
        boolean allowed = false;
        for (String domain : allowedDomains)
        {
            if (referer.contains(domain))
            {
                allowed = true;
                break;
            }
        }

        // 鏍规嵁妫€鏌ョ粨鏋滃喅瀹氭槸鍚︽斁琛?
        if (allowed)
        {
            chain.doFilter(request, response);
        }
        else
        {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Referer '" + referer + "' is not allowed");
        }
    }

    @Override
    public void destroy()
    {

    }
}