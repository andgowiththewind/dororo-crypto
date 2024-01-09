package com.dororo.future.dororocrypto.config.cryptoLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 加解密日志注解
 *
 * @author Dororo
 * @date 2024-01-09 14:31
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface CryptoLog {
    String value() default "";
}
