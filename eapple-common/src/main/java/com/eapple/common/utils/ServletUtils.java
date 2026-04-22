package com.eapple.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.eapple.common.constant.Constants;
import com.eapple.common.core.text.Convert;

/**
 * Servlet 工具类。
 *
 * @author Eapp1e
 */
public class ServletUtils
{
    /**
     * 获取 String 类型参数。
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取 String 类型参数。
     */
    public static String getParameter(String name, String defaultValue)
    {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取 Integer 类型参数。
     */
    public static Integer getParameterToInt(String name)
    {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取 Integer 类型参数。
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取 Boolean 类型参数。
     */
    public static Boolean getParameterToBool(String name)
    {
        return Convert.toBool(getRequest().getParameter(name));
    }

    /**
     * 获取 Boolean 类型参数。
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue)
    {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取请求参数映射。
     *
     * @param request 请求对象 {@link ServletRequest}
     * @return 参数映射
     */
    public static Map<String, String[]> getParams(ServletRequest request)
    {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

    /**
     * 获取请求参数键值映射。
     *
     * @param request 请求对象 {@link ServletRequest}
     * @return 参数映射
     */
    public static Map<String, String> getParamMap(ServletRequest request)
    {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet())
        {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
        }
        return params;
    }

    /**
     * 获取当前请求对象。
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取当前响应对象。
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取当前会话对象。
     */
    public static HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端。
     *
     * @param response 响应对象
     * @param string 需要输出的字符串
     */
    public static void renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 是否为 Ajax 异步请求。
     *
     * @param request 请求对象
     * @return true 表示是 Ajax 请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json"))
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest"))
        {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.inStringIgnoreCase(uri, ".json", ".xml"))
        {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        return StringUtils.inStringIgnoreCase(ajax, "json", "xml");
    }

    /**
     * URL 编码。
     *
     * @param str 原始字符串
     * @return 编码后的字符串
     */
    public static String urlEncode(String str)
    {
        try
        {
            return URLEncoder.encode(str, Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * URL 解码。
     *
     * @param str 编码字符串
     * @return 解码后的字符串
     */
    public static String urlDecode(String str)
    {
        try
        {
            return URLDecoder.decode(str, Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            return StringUtils.EMPTY;
        }
    }
}
