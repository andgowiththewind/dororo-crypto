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
    public static enum TypeEnum {
        // 请求表格行数据
        GET_TABLE_ROW("getTableRow"),
        // 更新表格行数据
        TABLE_ROW_UPDATE("tableRowUpdate");

        /**
         * 类型名称
         */
        private String name;
    }

}
