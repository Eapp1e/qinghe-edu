package com.eapple.common.utils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 鏃堕棿宸ュ叿绫?
 * 
 * @author Eapp1e
 */
@SuppressWarnings("deprecation")
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 鑾峰彇褰撳墠Date鍨嬫棩鏈?
     * 
     * @return Date() 褰撳墠鏃ユ湡
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 鑾峰彇褰撳墠鏃ユ湡, 榛樿鏍煎紡涓簓yyy-MM-dd
     * 
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 鏃ユ湡璺緞 鍗冲勾/鏈?鏃?濡?018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 鏃ユ湡璺緞 鍗冲勾/鏈?鏃?濡?0180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 鏃ユ湡鍨嬪瓧绗︿覆杞寲涓烘棩鏈?鏍煎紡
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 鑾峰彇鏈嶅姟鍣ㄥ惎鍔ㄦ椂闂?
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 璁＄畻鐩稿樊澶╂暟
     */
    public static int differentDaysByMillisecond(Date date1, Date date2)
    {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 璁＄畻鏃堕棿宸?
     *
     * @param endDate 鏈€鍚庢椂闂?
     * @param startTime 寮€濮嬫椂闂?
     * @return 鏃堕棿宸紙澶?灏忔椂/鍒嗛挓锛?
     */
    public static String timeDistance(Date endDate, Date startTime)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 鑾峰緱涓や釜鏃堕棿鐨勬绉掓椂闂村樊寮?
        long diff = endDate.getTime() - startTime.getTime();
        // 璁＄畻宸灏戝ぉ
        long day = diff / nd;
        // 璁＄畻宸灏戝皬鏃?
        long hour = diff % nd / nh;
        // 璁＄畻宸灏戝垎閽?
        long min = diff % nd % nh / nm;
        // 璁＄畻宸灏戠//杈撳嚭缁撴灉
        // long sec = diff % nd % nh % nm / ns;
        return day + "澶? + hour + "灏忔椂" + min + "鍒嗛挓";
    }

    /**
     * 澧炲姞 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 澧炲姞 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }
}
