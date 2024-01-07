package com.dororo.future.dororocrypto.controller;

import cn.hutool.core.lang.Assert;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.service.SysParamService;
import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统参数控制器
 *
 * @author Dororo
 * @date 2024-01-07 05:00
 */
@RestController
@RequestMapping("/sys/")
public class SysParamController {
    @Autowired
    private SysParamService sysParamService;

    @GetMapping("/getSysParam")
    public BaseMvcResponse getSysParam(@RequestParam("key") String key) {
        Assert.notBlank(key, "key不能为空");
        return BaseMvcResponse.successData(sysParamService.getSysParam(key));
    }
}
