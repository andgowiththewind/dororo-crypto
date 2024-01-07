package com.dororo.future.dororocrypto.components;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import com.dororo.future.dororocrypto.config.redis.RedisDataSourceType;
import com.dororo.future.dororocrypto.config.redis.RedisSelect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务 test
 *
 * @author Dororo
 * @date 2024-01-07 13:10
 */
@Service
public class DistributedLockService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RedissonClient redissonClient;


    @RedisSelect(value = RedisDataSourceType.SLAVE) // 切换Redis数据源
    public void executeWithLock(String lockKey) {
        // 线程名称
        String threadName = Thread.currentThread().getName();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            TimeInterval timer = DateUtil.timer();
            Console.log("线程[{}]开始尝试获取锁", threadName);
            // 尝试获取锁，最多等待3秒，锁自动释放时间为10秒

            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    Console.log("线程[{}]成功获取锁,耗时:[{}ms]", threadName, timer.intervalMs());
                    // 业务逻辑

                    // 模拟在Redisson加锁期间,业务操作切换的Redis数据源,测试最终Redisson是否能正确释放锁
                    redisCache.getCacheObject("test");

                    // 模拟业务操作需要5秒,锁自动释放时间10秒的情况下,是安全的
                    boolean sleep = ThreadUtil.sleep(12000);

                    System.out.println(sleep);

                } finally {
                    lock.unlock(); // 确保释放锁
                    Console.log("线程[{}]成功释放锁", threadName);
                }
            } else {
                Console.log("线程[{}]未获取锁XXXXXXXX,耗时:[{}ms]", threadName, timer.intervalMs());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
