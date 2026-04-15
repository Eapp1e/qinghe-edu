package com.eapple.framework.interceptor;

import java.lang.reflect.Method;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.eapple.common.annotation.RepeatSubmit;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.utils.ServletUtils;

/**
 * 闃叉閲嶅鎻愪氦鎷︽埅鍣?
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
     * 楠岃瘉鏄惁閲嶅鎻愪氦鐢卞瓙绫诲疄鐜板叿浣撶殑闃查噸澶嶆彁浜ょ殑瑙勫垯
     *
     * @param request 璇锋眰淇℃伅
     * @param annotation 闃查噸澶嶆敞瑙ｅ弬鏁?
     * @return 缁撴灉
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
