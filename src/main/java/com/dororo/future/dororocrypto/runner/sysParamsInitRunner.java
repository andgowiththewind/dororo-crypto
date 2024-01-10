package com.dororo.future.dororocrypto.runner;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.constant.ComConstants;
import com.dororo.future.dororocrypto.enums.CryptoStatusEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RedisCache redisCache;
    @Value("${server.port}")
    private Integer port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setCryptoStatusOptions();
        setEncryptedPrefix();
        setWebsocketUriPrefix();
    }

    private void setWebsocketUriPrefix() {
        redisCache.setCacheMapValue(CacheConstants.SYS_PARAM_MAP, CacheConstants.SysParamHKey.WEBSOCKET_URI_PREFIX, StrUtil.format("ws://localhost:{}", port));
    }

    private void setEncryptedPrefix() {
        redisCache.setCacheMapValue(CacheConstants.SYS_PARAM_MAP, CacheConstants.SysParamHKey.ENCRYPTED_PREFIX, ComConstants.ENCRYPTED_PREFIX);
    }

    private void setCryptoStatusOptions() {
        List<JSONObject> options = Arrays.stream(CryptoStatusEnum.values())
                .map(statusEnum -> JSONUtil.createObj().putOpt("code", statusEnum.getCode()).putOpt("name", statusEnum.getName()))
                .collect(Collectors.toList());
        redisCache.setCacheMapValue(CacheConstants.SYS_PARAM_MAP, CacheConstants.SysParamHKey.CRYPTO_STATUS_OPTIONS, options);
    }
}
