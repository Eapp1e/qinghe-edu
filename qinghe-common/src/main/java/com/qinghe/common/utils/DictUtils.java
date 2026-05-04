package com.qinghe.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.qinghe.common.constant.CacheConstants;
import com.qinghe.common.core.domain.entity.SysDictData;
import com.qinghe.common.core.redis.RedisCache;
import com.qinghe.common.utils.spring.SpringUtils;

/**
 * 数据字典工具类。
 *
 * @author Eapp1e
 */
public class DictUtils
{
    /**
     * 多值分隔符。
     */
    public static final String SEPARATOR = ",";

    /**
     * 写入字典缓存。
     *
     * @param key 缓存键
     * @param dictDatas 字典数据集合
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas)
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存。
     *
     * @param key 缓存键
     * @return 字典数据集合
     */
    public static List<SysDictData> getDictCache(String key)
    {
        Object cacheObject = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNull(cacheObject))
        {
            return null;
        }
        if (cacheObject instanceof JSONArray)
        {
            return ((JSONArray) cacheObject).toList(SysDictData.class);
        }
        if (cacheObject instanceof List)
        {
            return JSON.parseArray(JSON.toJSONString(cacheObject), SysDictData.class);
        }
        return JSON.parseArray(String.valueOf(cacheObject), SysDictData.class);
    }

    /**
     * 根据字典类型和字典值获取字典标签。
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
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
     * 根据字典类型和字典标签获取字典值。
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
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
     * 根据字典类型和字典值获取字典标签。
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param separator 多值分隔符
     * @return 字典标签
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
        for (String separatedValue : dictValue.split(separator))
        {
            if (dictMap.containsKey(separatedValue))
            {
                labelBuilder.append(dictMap.get(separatedValue)).append(separator);
            }
        }
        return StringUtils.removeEnd(labelBuilder.toString(), separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值。
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param separator 多值分隔符
     * @return 字典值
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
        for (String separatedValue : dictLabel.split(separator))
        {
            if (dictMap.containsKey(separatedValue))
            {
                valueBuilder.append(dictMap.get(separatedValue)).append(separator);
            }
        }
        return StringUtils.removeEnd(valueBuilder.toString(), separator);
    }

    /**
     * 获取字典值列表。
     *
     * @param dictType 字典类型
     * @return 多个字典值拼接后的字符串
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
     * 获取字典标签列表。
     *
     * @param dictType 字典类型
     * @return 多个字典标签拼接后的字符串
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
     * 删除指定字典缓存。
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key)
    {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空所有字典缓存。
     */
    public static void clearDictCache()
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(CacheConstants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 生成字典缓存键。
     *
     * @param configKey 字典键
     * @return 缓存键
     */
    public static String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }
}
