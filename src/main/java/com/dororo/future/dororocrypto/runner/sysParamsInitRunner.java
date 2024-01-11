package com.dororo.future.dororocrypto.runner;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dororo.future.dororocrypto.components.RedisMasterCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.constant.ComConstants;
import com.dororo.future.dororocrypto.enums.CryptoStatusEnum;
import com.dororo.future.dororocrypto.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统参数初始化
 *
 * @author Dororo
 * @date 2024-01-08 00:55
 */
@Slf4j
@Component
public class sysParamsInitRunner implements ApplicationRunner {
    @Value("${server.port}")
    private Integer port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setCryptoStatusOptions();
        setEncryptedPrefix();
        setWebsocketUriPrefix();
    }

    private void setWebsocketUriPrefix() {
        String key = CacheConstants.SysParamHKey.WEBSOCKET_URI_PREFIX;
        String value = StrUtil.format("ws://localhost:{}", port);
        SysParamService.setSysParam(key, value);
    }

    private void setEncryptedPrefix() {
        String key = CacheConstants.SysParamHKey.ENCRYPTED_PREFIX;
        String value = ComConstants.ENCRYPTED_PREFIX;
        SysParamService.setSysParam(key, value);
    }

    private void setCryptoStatusOptions() {
        List<JSONObject> options = Arrays.stream(CryptoStatusEnum.values())
                .map(statusEnum -> JSONUtil.createObj().putOpt("code", statusEnum.getCode()).putOpt("name", statusEnum.getName()))
                .collect(Collectors.toList());
        SysParamService.setSysParam(CacheConstants.SysParamHKey.CRYPTO_STATUS_OPTIONS, options);
    }
}