package com.dororo.future.dororocrypto.vo.req;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 加解密请求参数封装
 *
 * @author Dororo
 * @date 2024-01-08 23:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CryptoReqVo {
    /**
     * 文件或者目录路径集合
     */
    private List<String> pathList;
    /**
     * 路径类型,如果是文件,pathList传递的路径认为是文件路径,;如果是目录,pathList传递的路径认为是目录路径
     *
     * @see PathTypeEnum
     */
    private String PathType;
    /**
     * 本次请求是否要求加密: true:加密;false:解密
     */
    private Boolean askEncrypt;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 缓冲区大小
     */
    private Long bufferSize;


    @Getter
    @AllArgsConstructor
    public static enum PathTypeEnum {
        DIRECTORY("directory"),
        FILE("file");
        private String name;
    }
}
