package com.qinghe.common.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.common.utils.html.EscapeUtil;

/**
 * XSS 过滤包装请求
 * 
 * @author Eapp1e
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    /**
     * 构造包装请求
     * 
     * @param request 原始请求
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request)
    {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name)
    {
        String[] values = super.getParameterValues(name);
        if (values != null)
        {
            int length = values.length;
            String[] escapesValues = new String[length];
            for (int i = 0; i < length; i++)
            {
                // 清理参数中的 XSS 内容
                escapesValues[i] = EscapeUtil.clean(values[i]).trim();
            }
            return escapesValues;
        }
        return super.getParameterValues(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        // 非 JSON 类型请求，直接返回原始输入流
        if (!isJsonRequest())
        {
            return super.getInputStream();
        }

        // 请求体为空时，直接返回原始输入流
        String json = IOUtils.toString(super.getInputStream(), "utf-8");
        if (StringUtils.isEmpty(json))
        {
            return super.getInputStream();
        }

        // 清理 JSON 内容中的 XSS 代码
        json = EscapeUtil.clean(json).trim();
        byte[] jsonBytes = json.getBytes("utf-8");
        final ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
        return new ServletInputStream()
        {
            @Override
            public boolean isFinished()
            {
                return true;
            }

            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public int available() throws IOException
            {
                return jsonBytes.length;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {
            }

            @Override
            public int read() throws IOException
            {
                return bis.read();
            }
        };
    }

    /**
     * 是否为 JSON 请求
     * 
     * @return 是否为 JSON 请求
     */
    public boolean isJsonRequest()
    {
        String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE);
    }
}
