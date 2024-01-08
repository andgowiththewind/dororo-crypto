package com.dororo.future.dororocrypto.dto;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 花木兰
 *
 * @author Dororo
 * @date 2024-01-08 05:12 代表一个文件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Blossom implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    private String id;
    /**
     * 绝对路径
     */
    private String absPath;
    /**
     * 文件名
     */
    private String name;
    /**
     * 扩展名
     */
    private String extName;

    /**
     * 状态码
     *
     * @see com.dororo.future.dororocrypto.enums.StatusEnum
     */
    private Integer status;
    /**
     * 最近消息
     */
    private String message;
    /**
     * 文件加解密进度百分百
     */
    private Integer percentage;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 可读性文件大小
     */
    private String readableFileSize;
    /**
     * 更新时间
     */
    private DateTime gmtUpdate;
}
