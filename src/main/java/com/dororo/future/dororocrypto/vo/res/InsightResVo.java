package com.dororo.future.dororocrypto.vo.res;

import com.dororo.future.dororocrypto.dto.Blossom;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 请求文件夹文件列表条件分页查询响应封装
 *
 * @author Dororo
 * @date 2024-01-08 05:23
 */
@Data
@Builder
public class InsightResVo {
    private List<Blossom> list;
    private Long total;
}
