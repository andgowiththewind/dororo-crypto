package com.dororo.future.dororocrypto.config.redis;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * RedisSelect对应的切面类
 * 经测试,不优先使用`@Around`的方式;
 *
 * @author Dororo
 * @date 2023-11-23 17:48
 */
@Aspect
@Order(1)
@Component
public class RedisSelectAspect {

    /**
     * 设计上:只关注`service`上的`@RedisSelect`注解
     * 如果检测到`@RedisSelect`注解,则使用注解中的值,否则不作处理;不在当前配置文件兜底默认值;
     */
    @Before("execution(* com.dororo.future.dororocrypto.service.*.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisSelect redisSelect = AnnotationUtils.findAnnotation(method, RedisSelect.class);
        if (redisSelect == null) {
            Class<?> declaringClass = method.getDeclaringClass();
            redisSelect = declaringClass.getAnnotation(RedisSelect.class);
        }
        if (redisSelect != null) {
            DynamicRedisDataSourceContextHolder.setRedisDataSourceType(redisSelect.value().name());
        }
    }

    /**
     * 设计上`controller`作为`service`最外层,所以在`controller`层清除`ThreadLocal`中的值,防止内存泄漏
     */
    @After("execution(* com.dororo.future.dororocrypto.controller.*.*(..))")
    public void afterAdvice(JoinPoint joinPoint) {
        DynamicRedisDataSourceContextHolder.removeRedisDataSourceType();
    }
}
