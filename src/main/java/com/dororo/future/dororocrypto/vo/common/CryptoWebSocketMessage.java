package com.dororo.future.dororocrypto.vo.common;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Dororo
 * @date 2024-01-10 15:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CryptoWebSocketMessage {
    @Builder.Default
    private Integer code = 200;
    private String type;
    private Object data;

    @Getter
    @AllArgsConstructor
    public enum TypeEnum {
        // 更新表格数据
        TABLE_DATA_UPDATE("tableDataUpdate");
        /**
         * 类型名称
         */
        private String name;
    }

}
