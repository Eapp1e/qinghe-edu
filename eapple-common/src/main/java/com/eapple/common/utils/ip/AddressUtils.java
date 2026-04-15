package com.eapple.common.utils.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.constant.Constants;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.http.HttpUtils;

/**
 * 鑾峰彇鍦板潃绫?
 * 
 * @author Eapp1e
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP鍦板潃鏌ヨ
    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";

    // 鏈煡鍦板潃
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip)
    {
        // 鍐呯綉涓嶆煡璇?
        if (IpUtils.internalIp(ip))
        {
            return "鍐呯綉IP";
        }
        if (PlatformConfig.isAddressEnabled())
        {
            try
            {
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr))
                {
                    log.error("鑾峰彇鍦扮悊浣嶇疆寮傚父 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSON.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
                return String.format("%s %s", region, city);
            }
            catch (Exception e)
            {
                log.error("鑾峰彇鍦扮悊浣嶇疆寮傚父 {}", ip);
            }
        }
        return UNKNOWN;
    }
}

