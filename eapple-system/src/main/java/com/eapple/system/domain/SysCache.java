package com.eapple.system.domain;

import com.eapple.common.utils.StringUtils;

/**
 * 缂撳瓨淇℃伅
 * 
 * @author Eapp1e
 */
public class SysCache
{
    /** 缂撳瓨鍚嶇О */
    private String cacheName = "";

    /** 缂撳瓨閿悕 */
    private String cacheKey = "";

    /** 缂撳瓨鍐呭 */
    private String cacheValue = "";

    /** 澶囨敞 */
    private String remark = "";

    public SysCache()
    {

    }

    public SysCache(String cacheName, String remark)
    {
        this.cacheName = cacheName;
        this.remark = remark;
    }

    public SysCache(String cacheName, String cacheKey, String cacheValue)
    {
        this.cacheName = StringUtils.replace(cacheName, ":", "");
        this.cacheKey = StringUtils.replace(cacheKey, cacheName, "");
        this.cacheValue = cacheValue;
    }

    public String getCacheName()
    {
        return cacheName;
    }

    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }

    public String getCacheKey()
    {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey)
    {
        this.cacheKey = cacheKey;
    }

    public String getCacheValue()
    {
        return cacheValue;
    }

    public void setCacheValue(String cacheValue)
    {
        this.cacheValue = cacheValue;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
