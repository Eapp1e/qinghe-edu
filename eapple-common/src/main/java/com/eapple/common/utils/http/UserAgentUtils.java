package com.eapple.common.utils.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.eapple.common.utils.StringUtils;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * UserAgent瑙ｆ瀽宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class UserAgentUtils
{
    public static final String UNKNOWN = "";

    // 娴忚鍣ㄦ鍒欒〃杈惧紡妯″紡
    private static final Pattern CHROME_PATTERN = Pattern.compile("Chrome/(\\d+)(?:\\.\\d+)*");
    private static final Pattern FIREFOX_PATTERN = Pattern.compile("Firefox/(\\d+)(?:\\.\\d+)*");
    private static final Pattern EDGE_PATTERN = Pattern.compile("Edg(?:e)?/(\\d+)(?:\\.\\d+)*");
    private static final Pattern SAFARI_PATTERN = Pattern.compile("Version/(\\d+)(?:\\.\\d+)*");
    private static final Pattern OPERA_PATTERN = Pattern.compile("Opera/(\\d+)(?:\\.\\d+)*");
    private static final Pattern IE_PATTERN = Pattern.compile("(?:MSIE |Trident/.*rv:)(\\d+)(?:\\.\\d+)*");
    private static final Pattern SAMSUNG_PATTERN = Pattern.compile("SamsungBrowser/(\\d+)(?:\\.\\d+)*");
    private static final Pattern UC_PATTERN = Pattern.compile("UCBrowser/(\\d+)(?:\\.\\d+)*");
    private static final Pattern QQ_PATTERN = Pattern.compile("QQBrowser/(\\d+)(?:\\.\\d+)*");
    private static final Pattern WECHAT_PATTERN = Pattern.compile("MicroMessenger/(\\d+)(?:\\.\\d+)*");
    private static final Pattern BAIDU_PATTERN = Pattern.compile("baidubrowser/(\\d+)(?:\\.\\d+)*");

    // 鎿嶄綔绯荤粺姝ｅ垯琛ㄨ揪寮忔ā寮?
    private static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows NT (\\d+\\.\\d+)");
    private static final Pattern MACOS_PATTERN = Pattern.compile("Mac OS X (\\d+[_\\d]*)");
    private static final Pattern ANDROID_PATTERN = Pattern.compile("Android (\\d+)(?:\\.\\d+)*");
    private static final Pattern IOS_PATTERN = Pattern.compile("OS[\\s_](\\d+)(?:_\\d+)*");
    private static final Pattern LINUX_PATTERN = Pattern.compile("Linux");
    private static final Pattern CHROMEOS_PATTERN = Pattern.compile("CrOS");

    private static final UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder().hideMatcherLoadStats()
            .withCache(5000)
            .showMinimalVersion()
            .withField(UserAgent.AGENT_NAME_VERSION)
            .withField(UserAgent.OPERATING_SYSTEM_NAME_VERSION)
            .build();

    /**
     * 鑾峰彇瀹㈡埛绔祻瑙堝櫒
     */
    public static String getBrowser(String userAgent)
    {
        UserAgent.ImmutableUserAgent iua = userAgentAnalyzer.parse(userAgent);
        String agentNameVersion = iua.get(UserAgent.AGENT_NAME_VERSION).getValue();
        if (StringUtils.isBlank(agentNameVersion) || agentNameVersion.contains("??"))
        {
            return formatBrowser(userAgent);
        }
        return agentNameVersion;
    }

    /**
     * 鑾峰彇瀹㈡埛绔搷浣滅郴缁?
     */
    public static String getOperatingSystem(String userAgent)
    {
        UserAgent.ImmutableUserAgent iua = userAgentAnalyzer.parse(userAgent);
        String operatingSystemNameVersion = iua.get(UserAgent.OPERATING_SYSTEM_NAME_VERSION).getValue();
        if (StringUtils.isBlank(operatingSystemNameVersion) || operatingSystemNameVersion.contains("??"))
        {
            return formatOperatingSystem(userAgent);
        }
        return operatingSystemNameVersion;
    }

    /**
     * 鍏ㄩ潰娴忚鍣ㄦ娴?
     */
    private static String formatBrowser(String browser)
    {
        // Chrome绯诲垪娴忚鍣?
        Matcher chromeMatcher = CHROME_PATTERN.matcher(browser);
        if (chromeMatcher.find() && (browser.contains("Chrome") || browser.contains("CriOS")))
        {
            return "Chrome" + chromeMatcher.group(1);
        }
        // Firefox
        Matcher firefoxMatcher = FIREFOX_PATTERN.matcher(browser);
        if (firefoxMatcher.find())
        {
            return "Firefox" + firefoxMatcher.group(1);
        }
        // Edge娴忚鍣?
        Matcher edgeMatcher = EDGE_PATTERN.matcher(browser);
        if (edgeMatcher.find())
        {
            return "Edge" + edgeMatcher.group(1);
        }
        // Safari娴忚鍣紙闇€鎺掗櫎Chrome锛?
        Matcher safariMatcher = SAFARI_PATTERN.matcher(browser);
        if (safariMatcher.find() && !browser.contains("Chrome"))
        {
            return "Safari" + safariMatcher.group(1);
        }
        // 寰俊鍐呯疆娴忚鍣?
        Matcher wechatMatcher = WECHAT_PATTERN.matcher(browser);
        if (wechatMatcher.find())
        {
            return "WeChat" + wechatMatcher.group(1);
        }
        // UC娴忚鍣?
        Matcher ucMatcher = UC_PATTERN.matcher(browser);
        if (ucMatcher.find())
        {
            return "UC Browser" + ucMatcher.group(1);
        }
        // QQ娴忚鍣?
        Matcher qqMatcher = QQ_PATTERN.matcher(browser);
        if (qqMatcher.find())
        {
            return "QQ Browser" + qqMatcher.group(1);
        }
        // 鐧惧害娴忚鍣?
        Matcher baiduMatcher = BAIDU_PATTERN.matcher(browser);
        if (baiduMatcher.find())
        {
            return "Baidu Browser" + baiduMatcher.group(1);
        }
        // Samsung娴忚鍣?
        Matcher samsungMatcher = SAMSUNG_PATTERN.matcher(browser);
        if (samsungMatcher.find())
        {
            return "Samsung Browser" + samsungMatcher.group(1);
        }
        // Opera娴忚鍣?
        Matcher operaMatcher = OPERA_PATTERN.matcher(browser);
        if (operaMatcher.find())
        {
            return "Opera" + operaMatcher.group(1);
        }
        // IE娴忚鍣?
        Matcher ieMatcher = IE_PATTERN.matcher(browser);
        if (ieMatcher.find())
        {
            return "Internet Explorer" + ieMatcher.group(1);
        }
        return UNKNOWN;
    }

    /**
     * 妫€娴嬫搷浣滅郴缁?
     */
    private static String formatOperatingSystem(String operatingSystem)
    {
        // Windows绯荤粺
        Matcher windowsMatcher = WINDOWS_PATTERN.matcher(operatingSystem);
        if (windowsMatcher.find())
        {
            return "Windows" + getWindowsVersionDisplay(windowsMatcher.group(1));
        }
        // macOS绯荤粺
        Matcher macMatcher = MACOS_PATTERN.matcher(operatingSystem);
        if (macMatcher.find())
        {
            String version = macMatcher.group(1).replace("_", ".");
            return "macOS" + extractMajorVersion(version);
        }
        // Android绯荤粺
        Matcher androidMatcher = ANDROID_PATTERN.matcher(operatingSystem);
        if (androidMatcher.find())
        {
            return "Android" + extractMajorVersion(androidMatcher.group(1));
        }
        // iOS绯荤粺
        Matcher iosMatcher = IOS_PATTERN.matcher(operatingSystem);
        if (iosMatcher.find() && (operatingSystem.contains("iPhone") || operatingSystem.contains("iPad")))
        {
            return "iOS" + extractMajorVersion(iosMatcher.group(1));
        }
        // Linux绯荤粺
        if (LINUX_PATTERN.matcher(operatingSystem).find() && !operatingSystem.contains("Android"))
        {
            return "Linux";
        }
        // Chrome OS
        if (CHROMEOS_PATTERN.matcher(operatingSystem).find())
        {
            return "Chrome OS";
        }
        return UNKNOWN;
    }

    /**
     * 鎻愬彇浼樺寲鐨勪富鐗堟湰鍙?
     */
    private static String extractMajorVersion(String fullVersion)
    {
        if (StringUtils.isEmpty(fullVersion))
        {
            return StringUtils.EMPTY;
        }
        try
        {
            // 娓呯悊鐗堟湰鍙蜂腑鐨勯潪鏁板瓧瀛楃
            String cleanVersion = fullVersion.replaceAll("[^0-9.]", "");
            String[] parts = cleanVersion.split("\\.");
            if (parts.length > 0)
            {
                String firstPart = parts[0];
                if (firstPart.matches("\\d+"))
                {
                    int version = Integer.parseInt(firstPart);

                    // 澶勭悊涓変綅鏁扮増鏈彿锛堝142 -> 14锛?
                    if (version >= 100)
                    {
                        return String.valueOf(version / 10);
                    }
                    return firstPart;
                }
            }
        }
        catch (NumberFormatException e)
        {
            // 鐗堟湰鍙疯В鏋愬け璐ワ紝杩斿洖鍘熷鍊?
        }
        return fullVersion;
    }

    /**
     * Windows鐗堟湰鍙锋樉绀轰紭鍖?
     */
    private static String getWindowsVersionDisplay(String version)
    {
        switch (version)
        {
            case "10.0":
                return "10";
            case "6.3":
                return "8.1";
            case "6.2":
                return "8";
            case "6.1":
                return "7";
            case "6.0":
                return "Vista";
            case "5.1":
                return "XP";
            case "5.0":
                return "2000";
            default:
                return extractMajorVersion(version);
        }
    }
}
