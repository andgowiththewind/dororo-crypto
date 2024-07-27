package com.dororo.future.dororocrypto.controller.common;

import cn.hutool.core.util.StrUtil;
import com.dororo.future.dororocrypto.constant.ThreadPoolConstants;
import com.dororo.future.dororocrypto.controller.CryptoController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Dororo
 * @date 2024-01-09 12:57
 */
@Slf4j
@RestController
public class BaseController {
    @Autowired
    @Qualifier(ThreadPoolConstants.DISPATCH)
    private ThreadPoolTaskExecutor dispatchTaskExecutor;

    protected <T> void runAsyncExceptionally(Consumer<T> consumer) {
        // 子类控制器名称
        String controllerName = this.getClass().getSimpleName();
        // 子类控制器方法名称
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        CompletableFuture<Void> future = null;
        if (StrUtil.equals(controllerName, CryptoController.class.getSimpleName())) {
            // 如果是特定的控制器,则使用特定的线程池
            future = CompletableFuture.runAsync(() -> consumer.accept(null), dispatchTaskExecutor);
        } else {
            // 否则不使用特定线程池
            future = CompletableFuture.runAsync(() -> consumer.accept(null));
        }

        future.exceptionally((e) -> {
            // 异常处理:设计上只关注未知异常,已知异常需要再consumer中自行处理
            if (e != null) {
                String msg = StrUtil.format("[LEVEL=CONTROLLER]{}#{}()未知异常", controllerName, methodName);
                log.error(msg, e);
            }

            // ignore
            return null;
        });
    }
}
