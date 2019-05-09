package com.bin.kong.proxy.core.cache.impl;

import com.bin.kong.proxy.core.cache.ILocalCache;
import com.bin.kong.proxy.core.cache.LocalCacheDO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class LocalCacheUtils implements ILocalCache {

    private static ConcurrentHashMap<Object, LocalCacheDO> cache = new ConcurrentHashMap<>();

    /**
     * 判断缓存是否存在
     *
     * @param key
     * @return
     */
    public static boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public static void put(Object key, Object value) {
        cache.put(key, LocalCacheDO.builder()
                .cacheTime(System.currentTimeMillis())
                .cacheTime(0)
                .value(value)
                .build());
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public static void putIfAbsent(Object key, Object value) {
        cache.putIfAbsent(key, LocalCacheDO.builder()
                .cacheTime(System.currentTimeMillis())
                .cacheTime(0)
                .value(value)
                .build());
    }
    /**
     * 写入缓存 并设置过期时间
     *
     * @param key
     * @param value
     * @param cacheTime
     * @param unit
     */
    public static void put(Object key, Object value, long cacheTime, TimeUnit unit) {
        cache.put(key, LocalCacheDO.builder()
                .createTime(System.currentTimeMillis())
                .cacheTime(unit.toMillis(cacheTime))
                .value(value)
                .build());
    }

    /**
     * 写入缓存 并设置过期时间
     * @param key
     * @param value
     * @param cacheTime
     * @param unit
     */
    public static void putIfAbsent(Object key, Object value, long cacheTime, TimeUnit unit) {
        cache.putIfAbsent(key, LocalCacheDO.builder()
                .createTime(System.currentTimeMillis())
                .cacheTime(unit.toMillis(cacheTime))
                .value(value)
                .build());
    }
    /**
     * 删除缓存
     *
     * @param key
     */
    public static void remove(Object key) {
        cache.remove(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(Object key) {
        LocalCacheDO localCacheDO = cache.get(key);
        if (localCacheDO == null) {
            return null;
        }
        return (T) localCacheDO.getValue();
    }

    @Override
    public void refresh() {
        for (Object key : cache.keySet()) {
            LocalCacheDO localCacheDO = cache.get(key);
            long currentTime = System.currentTimeMillis();
            if (localCacheDO.getCacheTime() > 0 && currentTime - localCacheDO.getCreateTime() > localCacheDO.getCacheTime()) {
                cache.remove(key);
            }
        }
    }
}
