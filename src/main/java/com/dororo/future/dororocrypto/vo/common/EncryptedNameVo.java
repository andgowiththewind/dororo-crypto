package com.dororo.future.dororocrypto.vo.common;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.dororo.future.dororocrypto.constant.ComConstants;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.util.AesUtils;
import com.dororo.future.dororocrypto.util.PathUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 已加密文件名各部分信息
 *
 * <p>$前缀$密码摘要算法密文$盐对称加密密文$原文件名</p>
 *
 * @author Dororo
 * @date 2024-01-10 02:29 已加密文件名,格式参考(分隔符`$`): `固定识别前缀`+`密码摘要算法密文`+`整数盐对称加密密文`+`原文件名(如果存在)`+`原扩展名(如果存在)`
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EncryptedNameVo {
    /**
     * 原文件名
     */
    private String sourceName;
    /**
     * 已加密并按照固定格式拼接之后的文件名
     */
    private String encryptedName;
    /**
     * 已加密密码
     */
    private String encryptedPassword;
    /**
     * 解密后密码
     */
    private String password;

    /**
     * 已加密盐
     */
    private String encryptedSalt;
    /**
     * 解密后盐
     */
    private Integer salt;

    /**
     * 根据已加密文件名解析出各部分信息
     *
     * @param encryptedName 已加密文件名,格式参考(分隔符`$`): `固定识别前缀`+`密码摘要算法密文`+`整数盐对称加密密文`+`原文件名(如果存在)`+`原扩展名(如果存在)`
     * @param password      用户输入的密码
     * @return 已加密文件名各部分信息
     */
    public static EncryptedNameVo analyse(String encryptedName, String password) {
        Assert.notBlank(encryptedName);

        // 参考 parts = [, #SAFEBYWIND#, e8c64d15a388af348e3661cdc4e43203981cc6dd, 5340a7e142fa744cf75426bd74016490, 测试.txt]
        List<String> parts = StrUtil.split(encryptedName, ComConstants.DOLLAR);

        Assert.isTrue(parts.size() >= 5, "文件名格式不正确,无法解密 (SIZE_LESS_THAN_FIVE)");

        // 密码摘要算法不分
        String encryptedPassword = parts.get(2);
        Assert.notBlank(encryptedPassword);

        // 将用户本次输入的密码进行同样的摘要算法,与文件名中的摘要算法密文进行比对
        Assert.isTrue(StrUtil.equals(DigestUtil.sha256Hex(password), encryptedPassword), "用户输入的密码摘要算法值与文件名记录的不一致,不能使用当前密码解密");

        // 密码校验通过则将盐值对称解密出来
        String encryptedSalt = parts.get(3);
        Assert.notBlank(encryptedSalt);

        Integer salt = null;
        try {
            // 对称解密
            String saltStr = AesUtils.getAes(password).decryptStr(encryptedSalt);
            salt = Convert.toInt(saltStr, null);
        } catch (Exception e) {
            // ignore
        }
        if (salt == null) {
            throw new CryptoBusinessException("用户输入的密码无法解密出盐值(文件名可能被篡改)");
        }

        // 加密文件中第4个$符号之后的内容为原文件名
        String sourceName = StrUtil.subSuf(encryptedName, StrUtil.ordinalIndexOf(encryptedName, ComConstants.DOLLAR, 4) + 1);


        // 如果都解析成功,则返回
        return EncryptedNameVo.builder()
                .sourceName(sourceName)
                .encryptedName(encryptedName)
                .encryptedPassword(encryptedPassword)
                .password(password)
                .encryptedSalt(encryptedSalt)
                .salt(salt)
                .build();
    }

    /**
     * 拼装
     */
    public static String concat(EncryptedNameVo encryptedNameVo) {
        Assert.notBlank(encryptedNameVo.getEncryptedPassword());
        Assert.notBlank(encryptedNameVo.getEncryptedSalt());
        Assert.notBlank(encryptedNameVo.getSourceName());

        return StrUtil.format("{}{}${}${}",
                ComConstants.ENCRYPTED_PREFIX,
                encryptedNameVo.getEncryptedPassword(),
                encryptedNameVo.getEncryptedSalt(),
                encryptedNameVo.getSourceName()
        );
    }
}
