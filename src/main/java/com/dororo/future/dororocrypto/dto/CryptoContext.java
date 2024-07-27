package com.dororo.future.dororocrypto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * 加解密流程上下文信息
 *
 * @author Dororo
 * @date 2024-01-09 17:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CryptoContext {
    /**
     * 加解密类型：加密/解密
     */
    private Boolean askEncrypt;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 整数盐值
     */
    private Integer intSalt;
    /**
     * 缓冲区大小设置
     */
    private Integer bufferSize;
    /**
     * 执行加解密的原文件路径
     */
    private String beforePath;
    /**
     * 执行加解密的临时文件路径
     */
    private String tmpPath;
    /**
     * 执行加解密的目标文件路径
     */
    private String afterPath;
    /**
     * 输入流:被加解密的文件读取流
     */
    private BufferedInputStream bis;
    /**
     * 输出流:临时文件写入流
     */
    private BufferedOutputStream bos;
}