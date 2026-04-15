package com.eapple.common.core.redis;

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
 * spring redis 宸ュ叿绫?
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
     * 缂撳瓨鍩烘湰鐨勫璞★紝Integer銆丼tring銆佸疄浣撶被绛?
     *
     * @param key 缂撳瓨鐨勯敭鍊?
     * @param value 缂撳瓨鐨勫€?
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
     * 缂撳瓨鍩烘湰鐨勫璞★紝Integer銆丼tring銆佸疄浣撶被绛?
     *
     * @param key 缂撳瓨鐨勯敭鍊?
     * @param value 缂撳瓨鐨勫€?
     * @param timeout 鏃堕棿
     * @param timeUnit 鏃堕棿棰楃矑搴?
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
     * 璁剧疆鏈夋晥鏃堕棿
     *
     * @param key Redis閿?
     * @param timeout 瓒呮椂鏃堕棿
     * @return true=璁剧疆鎴愬姛锛沠alse=璁剧疆澶辫触
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 璁剧疆鏈夋晥鏃堕棿
     *
     * @param key Redis閿?
     * @param timeout 瓒呮椂鏃堕棿
     * @param unit 鏃堕棿鍗曚綅
     * @return true=璁剧疆鎴愬姛锛沠alse=璁剧疆澶辫触
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
     * 鑾峰彇鏈夋晥鏃堕棿
     *
     * @param key Redis閿?
     * @return 鏈夋晥鏃堕棿
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
     * 鍒ゆ柇 key鏄惁瀛樺湪
     *
     * @param key 閿?
     * @return true 瀛樺湪 false涓嶅瓨鍦?
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
     * 鑾峰緱缂撳瓨鐨勫熀鏈璞°€?
     *
     * @param key 缂撳瓨閿€?
     * @return 缂撳瓨閿€煎搴旂殑鏁版嵁
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
     * 鍒犻櫎鍗曚釜瀵硅薄
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
     * 鍒犻櫎闆嗗悎瀵硅薄
     *
     * @param collection 澶氫釜瀵硅薄
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
     * 缂撳瓨List鏁版嵁
     *
     * @param key 缂撳瓨鐨勯敭鍊?
     * @param dataList 寰呯紦瀛樼殑List鏁版嵁
     * @return 缂撳瓨鐨勫璞?
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 鑾峰緱缂撳瓨鐨刲ist瀵硅薄
     *
     * @param key 缂撳瓨鐨勯敭鍊?
     * @return 缂撳瓨閿€煎搴旂殑鏁版嵁
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缂撳瓨Set
     *
     * @param key 缂撳瓨閿€?
     * @param dataSet 缂撳瓨鐨勬暟鎹?
     * @return 缂撳瓨鏁版嵁鐨勫璞?
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
     * 鑾峰緱缂撳瓨鐨剆et
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缂撳瓨Map
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
     * 鑾峰緱缂撳瓨鐨凪ap
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 寰€Hash涓瓨鍏ユ暟鎹?
     *
     * @param key Redis閿?
     * @param hKey Hash閿?
     * @param value 鍊?
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 鑾峰彇Hash涓殑鏁版嵁
     *
     * @param key Redis閿?
     * @param hKey Hash閿?
     * @return Hash涓殑瀵硅薄
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 鑾峰彇澶氫釜Hash涓殑鏁版嵁
     *
     * @param key Redis閿?
     * @param hKeys Hash閿泦鍚?
     * @return Hash瀵硅薄闆嗗悎
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 鍒犻櫎Hash涓殑鏌愭潯鏁版嵁
     *
     * @param key Redis閿?
     * @param hKey Hash閿?
     * @return 鏄惁鎴愬姛
     */
    public boolean deleteCacheMapValue(final String key, final String hKey)
    {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 鑾峰緱缂撳瓨鐨勫熀鏈璞″垪琛?
     *
     * @param pattern 瀛楃涓插墠缂€
     * @return 瀵硅薄鍒楄〃
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
