package com.eapple.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Platform base configuration.
 *
 * @author EAPPLE
 */
@Component
@ConfigurationProperties(prefix = "platform")
public class PlatformConfig
{
    /** Platform name. */
    private String name;

    /** Platform version. */
    private String version;

    /** Copyright owner. */
    private String owner;

    /** Copyright year. */
    private String copyrightYear;

    /** GitHub identity. */
    private String github;

    /** Upload root path. */
    private static String profile;

    /** Whether address lookup is enabled. */
    private static boolean addressEnabled;

    /** Captcha type. */
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

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getCopyrightYear()
    {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear)
    {
        this.copyrightYear = copyrightYear;
    }

    public String getGithub()
    {
        return github;
    }

    public void setGithub(String github)
    {
        this.github = github;
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
     * Gets import file directory.
     */
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }

    /**
     * Gets avatar upload directory.
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * Gets download directory.
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * Gets upload directory.
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}
