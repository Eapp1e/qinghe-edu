package com.eapple.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 璇诲彇椤圭洰鐩稿叧閰嶇疆
 * 
 * @author Eapp1e
 */
@Component
@ConfigurationProperties(prefix = "ruoyi")
public class PlatformConfig
{
    /** 椤圭洰鍚嶇О */
    private String name;

    /** 鐗堟湰 */
    private String version;

    /** 鐗堟潈骞翠唤 */
    private String copyrightYear;

    /** 涓婁紶璺緞 */
    private static String profile;

    /** 鑾峰彇鍦板潃寮€鍏?*/
    private static boolean addressEnabled;

    /** 楠岃瘉鐮佺被鍨?*/
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
     * 鑾峰彇瀵煎叆涓婁紶璺緞
     */
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }

    /**
     * 鑾峰彇澶村儚涓婁紶璺緞
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * 鑾峰彇涓嬭浇璺緞
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 鑾峰彇涓婁紶璺緞
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}

