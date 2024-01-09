package com.dororo.future.dororocrypto.service.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.util.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * @author Dororo
 * @date 2024-01-07 19:15
 */
@Service
public class BaseService {
    @Autowired
    private RedisCache redisCache;

    /**
     * 根据绝对路径获取全局ID
     *
     * @param absPath 绝对路径
     * @return 经过SHA-256摘要算法后的全局ID,同样的绝对路径得到的摘要算法结果一致
     */
    public static String getIdByPath(String absPath) {
        Assert.notBlank(absPath);
        // 统一转正斜杠"/"
        absPath = PathUtils.leftJoin(absPath);
        // 使用比MD5更强更严谨的`SHA-256`摘要算法,将文件绝对路径转换为唯一ID
        String sha256Hex = DigestUtil.sha256Hex(absPath);
        return sha256Hex;
    }

    /**
     * 简单防抖
     */
    protected void simpleAntiShake(String antiShakeKey) {
        Assert.notBlank(antiShakeKey);
        String cacheObject = redisCache.getCacheObject(antiShakeKey);
        if (cacheObject != null) {
            // 说明处于防抖期间
            throw new CryptoBusinessException("请求过于频繁,请稍后再试");
        } else {
            // 说明不处于防抖期间
            redisCache.setCacheObject(antiShakeKey, "[提交加解密]接口：正在防抖", 1, TimeUnit.SECONDS);
        }
    }


    /**
     * 重复执行,直到成功
     *
     * <p>在指定的时间间隔内每隔N毫秒执行一次业务操作,业务操作如果抛出异常将被捕获并忽略,只关注时间截止之前最终是否执行成功;</p>
     * <p>但凡执行成功,则调用成功回调;</p>
     * <p>如果时间截止之前一直未执行成功,则调用失败回调;</p>
     *
     * @param intervalMs 间隔毫秒
     * @param maxMs      最大毫秒
     * @param actionCr   执行动作,如果业务上认为执行失败需要主动抛出异常
     * @param successCr  成功回调
     * @param errorCr    失败回调
     */
    protected void executeRepeatedlyWithFinalCheck(long intervalMs, long maxMs, Consumer<Void> actionCr, Consumer<Void> successCr, Consumer<Void> errorCr) {
        TimeInterval timer = DateUtil.timer();
        boolean finalSuccess = false;
        while (NumberUtil.compare(timer.intervalMs(), maxMs) <= 0) {
            try {
                actionCr.accept(null);
                finalSuccess = true;
                break;
            } catch (Exception e) {
                // Ignore
            }
            ThreadUtil.sleep(intervalMs);
        }
        if (finalSuccess) {
            Optional.ofNullable(successCr).filter(Objects::nonNull).ifPresent(consumer -> consumer.accept(null));
        } else {
            Optional.ofNullable(errorCr).filter(Objects::nonNull).ifPresent(consumer -> consumer.accept(null));
        }
    }
}
