package com.eapple.framework.interceptor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.eapple.common.annotation.RepeatSubmit;
import com.eapple.common.constant.CacheConstants;
import com.eapple.common.core.redis.RedisCache;
import com.eapple.common.filter.RepeatedlyRequestWrapper;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.http.HttpHelper;
import com.eapple.framework.interceptor.RepeatSubmitInterceptor;

/**
 * 鍒ゆ柇璇锋眰url鍜屾暟鎹槸鍚﹀拰涓婁竴娆＄浉鍚岋紝
 * 濡傛灉鍜屼笂娆＄浉鍚岋紝鍒欐槸閲嶅鎻愪氦琛ㄥ崟銆?鏈夋晥鏃堕棿涓?0绉掑唴銆?
 * 
 * @author Eapp1e
 */
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor
{
    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    // 浠ょ墝鑷畾涔夋爣璇?
    @Value("${token.header}")
    private String header;

    @Autowired
    private RedisCache redisCache;

    @SuppressWarnings("unchecked")
    @Override
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation)
    {
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper)
        {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
            nowParams = HttpHelper.getBodyString(repeatedlyRequest);
        }

        // body鍙傛暟涓虹┖锛岃幏鍙朠arameter鐨勬暟鎹?
        if (StringUtils.isEmpty(nowParams))
        {
            nowParams = JSON.toJSONString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<String, Object>();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 璇锋眰鍦板潃锛堜綔涓哄瓨鏀綾ache鐨刱ey鍊硷級
        String url = request.getRequestURI();

        // 鍞竴鍊硷紙娌℃湁娑堟伅澶村垯浣跨敤璇锋眰鍦板潃锛?
        String submitKey = StringUtils.trimToEmpty(request.getHeader(header));

        // 鍞竴鏍囪瘑锛堟寚瀹歬ey + url + 娑堟伅澶达級
        String cacheRepeatKey = CacheConstants.REPEAT_SUBMIT_KEY + url + submitKey;

        Object sessionObj = redisCache.getCacheObject(cacheRepeatKey);
        if (sessionObj != null)
        {
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if (sessionMap.containsKey(url))
            {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval()))
                {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<String, Object>();
        cacheMap.put(url, nowDataMap);
        redisCache.setCacheObject(cacheRepeatKey, cacheMap, annotation.interval(), TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * 鍒ゆ柇鍙傛暟鏄惁鐩稿悓
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap)
    {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 鍒ゆ柇涓ゆ闂撮殧鏃堕棿
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval)
    {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        if ((time1 - time2) < interval)
        {
            return true;
        }
        return false;
    }
}
