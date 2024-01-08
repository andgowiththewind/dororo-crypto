package com.dororo.future.dororocrypto.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.dto.Blossom;
import com.dororo.future.dororocrypto.enums.StatusEnum;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.service.common.BaseService;
import com.dororo.future.dororocrypto.util.PathUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁文件缓存服务类
 *
 * @author Dororo
 * @date 2024-01-08 16:49 分布式锁确保同一个绝对路径的文件、同一时间只允许有一个线程在处理"增删改"操作
 */
@Slf4j
@Service
public class BlossomCacheService extends BaseService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RedissonClient redissonClient;

    public Blossom lockToGetOrDefault(String absPath) {
        // 定义业务操作
        Supplier<Blossom> supplier = supplierGetOrDefault(absPath);
        // 提交业务操作到分布式锁代码结构
        Blossom blossom = lockExecute(absPath, supplier);
        // 返回结果
        return blossom;
    }


    /**
     * 分布式锁执行业务操作
     *
     * @param absPath  绝对路径,相同的绝对路径得到的锁KEY是一样的
     * @param supplier 业务操作
     * @param <T>      业务操作返回值类型
     * @return 业务操作返回值
     */
    @SneakyThrows // `tryLock()`方法抛出`InterruptedException`异常
    public <T> T lockExecute(String absPath, Supplier<T> supplier) {
        T result = null;
        // 获取ID
        String id = getIdByPath(absPath);
        // 拼接分布式锁KEY
        String lockKey = getLockKey(id);
        // 定义锁对象
        RLock lock = redissonClient.getLock(lockKey);
        // 根据当前业务设计的超时时间、过期时间
        if (lock.tryLock(5, 15, TimeUnit.SECONDS)) {
            // 当前线程加锁成功,执行业务操作
            try {
                result = supplier.get();
            } finally {
                // 确保释放锁
                lock.unlock();
            }
        } else {
            // 如果最终获取锁失败,则抛出自定义业务异常
            log.debug("Blossom增删改操作获取锁超时失败,需要关注业务耗时情况");
            throw new CryptoBusinessException("获取锁失败");
        }
        return result;
    }

    // 统一的分布式锁前缀
    public String getLockKey(String id) {
        Assert.notBlank(id);
        return StrUtil.format("{}:{}", CacheConstants.LOCK_BLOSSOM, id);
    }

    /**
     * 期望执行操作:如果缓存中有,直接返回;如果缓存中没有,则新建并返回
     *
     * @param absPath 绝对路径,相同的绝对路径得到的锁KEY是一样的
     */
    public Supplier<Blossom> supplierGetOrDefault(String absPath) {
        Supplier<Blossom> supplier = () -> {
            Blossom cacheVo = redisCache.getCacheMapValue(CacheConstants.BLOSSOM_MAP, getIdByPath(absPath));
            if (cacheVo != null) {
                // 如果缓存中存在对应的文件信息,则直接返回
                return cacheVo;
            } else {
                // 如果缓存中不存在对应的文件信息,则新建并返回
                Blossom insertVo = Blossom.builder()
                        .id(getIdByPath(absPath))
                        .absPath(PathUtils.leftJoin(absPath))
                        .name(FileUtil.getName(absPath))
                        .extName(FileUtil.extName(absPath))
                        .status((FileUtil.exist(FileUtil.file(absPath)) ? StatusEnum.FREE : StatusEnum.ABSENT).getCode())
                        .message("新建")
                        .percentage(null)
                        .size(FileUtil.size(FileUtil.file(absPath)))
                        .readableFileSize(FileUtil.readableFileSize(FileUtil.size(FileUtil.file(absPath))))
                        .gmtUpdate(DateUtil.date())
                        .build();
                redisCache.setCacheMapValue(CacheConstants.BLOSSOM_MAP, getIdByPath(absPath), insertVo);
                return insertVo;
            }
        };


        return supplier;
    }

    /**
     * 期望执行操作:删除
     *
     * @param absPath 绝对路径,相同的绝对路径得到的锁KEY是一样的
     */
    // private static Supplier<Blossom> supplierDelete(String absPath) {
    //     return null;
    // }

    /**
     * 期望执行操作:更新
     *
     * @param absPath 绝对路径,相同的绝对路径得到的锁KEY是一样的
     */
    // private static Supplier<Blossom> supplierUpdate(String absPath) {
    //     return null;
    // }
}