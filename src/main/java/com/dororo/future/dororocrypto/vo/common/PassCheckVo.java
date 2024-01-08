package com.dororo.future.dororocrypto.vo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 请输入类描述
 *
 * @author Dororo
 * @date 2024-01-09 02:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PassCheckVo {
    // 密码经过摘要算法加密之后的KEY字符串
    private String sha256Key;
    // 上次校验结果
    private Boolean check;
    // 如果校验失败,记录提示信息
    private String errorMessage;
}
