package com.eapple.common.constant;

/**
 * 定时任务通用常量。
 *
 * @author Eapp1e
 */
public class ScheduleConstants
{
    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /** 定时任务属性 key。 */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /** 默认策略。 */
    public static final String MISFIRE_DEFAULT = "0";

    /** 忽略错过执行策略。 */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /** 立即执行一次策略。 */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /** 不触发立即执行。 */
    public static final String MISFIRE_DO_NOTHING = "3";

    public enum Status
    {
        /**
         * 正常。
         */
        NORMAL("0"),
        /**
         * 暂停。
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
