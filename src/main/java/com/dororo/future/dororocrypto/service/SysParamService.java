package com.dororo.future.dororocrypto.service;

import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.config.redis.RedisDataSourceType;
import com.dororo.future.dororocrypto.config.redis.RedisSelect;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 系统参数服务
 *
 * @author Dororo
 * @date 2024-01-07 05:05
 */
@Service
public class SysParamService {
    @Autowired
    private RedisCache redisCache;

    @RedisSelect(value = RedisDataSourceType.SLAVE)
    public Object getSysParam(String key) {
        Map<String, Object> sysParamMap = redisCache.getCacheMap(CacheConstants.SYS_PARAM_MAP);
        if (sysParamMap == null) {
            return null;
        }
        return sysParamMap.get(key);
    }
}
