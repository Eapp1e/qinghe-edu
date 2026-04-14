package com.ruoyi.common.core.redis;

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
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * spring redis 工具类
 *
 * @author ruoyi
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
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
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
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
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
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
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
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
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
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
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
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
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
     * 删除单个对象
     *
     * @param key
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
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
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
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
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
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey)
    {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
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
