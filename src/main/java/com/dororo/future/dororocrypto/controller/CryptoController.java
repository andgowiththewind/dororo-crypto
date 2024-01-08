package com.dororo.future.dororocrypto.controller;

import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import com.dororo.future.dororocrypto.vo.req.CryptoReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dororo
 * @date 2024-01-09 00:37
 */
@Slf4j
@RestController
@RequestMapping("/crypto")
public class CryptoController {

    @PostMapping("/cryptoSubmit")
    public BaseMvcResponse cryptoSubmit(@RequestBody CryptoReqVo cryptoReqVo) {
        log.info("cryptoSubmit() called with parameters => [cryptoReqVo = {}]", cryptoReqVo);
        return BaseMvcResponse.success();
    }
}
