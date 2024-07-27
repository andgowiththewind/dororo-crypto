package com.dororo.future.dororocrypto.service;

import cn.hutool.core.lang.Assert;
import com.dororo.future.dororocrypto.components.RedisMasterCache;
import com.dororo.future.dororocrypto.components.RedisSlaveCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统参数服务
 *
 * @author Dororo
 * @date 2024-01-07 05:05
 */
@Service
public class SysParamService {
    // 由于没有整合关系型数据库,这里维护一个静态变量充当系统变量的数据库,防止REDIS键过期后无法获取系统变量
    private static final Map<String, Object> sysParamsMap = new HashMap<>();

    private final RedisSlaveCache redisSlaveCache;
    private final RedisMasterCache redisMasterCache;

    public SysParamService(RedisSlaveCache redisSlaveCache, RedisMasterCache redisMasterCache) {
        this.redisSlaveCache = redisSlaveCache;
        this.redisMasterCache = redisMasterCache;
    }

    public Object getSysParam(String key) {
        Assert.notBlank(key);
        // 缓存中查的出,则直接返回
        Object cacheMapValue = redisSlaveCache.getCacheMapValue(CacheConstants.SYS_PARAM_MAP, key);
        if (cacheMapValue != null) {
            return cacheMapValue;
        }
        // 缓存中查不出,则从静态变量中查找
        Object obj = sysParamsMap.get(key);
        if (obj != null) {
            // 重新缓存
            redisMasterCache.setCacheMapValue(CacheConstants.SYS_PARAM_MAP, key, obj);
        }
        return obj;
    }

    /**
     * 设置系统参数
     * <p>系统初始化启动时会将一些参数设置到静态变量中,防止REDIS键过期后无法获取系统变量</p>
     */
    public static void setSysParam(String key, Object value) {
        Assert.notBlank(key);
        sysParamsMap.put(key, value);
    }
}
