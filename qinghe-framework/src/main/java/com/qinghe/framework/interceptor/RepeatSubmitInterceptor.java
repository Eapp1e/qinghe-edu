package com.qinghe.framework.interceptor;

import java.lang.reflect.Method;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.qinghe.common.annotation.RepeatSubmit;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.utils.ServletUtils;

/**
 * 防止重复提交拦截器抽象类。
 *
 * @author Eapp1e
 */
@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null)
            {
                if (this.isRepeatSubmit(request, annotation))
                {
                    AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                    ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                    return false;
                }
            }
            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * 判断当前请求是否属于重复提交。
     *
     * @param request 请求信息
     * @param annotation 重复提交注解
     * @return true 表示重复提交
     * @throws Exception 异常信息
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) throws Exception;
}