package com.dororo.future.dororocrypto.config.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * REDIS数据源类型
 *
 * <p>当前项目设计上,与`application-redis.yml`定义的配置一一对应;</p>
 *
 * @author Dororo
 * @date 2023-11-23 15:43
 */
@Getter
@AllArgsConstructor
public enum RedisDataSourceType {
    MASTER,
    SLAVE,
}
