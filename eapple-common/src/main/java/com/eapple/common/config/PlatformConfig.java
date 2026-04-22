package com.eapple.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 平台基础配置。
 *
 * @author Eapp1e
 */
@Component
@ConfigurationProperties(prefix = "platform")
public class PlatformConfig
{
    /** 平台名称 */
    private String name;

    /** 平台版本 */
    private String version;

    /** 版权年份 */
    private String copyrightYear;

    /** 文件上传根目录 */
    private static String profile;

    /** 是否开启地址解析 */
    private static boolean addressEnabled;

    /** 验证码类型 */
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

    public static String getCaptchaType()
    {
        return captchaType;
    }

    public void setCaptchaType(String captchaType)
    {
        PlatformConfig.captchaType = captchaType;
    }

    /**
     * 获取导入文件目录。
     */
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }

    /**
     * 获取头像上传目录。
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载目录。
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传目录。
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}
