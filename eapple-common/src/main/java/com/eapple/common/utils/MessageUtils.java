package com.eapple.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.eapple.common.utils.spring.SpringUtils;

/**
 * 国际化消息工具类。
 *
 * @author Eapp1e
 */
public class MessageUtils
{
    /**
     * 根据消息键和参数获取国际化消息，委托给 Spring MessageSource。
     *
     * @param code 消息键
     * @param args 参数
     * @return 国际化消息内容
     */
    public static String message(String code, Object... args)
    {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
