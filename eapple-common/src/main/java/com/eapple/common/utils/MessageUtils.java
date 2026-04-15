package com.eapple.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.eapple.common.utils.spring.SpringUtils;

/**
 * 鑾峰彇i18n璧勬簮鏂囦欢
 * 
 * @author Eapp1e
 */
public class MessageUtils
{
    /**
     * 鏍规嵁娑堟伅閿拰鍙傛暟 鑾峰彇娑堟伅 濮旀墭缁檚pring messageSource
     *
     * @param code 娑堟伅閿?
     * @param args 鍙傛暟
     * @return 鑾峰彇鍥介檯鍖栫炕璇戝€?
     */
    public static String message(String code, Object... args)
    {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
