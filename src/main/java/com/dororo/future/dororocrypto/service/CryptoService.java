package com.dororo.future.dororocrypto.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.dororo.future.dororocrypto.config.cryptoLog.CryptoLog;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.constant.ThreadPoolConstants;
import com.dororo.future.dororocrypto.dto.Blossom;
import com.dororo.future.dororocrypto.dto.CryptoContext;
import com.dororo.future.dororocrypto.enums.StatusEnum;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.vo.req.CryptoReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 加解密服务
 *
 * @author Dororo
 * @date 2024-01-09 04:26
 */
@Slf4j
@Service
public class CryptoService extends CryptoHelperService {
    @Autowired
    @Qualifier(ThreadPoolConstants.DISPATCH)
    private ThreadPoolTaskExecutor dispatchTaskExecutor;

    @Autowired
    @Qualifier(ThreadPoolConstants.CRYPTO)
    private ThreadPoolTaskExecutor cryptoTaskExecutor;
    @Autowired
    private BlossomCacheService blossomCacheService;

    /**
     * 处理加解密提交
     */
    @CryptoLog("统计加解密提交")
    public String cryptoSubmitSync(CryptoReqVo cryptoReqVo) {
        // 期望处理的文件集合
        List<Blossom> expectList = getBlossomList(cryptoReqVo);

        // 仅空闲状态的文件才能被加解密
        List<String> absPathList = expectList.stream().filter(blossom -> StatusEnum.get(blossom.getStatus()).equals(StatusEnum.FREE))
                .map(Blossom::getAbsPath)
                .collect(Collectors.toList());

        // 循环提交任务到线程池
        for (String absPath : absPathList) {
            // 定义全局上下文,此上下文对象会在各个阶段传递,异步线程采用thenRunAsync,确保按顺序执行
            CryptoContext cryptoContext = CryptoContext.builder()
                    .beforePath(absPath)
                    .askEncrypt(cryptoReqVo.getAskEncrypt())
                    .userPassword(cryptoReqVo.getUserPassword())
                    .bufferSize(cryptoReqVo.getBufferSize())
                    .build();
            CompletableFuture<Void> future01 = CompletableFuture.runAsync(() -> handlePrepareAsync(cryptoContext), dispatchTaskExecutor);
            // 3.2.2 加解密阶段,独立线程
            CompletableFuture<Void> future02 = future01.thenRunAsync(() -> handleCryptoAsync(cryptoContext), cryptoTaskExecutor);
            // 3.2.3 异常处理,涉及前置多个阶段,处理业务上文件系统回滚
            future02.exceptionally(captureUnknownExceptions(cryptoContext));
        }


        // TODO 任务统计
        return null;
    }

    private void handlePrepareAsync(CryptoContext cryptoContext) {
        try {
            // 校验参数
            super.validateBeforeCrypto(cryptoContext);

            // 满足条件,将状态更新为排队中
            super.updateCacheAndPublish(Blossom.builder()
                    .absPath(cryptoContext.getBeforePath())
                    // 更新为排队中
                    .status(StatusEnum.WAITING.getCode())
                    .message(StatusEnum.WAITING.getMessage())
                    .gmtUpdate(DateUtil.date())
                    .build());

        } catch (IllegalArgumentException e) {
            throw new CryptoBusinessException(e.getMessage());
        }
    }

    /**
     * 加解密阶段
     */
    private void handleCryptoAsync(CryptoContext cryptoContext) {
        // 更新状态
        super.updateCacheAndPublish(Blossom.builder()
                .absPath(cryptoContext.getBeforePath())
                // 更新为加解密中
                .status(StatusEnum.OUTPUTTING.getCode())
                .message(StrUtil.format("成功进入工作线程,正在启动{}", cryptoContext.getAskEncrypt() ? "加密" : "解密"))
                .gmtUpdate(DateUtil.date())
                .build());

        // 定义临时文件,注册入缓存中,记录上下文
        super.registerTmpBlossom(cryptoContext);

        // 定义加解密操作对应输入输出流
        super.registerCryptoStream(cryptoContext);

        // 注册盐值
        super.registerSalt(cryptoContext);

        // 核心IO流加解密
        super.coreIoCrypto(cryptoContext);

        // 处理临时文件改名为最终目标文件相关操作
        super.tmpToAfter(cryptoContext);
    }

    private Function<Throwable, Void> captureUnknownExceptions(CryptoContext cryptoContext) {
        return throwable -> {
            if (throwable == null) {
                return null;
            }

            try {
                // 业务异常处理
                super.globalRollback(cryptoContext, throwable);
            } catch (Exception e) {
                // 如果在处理业务异常过程中,又发生了未知异常,那么就是极其严重的异常,需要打印日志,及时修复
                Console.error("======================================================================================");
                log.error("重点异常:请保留日志并联系开发人员", e);
                Console.error("======================================================================================");
                throw new RuntimeException(e);
            }

            // 线程返回值,当前设计上与业务无关。ignore
            return null;
        };
    }

    private List<Blossom> getBlossomList(CryptoReqVo cryptoReqVo) {
        List<Blossom> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(cryptoReqVo.getPathList())) {
            return new ArrayList<>();
        }
        try {
            Assert.notBlank(cryptoReqVo.getPathType(), "路径类型不能为空");
            boolean anyMatch = Arrays.stream(CryptoReqVo.PathTypeEnum.values()).anyMatch(anEnum -> StrUtil.equalsIgnoreCase(anEnum.getName(), cryptoReqVo.getPathType()));
            Assert.isTrue(anyMatch, "路径类型不合法");
            Assert.notNull(cryptoReqVo.getAskEncrypt(), "是否加密不能为空");
            Assert.notBlank(cryptoReqVo.getUserPassword(), "密码不能为空");
            //
            if (cryptoReqVo.getPathType().equalsIgnoreCase(CryptoReqVo.PathTypeEnum.FILE.getName())) {
                // 如果是文件类型
                result = cryptoReqVo.getPathList().stream().map(filePath -> blossomCacheService.lockToGetOrDefault(filePath)).collect(Collectors.toList());
            } else {
                // 否则为文件夹地址,收集全部文件夹的全部后代文件
                List<File> all = cryptoReqVo.getPathList().stream()
                        .filter(fp -> FileUtil.exist(fp) && FileUtil.isDirectory(fp))
                        .map(folderPath -> FileUtil.loopFiles(folderPath))
                        .flatMap(files -> files.stream())
                        .collect(Collectors.toList());
                // 筛选并去重,并转为缓存中的Blossom对象
                result = all.stream().filter(f -> FileUtil.exist(f) && FileUtil.isFile(f)).map(File::getAbsolutePath).distinct()
                        .map(absPath -> blossomCacheService.lockToGetOrDefault(absPath))
                        .collect(Collectors.toList());
            }
        } catch (IllegalArgumentException e) {
            throw new CryptoBusinessException(e.getMessage());
        }

        return result;
    }

    /**
     * 简单防抖
     */
    public void missionSimpleAntiShake(CryptoReqVo cryptoReqVo) {
        // reqVo忽略空值后转字符串,然后通过摘要算法计算对应的防抖缓存KEY
        String jsonStr = JSONUtil.toJsonStr(cryptoReqVo, JSONConfig.create().setIgnoreNullValue(true));
        Assert.notBlank(jsonStr, "传参不能为空");
        // 防抖缓存KEY
        String antiShakeKey = StrUtil.format("{}:{}", CacheConstants.MISSION_ANTI_SHAKE, DigestUtil.sha256Hex(jsonStr));
        super.simpleAntiShake(antiShakeKey);
    }
}