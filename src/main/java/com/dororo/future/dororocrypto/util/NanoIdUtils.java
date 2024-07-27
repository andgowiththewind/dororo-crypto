package com.dororo.future.dororocrypto.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.id.NanoId;

/**
 * nanoId工具类
 *
 * @author Dororo
 * @version 1.0.0
 * @date 2024-01-06 22:38 基于Hutool二开,结合ChatGPT
 */
public class NanoIdUtils {
    // 默认随机字母表,使用URL安全的Base64字符
    private static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    // 仅数字与小写字母表
    private static final char[] NUMERIC_LOWERCASE_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    // 仅数字与大写字母表
    private static final char[] NUMERIC_UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private static final Integer DEFAULT_LENGTH = 21;

    public static String randomLowercaseNanoId() {
        return NanoId.randomNanoId(null, NUMERIC_LOWERCASE_ALPHABET, DEFAULT_LENGTH);
    }

    public static String randomLowercaseNanoId(Integer size) {
        Assert.isTrue(size != null && size > 0, "随机字符串长度必须大于0");
        return NanoId.randomNanoId(null, NUMERIC_LOWERCASE_ALPHABET, size);
    }


    public static String randomUppercaseNanoId() {
        return NanoId.randomNanoId(null, NUMERIC_UPPERCASE_ALPHABET, DEFAULT_LENGTH);
    }

    public static String randomUppercaseNanoId(Integer size) {
        Assert.isTrue(size != null && size > 0, "随机字符串长度必须大于0");
        return NanoId.randomNanoId(null, NUMERIC_UPPERCASE_ALPHABET, size);
    }
}
