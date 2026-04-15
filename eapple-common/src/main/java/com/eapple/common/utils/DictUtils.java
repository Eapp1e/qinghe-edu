package com.eapple.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSONArray;
import com.eapple.common.constant.CacheConstants;
import com.eapple.common.core.domain.entity.SysDictData;
import com.eapple.common.core.redis.RedisCache;
import com.eapple.common.utils.spring.SpringUtils;

/**
 * 瀛楀吀宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class DictUtils
{
    /**
     * 鍒嗛殧绗?
     */
    public static final String SEPARATOR = ",";

    /**
     * 璁剧疆瀛楀吀缂撳瓨
     * 
     * @param key 鍙傛暟閿?
     * @param dictDatas 瀛楀吀鏁版嵁鍒楄〃
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas)
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 鑾峰彇瀛楀吀缂撳瓨
     * 
     * @param key 鍙傛暟閿?
     * @return dictDatas 瀛楀吀鏁版嵁鍒楄〃
     */
    public static List<SysDictData> getDictCache(String key)
    {
        JSONArray arrayCache = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache))
        {
            return arrayCache.toList(SysDictData.class);
        }
        return null;
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏稿€艰幏鍙栧瓧鍏告爣绛?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictValue 瀛楀吀鍊?
     * @return 瀛楀吀鏍囩
     */
    public static String getDictLabel(String dictType, String dictValue)
    {
        if (StringUtils.isEmpty(dictValue))
        {
            return StringUtils.EMPTY;
        }
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏告爣绛捐幏鍙栧瓧鍏稿€?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictLabel 瀛楀吀鏍囩
     * @return 瀛楀吀鍊?
     */
    public static String getDictValue(String dictType, String dictLabel)
    {
        if (StringUtils.isEmpty(dictLabel))
        {
            return StringUtils.EMPTY;
        }
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏稿€艰幏鍙栧瓧鍏告爣绛?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictValue 瀛楀吀鍊?
     * @param separator 鍒嗛殧绗?
     * @return 瀛楀吀鏍囩
     */
    public static String getDictLabel(String dictType, String dictValue, String separator)
    {
        List<SysDictData> datas = getDictCache(dictType);
        if (StringUtils.isNull(datas) || StringUtils.isEmpty(dictValue))
        {
            return StringUtils.EMPTY;
        }
        Map<String, String> dictMap = datas.stream().collect(HashMap::new, (map, dict) -> map.put(dict.getDictValue(), dict.getDictLabel()), Map::putAll);
        if (!StringUtils.contains(dictValue, separator))
        {
            return dictMap.getOrDefault(dictValue, StringUtils.EMPTY);
        }
        StringBuilder labelBuilder = new StringBuilder();
        for (String seperatedValue : dictValue.split(separator))
        {
            if (dictMap.containsKey(seperatedValue))
            {
                labelBuilder.append(dictMap.get(seperatedValue)).append(separator);
            }
        }
        return StringUtils.removeEnd(labelBuilder.toString(), separator);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鍜屽瓧鍏告爣绛捐幏鍙栧瓧鍏稿€?
     * 
     * @param dictType 瀛楀吀绫诲瀷
     * @param dictLabel 瀛楀吀鏍囩
     * @param separator 鍒嗛殧绗?
     * @return 瀛楀吀鍊?
     */
    public static String getDictValue(String dictType, String dictLabel, String separator)
    {
        List<SysDictData> datas = getDictCache(dictType);
        if (StringUtils.isNull(datas) || StringUtils.isEmpty(dictLabel))
        {
            return StringUtils.EMPTY;
        }
        Map<String, String> dictMap = datas.stream().collect(HashMap::new, (map, dict) -> map.put(dict.getDictLabel(), dict.getDictValue()), Map::putAll);
        if (!StringUtils.contains(dictLabel, separator))
        {
            return dictMap.getOrDefault(dictLabel, StringUtils.EMPTY);
        }
        StringBuilder valueBuilder = new StringBuilder();
        for (String seperatedValue : dictLabel.split(separator))
        {
            if (dictMap.containsKey(seperatedValue))
            {
                valueBuilder.append(dictMap.get(seperatedValue)).append(separator);
            }
        }
        return StringUtils.removeEnd(valueBuilder.toString(), separator);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鑾峰彇瀛楀吀鎵€鏈夊€?
     *
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀鍊?
     */
    public static String getDictValues(String dictType)
    {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> datas = getDictCache(dictType);
        if (StringUtils.isNull(datas))
        {
            return StringUtils.EMPTY;
        }
        for (SysDictData dict : datas)
        {
            propertyString.append(dict.getDictValue()).append(SEPARATOR);
        }
        return StringUtils.stripEnd(propertyString.toString(), SEPARATOR);
    }

    /**
     * 鏍规嵁瀛楀吀绫诲瀷鑾峰彇瀛楀吀鎵€鏈夋爣绛?
     *
     * @param dictType 瀛楀吀绫诲瀷
     * @return 瀛楀吀鍊?
     */
    public static String getDictLabels(String dictType)
    {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> datas = getDictCache(dictType);
        if (StringUtils.isNull(datas))
        {
            return StringUtils.EMPTY;
        }
        for (SysDictData dict : datas)
        {
            propertyString.append(dict.getDictLabel()).append(SEPARATOR);
        }
        return StringUtils.stripEnd(propertyString.toString(), SEPARATOR);
    }

    /**
     * 鍒犻櫎鎸囧畾瀛楀吀缂撳瓨
     * 
     * @param key 瀛楀吀閿?
     */
    public static void removeDictCache(String key)
    {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 娓呯┖瀛楀吀缂撳瓨
     */
    public static void clearDictCache()
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(CacheConstants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 璁剧疆cache key
     * 
     * @param configKey 鍙傛暟閿?
     * @return 缂撳瓨閿甼ey
     */
    public static String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }
}
