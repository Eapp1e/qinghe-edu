package com.eapple.system.domain.vo;

import com.eapple.common.utils.StringUtils;

/**
 * 璺敱鏄剧ず淇℃伅
 * 
 * @author Eapp1e
 */
public class MetaVo
{
    /**
     * 璁剧疆璇ヨ矾鐢卞湪渚ц竟鏍忓拰闈㈠寘灞戜腑灞曠ず鐨勫悕瀛?
     */
    private String title;

    /**
     * 璁剧疆璇ヨ矾鐢辩殑鍥炬爣锛屽搴旇矾寰剆rc/assets/icons/svg
     */
    private String icon;

    /**
     * 璁剧疆涓簍rue锛屽垯涓嶄細琚?<keep-alive>缂撳瓨
     */
    private boolean noCache;

    /**
     * 鍐呴摼鍦板潃锛坔ttp(s)://寮€澶达級
     */
    private String link;

    public MetaVo()
    {
    }

    public MetaVo(String title, String icon)
    {
        this.title = title;
        this.icon = icon;
    }

    public MetaVo(String title, String icon, boolean noCache)
    {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }

    public MetaVo(String title, String icon, String link)
    {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }

    public MetaVo(String title, String icon, boolean noCache, String link)
    {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
        if (StringUtils.ishttp(link))
        {
            this.link = link;
        }
    }

    public boolean isNoCache()
    {
        return noCache;
    }

    public void setNoCache(boolean noCache)
    {
        this.noCache = noCache;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }
}
