package com.eapple.common.xss;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鑷畾涔墄ss鏍￠獙娉ㄨВ
 * 
 * @author Eapp1e
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
@Constraint(validatedBy = { XssValidator.class })
public @interface Xss
{
    String message()

    default "涓嶅厑璁镐换浣曡剼鏈繍琛?;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
