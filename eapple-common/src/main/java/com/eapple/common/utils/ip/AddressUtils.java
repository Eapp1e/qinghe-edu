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
 * IP 地址解析工具类。
 *
 * @author Eapp1e
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    /** IP 地址查询接口 */
    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";

    /** 未知地址 */
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip)
    {
        if (IpUtils.internalIp(ip))
        {
            return "内网 IP";
        }
        if (PlatformConfig.isAddressEnabled())
        {
            try
            {
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr))
                {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSON.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
                return String.format("%s %s", region, city);
            }
            catch (Exception e)
            {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN;
    }
}
