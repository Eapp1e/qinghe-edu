package com.eapple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.eapple.common.enums.DataSourceType;

/**
 * 鑷畾涔夊鏁版嵁婧愬垏鎹㈡敞瑙?
 *
 * 浼樺厛绾э細鍏堟柟娉曪紝鍚庣被锛屽鏋滄柟娉曡鐩栦簡绫讳笂鐨勬暟鎹簮绫诲瀷锛屼互鏂规硶鐨勪负鍑嗭紝鍚﹀垯浠ョ被涓婄殑涓哄噯
 *
 * @author Eapp1e
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource
{
    /**
     * 鍒囨崲鏁版嵁婧愬悕绉?
     */
    public DataSourceType value() default DataSourceType.MASTER;
}
