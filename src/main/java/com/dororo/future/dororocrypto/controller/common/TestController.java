package com.dororo.future.dororocrypto.controller.common;

import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author Dororo
 * @date 2023-12-27 00:18
 */
@RestController
@RequestMapping("/open/test")
public class TestController {

    @GetMapping("/test")
    public BaseMvcResponse test() {
        return BaseMvcResponse.success();
    }
}
