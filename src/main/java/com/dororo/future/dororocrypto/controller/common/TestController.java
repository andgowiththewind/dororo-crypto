package com.dororo.future.dororocrypto.controller.common;

import cn.hutool.core.date.DateUtil;
import com.dororo.future.dororocrypto.components.DistributedLockService;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 测试控制器
 *
 * @author Dororo
 * @date 2023-12-27 00:18
 */
@RestController
@RequestMapping("/open/test")
public class TestController {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DistributedLockService distributedLockService;

    @GetMapping("/test")
    public BaseMvcResponse test() {
        CompletableFuture.runAsync(() -> distributedLockService.executeWithLock("testDistributedLock"));
        return BaseMvcResponse.success();
    }
}
