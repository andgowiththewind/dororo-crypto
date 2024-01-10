package com.dororo.future.dororocrypto.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dororo.future.dororocrypto.components.RedisSlaveCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.constant.ThreadPoolConstants;
import com.dororo.future.dororocrypto.dto.Blossom;
import com.dororo.future.dororocrypto.enums.StatusEnum;
import com.dororo.future.dororocrypto.vo.common.CryptoWebSocketMessage;
import com.dororo.future.dororocrypto.websocket.CryptoWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 统计或者表格数据高频请求
 *
 * @author Dororo
 * @date 2024-01-10 22:01
 */
@Slf4j
@Service
public class StatService {
    @Autowired
    private RedisSlaveCache redisSlaveCache;
    @Autowired
    @Qualifier(ThreadPoolConstants.STAT)
    private ThreadPoolTaskExecutor statTaskExecutor;

    public void onMessage(String sessionId, String message) {
        log.debug("[CRYPTO WEBSOCKET]-收到消息:[ID={}],[消息={}]", sessionId, message);

        CryptoWebSocketMessage convert = null;
        try {
            convert = Convert.convert(CryptoWebSocketMessage.class, JSONUtil.parseObj(message));
        } catch (ConvertException e) {
            // ignore
        }

        if (convert == null || StrUtil.isBlank(convert.getType())) {
            log.warn("[CRYPTO WEBSOCKET]-消息格式错误:[ID={}],[消息={}]", sessionId, message);
            return;
        }
        // 使用专门的线程池处理消息处理并推送
        CryptoWebSocketMessage finalConvert = convert;
        CompletableFuture.runAsync(() -> {
            // 如果是请求表格数据
            if (StrUtil.equalsIgnoreCase(finalConvert.getType(), CryptoWebSocketMessage.TypeEnum.TABLE_DATA_UPDATE.getName())) {
                tableDataUpdate(sessionId, finalConvert);
            }
        }, statTaskExecutor).exceptionally((e) -> {
            // 异常处理:设计上只关注未知异常,已知异常需要在consumer中自行处理
            if (e != null) {
                String msg = "[LEVEL=STAT]统计线程池未知异常";
                log.error(msg, e);
            }
            // ignore
            return null;
        });
    }

    private void tableDataUpdate(String sessionId, CryptoWebSocketMessage reqVo) {
        // 约定为预览表格的idList
        List<String> idList = (List<String>) reqVo.getData();
        // 找出缓存中的数据
        List<Blossom> insightTableData = idList.stream().map(id -> {
            Blossom cacheMapValue = redisSlaveCache.getCacheMapValue(CacheConstants.BLOSSOM_MAP, id);
            return cacheMapValue;
        }).filter(Objects::nonNull).collect(Collectors.toList());


        // 进度表格的数据
        List<Blossom> processTableData = new ArrayList<>();
        Map<String, Blossom> blossomMap = redisSlaveCache.getCacheMap(CacheConstants.BLOSSOM_MAP);
        for (Map.Entry<String, Blossom> entry : blossomMap.entrySet()) {
            Blossom value = entry.getValue();
            if (value != null && value.getStatus() != null) {
                // 按照状态筛选
                if (ListUtil.toList(StatusEnum.OUTPUTTING.getCode(), StatusEnum.ALMOST.getCode()).contains(value.getStatus())) {
                    processTableData.add(value);
                }
            }
        }

        JSONObject dataPart = JSONUtil.createObj().putOpt("insightTableData", insightTableData).putOpt("processTableData", processTableData);
        CryptoWebSocketMessage resVo = CryptoWebSocketMessage.builder()
                .type(CryptoWebSocketMessage.TypeEnum.TABLE_DATA_UPDATE.getName())
                .data(dataPart)
                .build();
        String msg = JSONUtil.toJsonStr(resVo);

        CryptoWebSocket.sendMessage(sessionId, msg);
    }
}