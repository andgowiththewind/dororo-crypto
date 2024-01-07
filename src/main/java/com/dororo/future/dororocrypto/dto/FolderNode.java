package com.dororo.future.dororocrypto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 目录树节点
 *
 * @author Dororo
 * @date 2024-01-07 21:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderNode {
    private Integer id;
    private Integer parentId;// 禁止使用`pId`
    private String name;
    private String absPath;
    @Builder.Default
    private Boolean open = false;
}
