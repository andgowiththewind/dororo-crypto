package com.dororo.future.dororocrypto.config.cryptoLog;

import cn.hutool.core.lang.Console;
import com.dororo.future.dororocrypto.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 加解密日志切面
 *
 * @author Dororo
 * @date 2024-01-09 14:42
 */
@Slf4j
@Aspect
@Component
public class CryptoLogAspect {
    @Pointcut("@annotation(com.dororo.future.dororocrypto.config.cryptoLog.CryptoLog)")
    public void logPointcut() {
        // 定义切点
    }

    @Before("logPointcut() && @annotation(cryptoLogQuotation)")
    public void logAfter(JoinPoint joinPoint, CryptoLog cryptoLogQuotation) {
        // 异步执行且处理未知异常
        runAsyncExceptionally(() -> missionStatLog(joinPoint, cryptoLogQuotation));
    }

    /**
     * 任务统计
     *
     * @date 2024-1-9 15:33 如果此注解标注在特定的方法上,则在对应的统计上+1
     */
    private void missionStatLog(JoinPoint joinPoint, CryptoLog cryptoLogQuotation) {
        boolean classMatch = CryptoService.class.getSimpleName().equals(joinPoint.getSignature().getDeclaringType().getSimpleName());
        boolean methodMatch = "cryptoSubmitSync".equals(joinPoint.getSignature().getName());
        if (classMatch && methodMatch) {
            // TODO 类名和方法名都匹配正确,说明调用的是我们期望的统计方法
            Console.error("TODO 类名和方法名都匹配正确,说明调用的是我们期望的统计方法");
        }
    }

    private void runAsyncExceptionally(Runnable runnable) {
        CompletableFuture.runAsync(runnable).exceptionally(e -> {
            if (e != null) {
                log.error("需要注意的异常信息", e);
            }
            return null;
        });
    }


    /**
     * 记录任务收集结果
     *
     * @param result
     */
    @AfterReturning(pointcut = "execution(* com.dororo.future.dororocrypto.service.CryptoService.cryptoSubmitSync(..))", returning = "result")
    public void logAfterReturning(Object result) {
        System.out.println("Method returned value is : " + result);
    }
}