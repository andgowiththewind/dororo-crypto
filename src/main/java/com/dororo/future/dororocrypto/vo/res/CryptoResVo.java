package com.dororo.future.dororocrypto.vo.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 请输入类描述
 *
 * @author Dororo
 * @date 2024-01-09 00:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CryptoResVo {
    private String message;
}
