package com.eapple.common.constant;

/**
 * 浠诲姟璋冨害閫氱敤甯搁噺
 * 
 * @author Eapp1e
 */
public class ScheduleConstants
{
    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /** 鎵ц鐩爣key */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /** 榛樿 */
    public static final String MISFIRE_DEFAULT = "0";

    /** 绔嬪嵆瑙﹀彂鎵ц */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /** 瑙﹀彂涓€娆℃墽琛?*/
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /** 涓嶈Е鍙戠珛鍗虫墽琛?*/
    public static final String MISFIRE_DO_NOTHING = "3";

    public enum Status
    {
        /**
         * 姝ｅ父
         */
        NORMAL("0"),
        /**
         * 鏆傚仠
         */
        PAUSE("1");

        private String value;

        private Status(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }
}
