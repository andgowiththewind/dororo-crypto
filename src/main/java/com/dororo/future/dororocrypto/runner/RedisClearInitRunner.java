package com.dororo.future.dororocrypto.runner;

import cn.hutool.core.collection.ListUtil;
import com.dororo.future.dororocrypto.components.RedisMasterCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REDIS清理初始化
 *
 * @author Dororo
 * @date 2024-01-11 14:48
 */
@Slf4j
@Component
public class RedisClearInitRunner implements ApplicationRunner {
    private final RedisMasterCache redisMasterCache;

    public RedisClearInitRunner(RedisMasterCache redisMasterCache) {
        this.redisMasterCache = redisMasterCache;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        action();
    }

    /**
     * 根据业务设计,一些KEY需要再系统启动之际清理
     */
    private void action() {
        // (1)按照完全匹配进行删除
        deleteByExactMatch();
        // (2)按照前缀匹配进行删除
        deleteByPrefixMatch();
    }

    private void deleteByExactMatch() {
        List<String> exactList = ListUtil.toList(
                CacheConstants.BLOSSOM_MAP
                , CacheConstants.SYS_PARAM_MAP
        );
        exactList.forEach(key -> {
            redisMasterCache.deleteObject(key);
            log.debug("根据[KEY={}]删除缓存,规则:完全匹配", key);
        });
    }

    private void deleteByPrefixMatch() {
        List<String> prefixList = ListUtil.toList(
                CacheConstants.PREFIX_LOCK_BLOSSOM
                , CacheConstants.PREFIX_LOCK_AFTER_NAME_GENERATE
                , CacheConstants.MISSION_ANTI_SHAKE
        );
        prefixList.forEach(prefix -> {
            Collection<String> keys = redisMasterCache.keys(prefix + "*");
            if (keys != null && keys.size() > 0) {
                keys.forEach(key -> {
                    redisMasterCache.deleteObject(key);
                    log.debug("根据[KEY={}]删除缓存,规则:前缀匹配", key);
                });
            }
        });
    }

}
