package com.dororo.future.dororocrypto.config.redis;

/**
 * 动态REDIS数据源上下文
 *
 * @author Dororo
 * @date 2023-11-23 15:36
 */
public class DynamicRedisDataSourceContextHolder {
    // 使用ThreadLocal维护变量,变量为每个使用该变量的线程提供独立的变量副本,所以每一个线程都可以独立地改变自己的副本,而不会影响其它线程所对应的副本。
    // 当前维护的变量为REDIS连接工厂的标识(比如master/slave等字符串)
    private static final ThreadLocal<String> REDIS_CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setRedisDataSourceType(String dsType) {
        REDIS_CONTEXT_HOLDER.set(dsType);
    }

    public static String getRedisDataSourceType() {
        return REDIS_CONTEXT_HOLDER.get();
    }

    public static void removeRedisDataSourceType() {
        REDIS_CONTEXT_HOLDER.remove();
    }
}
