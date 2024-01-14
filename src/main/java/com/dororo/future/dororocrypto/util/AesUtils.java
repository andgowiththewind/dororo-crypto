package com.dororo.future.dororocrypto.util;

import cn.hutool.crypto.symmetric.AES;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;

/**
 * 秘钥工具类
 *
 * @author Dororo
 * @date 2024-01-09 18:38 处理任意长度密码转秘钥相关逻辑
 */
public class AesUtils {
    /**
     * 明文盐值:由于当前系统业务场景下,不要求用户同时输入盐值(只需要用户输入密码,避免记忆繁琐),所以这里在系统内部使用固定盐值
     */
    private final static String explicitSaltString = "aaa1234567890bbb";

    public static AES getAes(String userPassword) {
        try {
            // 1.0 秘钥派生函数,将用户输入的任意长度的密码转换为固定长度的秘钥
            byte[] salt = explicitSaltString.getBytes();
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(userPassword.toCharArray(), salt, 65536, 256);// 65536是迭代次数,256是期望生成的密钥长度
            SecretKey secretKey = factory.generateSecret(spec);
            // 2.0 生成AES加密算法
            byte[] secretKeyBytes = secretKey.getEncoded();
            // 偏移向量同样使用显示盐值
            return new AES("CBC", "PKCS7Padding", secretKeyBytes, salt);
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }
}
