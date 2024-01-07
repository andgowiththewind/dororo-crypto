package com.dororo.future.dororocrypto.config.redisson;

import com.dororo.future.dororocrypto.config.redis.DynamicRedisProperties;
import com.dororo.future.dororocrypto.config.redis.RedisDataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 *
 * @author Dororo
 * @date 2024-01-07 13:50
 */
@Slf4j
@Configuration
public class RedissonClientConfiguration {
    @Autowired
    private DynamicRedisProperties dynamicRedisProperties;


    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        // 当前系统为单机模式,从主从配置信息中获取主节点的配置信息
        DynamicRedisProperties.PropertyDTO master = dynamicRedisProperties.getPropertiesMap().get(RedisDataSourceType.MASTER.name().toLowerCase());

        // 创建 Redisson 配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + master.getHost() + ":" + master.getPort())
                .setPassword(master.getPassword());

        // config.setLockWatchdogTimeout(10000L);


        log.debug("=====================================================================");
        log.debug("构建REDISSON");
        log.debug("=====================================================================");

        // 创建并返回 Redisson 客户端
        return Redisson.create(config);
    }
}
