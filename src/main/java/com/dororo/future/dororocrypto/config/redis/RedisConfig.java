package com.dororo.future.dororocrypto.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * REDIS配置类
 * <p>当前配置类的主要作用是:
 * (1)注入一个自定义的动态REDIS数据源Bean;
 * (2)
 * </p>
 *
 * @author Dororo
 * @date 2023-11-23 16:13
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    // TODO 动态REDIS数据源
    @Bean
    public DynamicRedisDataSource dynamicRedisDataSource(DynamicRedisProperties dynamicRedisProperties) {
        log.info("DynamicRedisDataSource init ...");
        // 根据yaml配置的REDIS数据源属性,创建对应的REDIS连接工厂,并收集到一个map中
        Map<String, LettuceConnectionFactory> connectionFactoryMap = new HashMap<>();
        dynamicRedisProperties.getPropertiesMap().forEach((key, value) -> connectionFactoryMap.put(key, getFactoryByYmlProps(value)));
        // 将收集好的连接工程map传入自定义的动态REDIS数据源中,返回一个动态REDIS数据源对象,注入到Spring容器中,Spring在尝试连接REDIS时,会从该数据源中获取连接工厂
        return new DynamicRedisDataSource(connectionFactoryMap);
    }

    private LettuceConnectionFactory getFactoryByYmlProps(DynamicRedisProperties.PropertyDTO redisProperties) {
        return new LettuceConnectionFactory(getRedisConfig(redisProperties), getClientConfig(redisProperties));
    }

    private RedisStandaloneConfiguration getRedisConfig(DynamicRedisProperties.PropertyDTO redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setDatabase(redisProperties.getDatabase());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return config;
    }

    private LettuceClientConfiguration getClientConfig(DynamicRedisProperties.PropertyDTO redisProperties) {
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .poolConfig(getPoolConfig(redisProperties))
                .build();
        return clientConfig;
    }

    private GenericObjectPoolConfig getPoolConfig(DynamicRedisProperties.PropertyDTO redisProperties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        config.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        config.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        config.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait());
        return config;
    }
}
