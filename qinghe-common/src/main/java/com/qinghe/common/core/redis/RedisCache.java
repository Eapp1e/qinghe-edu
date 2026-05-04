package com.qinghe.common.core.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * Spring Redis 缓存工具类。
 *
 * @author Eapp1e
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisCache
{
    private static final long NO_EXPIRE = -1L;

    private final Map<String, LocalCacheValue> localCache = new ConcurrentHashMap<>();

    @Autowired
    public RedisTemplate redisTemplate;

    private static final class LocalCacheValue
    {
        private final Object value;

        private final long expireAt;

        private LocalCacheValue(Object value, long expireAt)
        {
            this.value = value;
            this.expireAt = expireAt;
        }
    }

    /**
     * 缓存基础对象，Integer、String、实体类等都可以。
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        try
        {
            redisTemplate.opsForValue().set(key, value);
        }
        catch (Exception ex)
        {
            setLocalCacheObject(key, value, NO_EXPIRE, TimeUnit.SECONDS);
        }
    }

    /**
     * 缓存基础对象，支持设置过期时间。
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        try
        {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }
        catch (Exception ex)
        {
            setLocalCacheObject(key, value, timeout.longValue(), timeUnit);
        }
    }

    /**
     * 设置缓存过期时间。
     *
     * @param key Redis 键
     * @param timeout 过期时间
     * @return true 表示设置成功，false 表示设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存过期时间。
     *
     * @param key Redis 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 表示设置成功，false 表示设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        try
        {
            return redisTemplate.expire(key, timeout, unit);
        }
        catch (Exception ex)
        {
            LocalCacheValue cached = getLocalCacheValue(key);
            if (cached == null)
            {
                return false;
            }
            setLocalCacheObject(key, cached.value, timeout, unit);
            return true;
        }
    }

    /**
     * 获取缓存剩余有效时间。
     *
     * @param key Redis 键
     * @return 剩余过期时间
     */
    public long getExpire(final String key)
    {
        try
        {
            return redisTemplate.getExpire(key);
        }
        catch (Exception ex)
        {
            LocalCacheValue cached = getLocalCacheValue(key);
            if (cached == null)
            {
                return 0L;
            }
            if (cached.expireAt == NO_EXPIRE)
            {
                return NO_EXPIRE;
            }
            long ttlMillis = cached.expireAt - System.currentTimeMillis();
            return Math.max(0L, TimeUnit.MILLISECONDS.toSeconds(ttlMillis));
        }
    }

    /**
     * 判断 key 是否存在。
     *
     * @param key 键
     * @return true 存在，false 不存在
     */
    public Boolean hasKey(String key)
    {
        try
        {
            return redisTemplate.hasKey(key);
        }
        catch (Exception ex)
        {
            return getLocalCacheValue(key) != null;
        }
    }

    /**
     * 获取缓存中的基础对象。
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public <T> T getCacheObject(final String key)
    {
        try
        {
            ValueOperations<String, T> operation = redisTemplate.opsForValue();
            return operation.get(key);
        }
        catch (Exception ex)
        {
            LocalCacheValue cached = getLocalCacheValue(key);
            return cached == null ? null : (T) cached.value;
        }
    }

    /**
     * 删除单个缓存对象。
     *
     * @param key 缓存键
     */
    public boolean deleteObject(final String key)
    {
        try
        {
            return redisTemplate.delete(key);
        }
        catch (Exception ex)
        {
            return localCache.remove(key) != null;
        }
    }

    /**
     * 批量删除缓存对象。
     *
     * @param collection 键集合
     * @return 是否删除成功
     */
    public boolean deleteObject(final Collection collection)
    {
        try
        {
            return redisTemplate.delete(collection) > 0;
        }
        catch (Exception ex)
        {
            boolean removed = false;
            for (Object key : collection)
            {
                removed = localCache.remove(String.valueOf(key)) != null || removed;
            }
            return removed;
        }
    }

    /**
     * 缓存 List 数据。
     *
     * @param key 缓存键
     * @param dataList List 数据
     * @return 写入条数
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获取缓存中的 List 数据。
     *
     * @param key 缓存键
     * @return List 数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存 Set 数据。
     *
     * @param key 缓存键
     * @param dataSet Set 数据
     * @return 绑定的 Set 操作对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获取缓存中的 Set 数据。
     *
     * @param key 缓存键
     * @return Set 数据
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存 Map 数据。
     *
     * @param key 缓存键
     * @param dataMap Map 数据
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获取缓存中的 Map 数据。
     *
     * @param key 缓存键
     * @return Map 数据
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 向 Hash 中存储数据。
     *
     * @param key Redis 键
     * @param hKey Hash 键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取 Hash 中的单个值。
     *
     * @param key Redis 键
     * @param hKey Hash 键
     * @return Hash 中的值
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 批量获取 Hash 中的多个值。
     *
     * @param key Redis 键
     * @param hKeys Hash 键集合
     * @return Hash 值集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除 Hash 中的单个键值。
     *
     * @param key Redis 键
     * @param hKey Hash 键
     * @return 是否删除成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey)
    {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 按模式查询缓存键。
     *
     * @param pattern 键匹配模式
     * @return 键集合
     */
    public Collection<String> keys(final String pattern)
    {
        try
        {
            return redisTemplate.keys(pattern);
        }
        catch (Exception ex)
        {
            cleanupExpiredEntries();
            return getLocalKeys(pattern);
        }
    }

    private <T> void setLocalCacheObject(final String key, final T value, final long timeout, final TimeUnit timeUnit)
    {
        long expireAt = NO_EXPIRE;
        if (timeout > 0)
        {
            expireAt = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        }
        localCache.put(key, new LocalCacheValue(value, expireAt));
    }

    private LocalCacheValue getLocalCacheValue(String key)
    {
        LocalCacheValue cached = localCache.get(key);
        if (cached == null)
        {
            return null;
        }
        if (cached.expireAt != NO_EXPIRE && cached.expireAt <= System.currentTimeMillis())
        {
            localCache.remove(key);
            return null;
        }
        return cached;
    }

    private void cleanupExpiredEntries()
    {
        for (String key : new ArrayList<>(localCache.keySet()))
        {
            getLocalCacheValue(key);
        }
    }

    private Collection<String> getLocalKeys(String pattern)
    {
        if (pattern == null || pattern.isEmpty())
        {
            return Collections.emptySet();
        }
        if (!pattern.contains("*"))
        {
            return localCache.containsKey(pattern) ? Collections.singleton(pattern) : Collections.emptySet();
        }
        String prefix = pattern.substring(0, pattern.indexOf('*'));
        Set<String> matched = new LinkedHashSet<>();
        for (String key : localCache.keySet())
        {
            if (key.startsWith(prefix) && getLocalCacheValue(key) != null)
            {
                matched.add(key);
            }
        }
        return matched;
    }
}