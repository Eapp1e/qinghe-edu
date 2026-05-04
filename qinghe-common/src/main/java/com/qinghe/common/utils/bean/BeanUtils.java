package com.qinghe.common.utils.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类。
 *
 * @author Eapp1e
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{
    /** Bean 属性名在方法名中的起始下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** Getter 方法匹配规则 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** Setter 方法匹配规则 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * 复制 Bean 属性。
     *
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象中的所有 Setter 方法。
     *
     * @param obj 对象实例
     * @return Setter 方法集合
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        List<Method> setterMethods = new ArrayList<>();
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        return setterMethods;
    }

    /**
     * 获取对象中的所有 Getter 方法。
     *
     * @param obj 对象实例
     * @return Getter 方法集合
     */
    public static List<Method> getGetterMethods(Object obj)
    {
        List<Method> getterMethods = new ArrayList<>();
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        return getterMethods;
    }

    /**
     * 判断两个方法名对应的属性名是否一致。
     * 例如 getName 与 setName 返回 true，getName 与 setAge 返回 false。
     *
     * @param m1 方法名一
     * @param m2 方法名二
     * @return 是否为同一属性
     */
    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }
}
