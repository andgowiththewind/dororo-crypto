package com.dororo.future.dororocrypto.controller;

import com.dororo.future.dororocrypto.service.TableDataService;
import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import com.dororo.future.dororocrypto.vo.req.InsightReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 表格数据控制器
 *
 * @author Dororo
 * @date 2024-01-08 05:01
 */
@RestController
@RequestMapping("/tableData")
public class TableDataController {
    @Autowired
    private TableDataService tableDataService;

    @PostMapping("/getInsightTableData")
    public BaseMvcResponse conditionPagingQuery(@RequestBody InsightReqVo reqVo) {
        return BaseMvcResponse.successData(tableDataService.conditionPagingQuery(reqVo));
    }

}
