package com.eapple.common.utils.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{
    /** Bean鏂规硶鍚嶄腑灞炴€у悕寮€濮嬬殑涓嬫爣 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 鍖归厤getter鏂规硶鐨勬鍒欒〃杈惧紡 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 鍖归厤setter鏂规硶鐨勬鍒欒〃杈惧紡 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean灞炴€у鍒跺伐鍏锋柟娉曘€?
     * 
     * @param dest 鐩爣瀵硅薄
     * @param src 婧愬璞?
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
     * 鑾峰彇瀵硅薄鐨剆etter鏂规硶銆?
     * 
     * @param obj 瀵硅薄
     * @return 瀵硅薄鐨剆etter鏂规硶鍒楄〃
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter鏂规硶鍒楄〃
        List<Method> setterMethods = new ArrayList<Method>();

        // 鑾峰彇鎵€鏈夋柟娉?
        Method[] methods = obj.getClass().getMethods();

        // 鏌ユ壘setter鏂规硶

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 杩斿洖setter鏂规硶鍒楄〃
        return setterMethods;
    }

    /**
     * 鑾峰彇瀵硅薄鐨刧etter鏂规硶銆?
     * 
     * @param obj 瀵硅薄
     * @return 瀵硅薄鐨刧etter鏂规硶鍒楄〃
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter鏂规硶鍒楄〃
        List<Method> getterMethods = new ArrayList<Method>();
        // 鑾峰彇鎵€鏈夋柟娉?
        Method[] methods = obj.getClass().getMethods();
        // 鏌ユ壘getter鏂规硶
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 杩斿洖getter鏂规硶鍒楄〃
        return getterMethods;
    }

    /**
     * 妫€鏌ean鏂规硶鍚嶄腑鐨勫睘鎬у悕鏄惁鐩哥瓑銆?br>
     * 濡俫etName()鍜宻etName()灞炴€у悕涓€鏍凤紝getName()鍜宻etAge()灞炴€у悕涓嶄竴鏍枫€?
     * 
     * @param m1 鏂规硶鍚?
     * @param m2 鏂规硶鍚?
     * @return 灞炴€у悕涓€鏍疯繑鍥瀟rue锛屽惁鍒欒繑鍥瀎alse
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }
}
