package com.dororo.future.dororocrypto.controller;

import com.dororo.future.dororocrypto.constant.ThreadPoolConstants;
import com.dororo.future.dororocrypto.controller.common.BaseController;
import com.dororo.future.dororocrypto.service.CryptoService;
import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import com.dororo.future.dororocrypto.vo.req.CryptoReqVo;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Dororo
 * @date 2024-01-09 00:37
 */
@Slf4j
@RestController
@RequestMapping("/crypto")
public class CryptoController extends BaseController {
    @Autowired
    private CryptoService cryptoService;

    @PostMapping("/cryptoSubmit")
    public BaseMvcResponse cryptoSubmit(@RequestBody CryptoReqVo cryptoReqVo) {
        // 后端对任务进行防抖处理
        cryptoService.missionSimpleAntiShake(cryptoReqVo);
        // 异步执行任务,含未知异常处理
        super.runAsyncExceptionally((v) -> cryptoService.cryptoSubmitSync(cryptoReqVo));
        // 非阻塞
        return BaseMvcResponse.success("任务已提交");
    }
}
