package com.dororo.future.dororocrypto.config.redis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Map;
import java.util.Optional;

/**
 * 动态REDIS数据源
 * <p>此类实现了`InitializingBean`和`DisposableBean`接口,这两个接口的作用是在Spring容器加载bean的时候,会回调`InitializingBean`接口的`afterPropertiesSet`方法,当Spring容器关闭的时候,会回调`DisposableBean`接口的`destroy`方法。</p>
 * <p>此类实现了`RedisConnectionFactory`和`ReactiveRedisConnectionFactory`接口,这两个接口的作用是提供REDIS连接工厂,用于创建REDIS连接。</p>
 * <p>[设计如此]-当Spring尝试获取REDIS连接时,会检查`RedisConnectionFactory`或`ReactiveRedisConnectionFactory`接口的实现类,并调用`getConnection`或`getReactiveConnection`方法,从而获取REDIS连接。</p>
 * <p>因此我们的动态REDIS数据源改造就以此为突破口;</p>
 *
 * @author Dororo
 * @date 2023-11-23 15:08
 */
@AllArgsConstructor
public class DynamicRedisDataSource implements InitializingBean, DisposableBean, RedisConnectionFactory, ReactiveRedisConnectionFactory {
    // Logger
    private static final Logger log = LoggerFactory.getLogger(DynamicRedisDataSource.class);

    /**
     * 设置一个map属性记录所有的REDIS连接工厂,其中key为REDIS连接工厂的标识(比如master/slave等字符串),值为REDIS连接工厂对象
     */
    private final Map<String, LettuceConnectionFactory> connectionFactoryMap;

    /**
     * InitializingBean接口的实现方法
     * <p>在Spring框架中,InitializingBean接口用于在bean的属性被设置后以及Spring容器中的其他bean被初始化之前,执行自定义的初始化逻辑。这是Spring提供的一种方式,用于在bean完全创建好之后,但在它被放入使用前,执行一些操作。</p>
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Optional.ofNullable(connectionFactoryMap).filter(CollectionUtil::isNotEmpty).ifPresent(map -> map.values().forEach(LettuceConnectionFactory::afterPropertiesSet));
    }

    /**
     * DisposableBean接口的实现方法
     * <p>
     * 当你的bean实现了DisposableBean接口,它需要实现destroy方法。Spring容器在关闭时,会自动调用这个方法。这意味着你可以在这个方法中放置任何清理代码,例如：
     * 关闭网络连接。
     * 释放数据库连接。
     * 清理临时文件。
     * 停止后台线程。
     * </p>
     */
    @Override
    public void destroy() throws Exception {
        Optional.ofNullable(connectionFactoryMap).filter(CollectionUtil::isNotEmpty).ifPresent(map -> map.values().forEach(LettuceConnectionFactory::destroy));
    }

    // =============================================================================实现`RedisConnectionFactory`接口需要实现的方法[start]=============================================================================
    @Override
    public RedisConnection getConnection() {
        return getCurrentRealTimeLettuceConnectionFactory().getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return getCurrentRealTimeLettuceConnectionFactory().getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return getCurrentRealTimeLettuceConnectionFactory().getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return getCurrentRealTimeLettuceConnectionFactory().getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return getCurrentRealTimeLettuceConnectionFactory().translateExceptionIfPossible(ex);
    }

    // =============================================================================实现`RedisConnectionFactory`接口需要实现的方法[end  ]=============================================================================


    // =============================================================================实现`ReactiveRedisConnectionFactory`接口需要实现的方法[start]=============================================================================
    @Override
    public ReactiveRedisConnection getReactiveConnection() {
        return getCurrentRealTimeLettuceConnectionFactory().getReactiveConnection();
    }

    @Override
    public ReactiveRedisClusterConnection getReactiveClusterConnection() {
        return getCurrentRealTimeLettuceConnectionFactory().getReactiveClusterConnection();
    }
    // =============================================================================实现`ReactiveRedisConnectionFactory`接口需要实现的方法[end  ]=============================================================================


    /**
     * TODO 关键方法:我方自定义定制地切换REDIS连接工厂
     */
    public LettuceConnectionFactory getCurrentRealTimeLettuceConnectionFactory() {
        // 获取当前线程的REDIS数据源类型(ThreadLocal维护)
        String type = DynamicRedisDataSourceContextHolder.getRedisDataSourceType();

        // 如果当前线程的REDIS数据源类型为空,则返回默认的REDIS连接工厂
        if (StrUtil.isBlank(type)) {
            type = RedisDataSourceType.MASTER.name();
        }

        log.debug("切换REDIS[{}]数据源", type);
        return getIgnoreCase(type);
    }


    /**
     * 忽略大小写地获取
     * <p>因为在切面时填充的KEY是enum枚举类的name,但是yml中配置的key不一定是大写</p>
     */
    private LettuceConnectionFactory getIgnoreCase(String key) {
        for (Map.Entry<String, LettuceConnectionFactory> entry : connectionFactoryMap.entrySet()) {
            if (StrUtil.equalsIgnoreCase(entry.getKey(), key)) {
                return entry.getValue();
            }
        }
        return null;
    }


}
