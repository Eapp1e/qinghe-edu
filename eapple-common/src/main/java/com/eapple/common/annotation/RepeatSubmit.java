package com.eapple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鑷畾涔夋敞瑙ｉ槻姝㈣〃鍗曢噸澶嶆彁浜?
 * 
 * @author Eapp1e
 *
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit
{
    /**
     * 闂撮殧鏃堕棿(ms)锛屽皬浜庢鏃堕棿瑙嗕负閲嶅鎻愪氦
     */
    public int interval() default 5000;

    /**
     * 鎻愮ず娑堟伅
     */
    public String message() default "涓嶅厑璁搁噸澶嶆彁浜わ紝璇风◢鍊欏啀璇?;
}
