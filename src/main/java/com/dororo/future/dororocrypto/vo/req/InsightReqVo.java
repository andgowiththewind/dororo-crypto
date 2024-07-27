package com.dororo.future.dororocrypto.vo.req;

import com.dororo.future.dororocrypto.dto.Blossom;
import com.dororo.future.dororocrypto.enums.CryptoStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Retention;

/**
 * 请求文件夹文件列表条件分页查询前端传参封装
 *
 * @author Dororo
 * @date 2024-01-08 05:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsightReqVo {
    private Blossom model;
    private PageDTO page;
    private ParamDTO params;

    @Data
    public static class PageDTO {
        private Integer pageNum;
        private Integer pageSize;
    }

    @Data
    public static class ParamDTO {
        private String folderPath;
        /**
         * @see CryptoStatusEnum
         */
        private Integer cryptoStatus;
    }
}
