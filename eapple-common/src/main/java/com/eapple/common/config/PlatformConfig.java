package com.eapple.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 鐠囪褰囨い鍦窗閻╃鍙ч柊宥囩枂
 * 
 * @author Eapp1e
 */
@Component
@ConfigurationProperties(prefix = "platform")
public class PlatformConfig
{
    /** 妞ゅ湱娲伴崥宥囆?*/
    private String name;

    /** 閻楀牊婀?*/
    private String version;

    /** 閻楀牊娼堥獮缈犲敜 */
    private String copyrightYear;

    /** 娑撳﹣绱剁捄顖氱窞 */
    private static String profile;

    /** 閼惧嘲褰囬崷鏉挎絻瀵偓閸?*/
    private static boolean addressEnabled;

    /** 妤犲矁鐦夐惍浣鸿閸?*/
    private static String captchaType;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCopyrightYear()
    {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear)
    {
        this.copyrightYear = copyrightYear;
    }

    public static String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        PlatformConfig.profile = profile;
    }

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        PlatformConfig.addressEnabled = addressEnabled;
    }

    public static String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        PlatformConfig.captchaType = captchaType;
    }

    /**
     * 閼惧嘲褰囩€电厧鍙嗘稉濠佺炊鐠侯垰绶?
     */
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }

    /**
     * 閼惧嘲褰囨径鏉戝剼娑撳﹣绱剁捄顖氱窞
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * 閼惧嘲褰囨稉瀣祰鐠侯垰绶?
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 閼惧嘲褰囨稉濠佺炊鐠侯垰绶?
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}


