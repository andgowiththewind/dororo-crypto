package com.dororo.future.dororocrypto.config.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记切换REDIS的注解
 *
 * @author Dororo
 * @date 2023-11-23 17:45
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisSelect {
    public RedisDataSourceType value() default RedisDataSourceType.MASTER;
}
