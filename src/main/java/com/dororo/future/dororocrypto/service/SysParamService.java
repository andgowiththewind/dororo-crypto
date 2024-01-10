package com.dororo.future.dororocrypto.service;

import com.dororo.future.dororocrypto.components.RedisSlaveCache;
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
    private RedisSlaveCache redisSlaveCache;

    public Object getSysParam(String key) {
        Map<String, Object> sysParamMap = redisSlaveCache.getCacheMap(CacheConstants.SYS_PARAM_MAP);
        if (sysParamMap == null) {
            return null;
        }
        return sysParamMap.get(key);
    }
}
