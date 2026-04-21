package com.eapple.common.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eapple.common.core.text.Convert;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.StringUtils;

/**
 * 鍙嶅皠宸ュ叿绫? 鎻愪緵璋冪敤getter/setter鏂规硶, 璁块棶绉佹湁鍙橀噺, 璋冪敤绉佹湁鏂规硶, 鑾峰彇娉涘瀷绫诲瀷Class, 琚獳OP杩囩殑鐪熷疄绫荤瓑宸ュ叿鍑芥暟.
 * 
 * @author Eapp1e
 */
@SuppressWarnings("rawtypes")
public class ReflectUtils
{
    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 璋冪敤Getter鏂规硶.
     * 鏀寔澶氱骇锛屽锛氬璞″悕.瀵硅薄鍚?鏂规硶
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName)
    {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, "."))
        {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
        }
        return (E) object;
    }

    /**
     * 璋冪敤Setter鏂规硶, 浠呭尮閰嶆柟娉曞悕銆?
     * 鏀寔澶氱骇锛屽锛氬璞″悕.瀵硅薄鍚?鏂规硶
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E value)
    {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; i++)
        {
            if (i < names.length - 1)
            {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
            }
            else
            {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[] { value });
            }
        }
    }

    /**
     * 鐩存帴璇诲彇瀵硅薄灞炴€у€? 鏃犺private/protected淇グ绗? 涓嶇粡杩噂etter鍑芥暟.
     */
    @SuppressWarnings("unchecked")
    public static <E> E getFieldValue(final Object obj, final String fieldName)
    {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null)
        {
            logger.debug("鍦?[" + obj.getClass() + "] 涓紝娌℃湁鎵惧埌 [" + fieldName + "] 瀛楁 ");
            return null;
        }
        E result = null;
        try
        {
            result = (E) field.get(obj);
        }
        catch (IllegalAccessException e)
        {
            logger.error("涓嶅彲鑳芥姏鍑虹殑寮傚父{}", e.getMessage());
        }
        return result;
    }

    /**
     * 鐩存帴璁剧疆瀵硅薄灞炴€у€? 鏃犺private/protected淇グ绗? 涓嶇粡杩噑etter鍑芥暟.
     */
    public static <E> void setFieldValue(final Object obj, final String fieldName, final E value)
    {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null)
        {
            // throw new IllegalArgumentException("鍦?[" + obj.getClass() + "] 涓紝娌℃湁鎵惧埌 [" + fieldName + "] 瀛楁 ");
            logger.debug("鍦?[" + obj.getClass() + "] 涓紝娌℃湁鎵惧埌 [" + fieldName + "] 瀛楁 ");
            return;
        }
        try
        {
            field.set(obj, value);
        }
        catch (IllegalAccessException e)
        {
            logger.error("涓嶅彲鑳芥姏鍑虹殑寮傚父: {}", e.getMessage());
        }
    }

    /**
     * 鐩存帴璋冪敤瀵硅薄鏂规硶, 鏃犺private/protected淇グ绗?
     * 鐢ㄤ簬涓€娆℃€ц皟鐢ㄧ殑鎯呭喌锛屽惁鍒欏簲浣跨敤getAccessibleMethod()鍑芥暟鑾峰緱Method鍚庡弽澶嶈皟鐢?
     * 鍚屾椂鍖归厤鏂规硶鍚?鍙傛暟绫诲瀷锛?
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
            final Object[] args)
    {
        if (obj == null || methodName == null)
        {
            return null;
        }
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null)
        {
            logger.debug("鍦?[" + obj.getClass() + "] 涓紝娌℃湁鎵惧埌 [" + methodName + "] 鏂规硶 ");
            return null;
        }
        try
        {
            return (E) method.invoke(obj, args);
        }
        catch (Exception e)
        {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 鐩存帴璋冪敤瀵硅薄鏂规硶, 鏃犺private/protected淇グ绗︼紝
     * 鐢ㄤ簬涓€娆℃€ц皟鐢ㄧ殑鎯呭喌锛屽惁鍒欏簲浣跨敤getAccessibleMethodByName()鍑芥暟鑾峰緱Method鍚庡弽澶嶈皟鐢?
     * 鍙尮閰嶅嚱鏁板悕锛屽鏋滄湁澶氫釜鍚屽悕鍑芥暟璋冪敤绗竴涓€?
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethodByName(final Object obj, final String methodName, final Object[] args)
    {
        Method method = getAccessibleMethodByName(obj, methodName, args.length);
        if (method == null)
        {
            // 濡傛灉涓虹┖涓嶆姤閿欙紝鐩存帴杩斿洖绌恒€?
            logger.debug("鍦?[" + obj.getClass() + "] 涓紝娌℃湁鎵惧埌 [" + methodName + "] 鏂规硶 ");
            return null;
        }
        try
        {
            // 绫诲瀷杞崲锛堝皢鍙傛暟鏁版嵁绫诲瀷杞崲涓虹洰鏍囨柟娉曞弬鏁扮被鍨嬶級
            Class<?>[] cs = method.getParameterTypes();
            for (int i = 0; i < cs.length; i++)
            {
                if (args[i] != null && !args[i].getClass().equals(cs[i]))
                {
                    if (cs[i] == String.class)
                    {
                        args[i] = Convert.toStr(args[i]);
                        if (StringUtils.endsWith((String) args[i], ".0"))
                        {
                            args[i] = StringUtils.substringBefore((String) args[i], ".0");
                        }
                    }
                    else if (cs[i] == Integer.class)
                    {
                        args[i] = Convert.toInt(args[i]);
                    }
                    else if (cs[i] == Long.class)
                    {
                        args[i] = Convert.toLong(args[i]);
                    }
                    else if (cs[i] == Double.class)
                    {
                        args[i] = Convert.toDouble(args[i]);
                    }
                    else if (cs[i] == Float.class)
                    {
                        args[i] = Convert.toFloat(args[i]);
                    }
                    else if (cs[i] == Date.class)
                    {
                        if (args[i] instanceof String)
                        {
                            args[i] = DateUtils.parseDate(args[i]);
                        }
                        else
                        {
                            args[i] = DateUtil.getJavaDate((Double) args[i]);
                        }
                    }
                    else if (cs[i] == boolean.class || cs[i] == Boolean.class)
                    {
                        args[i] = Convert.toBool(args[i]);
                    }
                }
            }
            return (E) method.invoke(obj, args);
        }
        catch (Exception e)
        {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 寰幆鍚戜笂杞瀷, 鑾峰彇瀵硅薄鐨凞eclaredField, 骞跺己鍒惰缃负鍙闂?
     * 濡傚悜涓婅浆鍨嬪埌Object浠嶆棤娉曟壘鍒? 杩斿洖null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName)
    {
        // 涓虹┖涓嶆姤閿欍€傜洿鎺ヨ繑鍥?null
        if (obj == null)
        {
            return null;
        }
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass())
        {
            try
            {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            }
            catch (NoSuchFieldException e)
            {
                continue;
            }
        }
        return null;
    }

    /**
     * 寰幆鍚戜笂杞瀷, 鑾峰彇瀵硅薄鐨凞eclaredMethod,骞跺己鍒惰缃负鍙闂?
     * 濡傚悜涓婅浆鍨嬪埌Object浠嶆棤娉曟壘鍒? 杩斿洖null.
     * 鍖归厤鍑芥暟鍚?鍙傛暟绫诲瀷銆?
     * 鐢ㄤ簬鏂规硶闇€瑕佽澶氭璋冪敤鐨勬儏鍐? 鍏堜娇鐢ㄦ湰鍑芥暟鍏堝彇寰桵ethod,鐒跺悗璋冪敤Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
            final Class<?>... parameterTypes)
    {
        // 涓虹┖涓嶆姤閿欍€傜洿鎺ヨ繑鍥?null
        if (obj == null)
        {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass())
        {
            try
            {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            }
            catch (NoSuchMethodException e)
            {
                continue;
            }
        }
        return null;
    }

    /**
     * 寰幆鍚戜笂杞瀷, 鑾峰彇瀵硅薄鐨凞eclaredMethod,骞跺己鍒惰缃负鍙闂?
     * 濡傚悜涓婅浆鍨嬪埌Object浠嶆棤娉曟壘鍒? 杩斿洖null.
     * 鍙尮閰嶅嚱鏁板悕銆?
     * 鐢ㄤ簬鏂规硶闇€瑕佽澶氭璋冪敤鐨勬儏鍐? 鍏堜娇鐢ㄦ湰鍑芥暟鍏堝彇寰桵ethod,鐒跺悗璋冪敤Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName, int argsNum)
    {
        // 涓虹┖涓嶆姤閿欍€傜洿鎺ヨ繑鍥?null
        if (obj == null)
        {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass())
        {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods)
            {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == argsNum)
                {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 鏀瑰彉private/protected鐨勬柟娉曚负public锛屽敖閲忎笉璋冪敤瀹為檯鏀瑰姩鐨勮鍙ワ紝閬垮厤JDK鐨凷ecurityManager鎶辨€ㄣ€?
     */
    @SuppressWarnings("deprecation")
    public static void makeAccessible(Method method)
    {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible())
        {
            method.setAccessible(true);
        }
    }

    /**
     * 鏀瑰彉private/protected鐨勬垚鍛樺彉閲忎负public锛屽敖閲忎笉璋冪敤瀹為檯鏀瑰姩鐨勮鍙ワ紝閬垮厤JDK鐨凷ecurityManager鎶辨€ㄣ€?
     */
    @SuppressWarnings("deprecation")
    public static void makeAccessible(Field field)
    {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible())
        {
            field.setAccessible(true);
        }
    }

    /**
     * 閫氳繃鍙嶅皠, 鑾峰緱Class瀹氫箟涓０鏄庣殑娉涘瀷鍙傛暟鐨勭被鍨? 娉ㄦ剰娉涘瀷蹇呴』瀹氫箟鍦ㄧ埗绫诲
     * 濡傛棤娉曟壘鍒? 杩斿洖Object.class.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz)
    {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 閫氳繃鍙嶅皠, 鑾峰緱Class瀹氫箟涓０鏄庣殑鐖剁被鐨勬硾鍨嬪弬鏁扮殑绫诲瀷.
     * 濡傛棤娉曟壘鍒? 杩斿洖Object.class.
     */
    public static Class getClassGenricType(final Class clazz, final int index)
    {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType))
        {
            logger.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0)
        {
            logger.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class))
        {
            logger.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    public static Class<?> getUserClass(Object instance)
    {
        if (instance == null)
        {
            throw new RuntimeException("Instance must not be null");
        }
        Class clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR))
        {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass))
            {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * 灏嗗弽灏勬椂鐨刢hecked exception杞崲涓簎nchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e)
    {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException)
        {
            return new IllegalArgumentException(msg, e);
        }
        else if (e instanceof InvocationTargetException)
        {
            return new RuntimeException(msg, ((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(msg, e);
    }
}
