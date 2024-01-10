package com.dororo.future.dororocrypto.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.constant.ComConstants;
import com.dororo.future.dororocrypto.dto.Blossom;
import com.dororo.future.dororocrypto.dto.CryptoContext;
import com.dororo.future.dororocrypto.enums.StatusEnum;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.service.common.BaseService;
import com.dororo.future.dororocrypto.util.AesUtils;
import com.dororo.future.dororocrypto.util.PathUtils;
import com.dororo.future.dororocrypto.vo.common.CryptoWebSocketMessage;
import com.dororo.future.dororocrypto.vo.common.EncryptedNameVo;
import com.dororo.future.dororocrypto.websocket.CryptoWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 请输入类描述
 *
 * @author Dororo
 * @date 2024-01-09 18:06
 */
@Slf4j
@Service
public class CryptoHelperService extends BaseService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private BlossomCacheService blossomCacheService;


    protected void validateBeforeCrypto(CryptoContext cryptoContext) {
        String beforePath = cryptoContext.getBeforePath();
        Assert.notBlank(beforePath);
        Assert.isTrue(FileUtil.exist(beforePath), "文件已经不存在,加解密任务终止");
        Assert.isTrue(FileUtil.size(FileUtil.file(beforePath)) > 0, "文件大小为零,加解密任务终止");
        Assert.isTrue(FileUtil.isFile(beforePath), "不是文件,加解密任务终止");

        // 实时查出缓存信息
        Blossom blossom = blossomCacheService.lockToGetOrDefault(cryptoContext.getBeforePath());

        // 状态要求FREE
        Assert.isTrue(blossom.getStatus() == StatusEnum.FREE.getCode(), "文件不是空闲状态,已有其他任务在处理,当前加解密任务终止");

        Boolean askEncrypt = cryptoContext.getAskEncrypt();
        Assert.notNull(askEncrypt);

        // 根据文件名前缀判断此文件是否曾经有当前系统进行过加密
        boolean isEncrypted = StrUtil.startWithIgnoreCase(FileUtil.getName(beforePath), ComConstants.ENCRYPTED_PREFIX);
        if (askEncrypt) {
            // 要求加密
            Assert.isFalse(isEncrypted, "文件已经加密,当前加解密任务终止");
        } else {
            // 要求解密
            Assert.isTrue(isEncrypted, "文件未加密,当前加解密任务终止");
        }
    }

    /**
     * 更新缓存并发布
     *
     * @param source 记录需要被更新字段的对象
     */
    protected void updateCacheAndPublish(Blossom source) {
        // 绝对路径涉及ID的获取,不能为空
        Assert.notBlank(source.getAbsPath());
        // 实时查出缓存中的记录
        Blossom target = blossomCacheService.lockToGetOrDefault(source.getAbsPath());
        // 基于`BeanUtil.copyProperties`方法复制对象属性,实现类似于JavaScript中`Object.assign`的功
        BeanUtil.copyProperties(source, target, CopyOptions.create().setIgnoreNullValue(true));
        // 更新缓存
        redisCache.setCacheMapValue(CacheConstants.BLOSSOM_MAP, target.getId(), target);

        // TODO 发布消息
        CryptoWebSocketMessage message = CryptoWebSocketMessage.builder()
                .type(CryptoWebSocketMessage.TypeEnum.TABLE_ROW_UPDATE.getName())
                .data(target)
                .build();
        CompletableFuture.runAsync(() -> CryptoWebSocket.broadcast(JSONUtil.toJsonStr(message)));
    }

    /**
     * 定义临时文件,并登记注册入缓存中
     */
    protected void registerTmpBlossom(CryptoContext cryptoContext) {
        String beforePath = cryptoContext.getBeforePath();

        // 拼接临时文件名
        String tmpName = concatTmpName(beforePath);

        // 在源文件的同级目录下创建临时文件
        String parent = FileUtil.getParent(beforePath, 1);
        String tmpAbsPath = PathUtils.leftJoin(parent, tmpName);

        TimeInterval timer = DateUtil.timer();
        while (!notExistAndNotRegister(tmpAbsPath)) {
            long max = 5000L;
            if (timer.intervalMs() > max) {
                throw new CryptoBusinessException(StrUtil.format("临时文件超时[{}ms]创建失败,请稍后重试", max));
            }
            tmpName = concatTmpName(beforePath);
            tmpAbsPath = PathUtils.leftJoin(parent, tmpName);
            ThreadUtil.sleep(500L);
        }

        // 临时文件路径生成成功,登记入缓存
        Blossom tmpBlossom = blossomCacheService.lockToGetOrDefault(tmpAbsPath);
        // 更新状态信息
        updateCacheAndPublish(Blossom.builder().absPath(tmpAbsPath).status(StatusEnum.INPUTTING.getCode()).gmtUpdate(DateUtil.date()).build());

        // 记录上下文
        cryptoContext.setTmpPath(tmpAbsPath);

    }

    /**
     * 定义加密后文件,并登记注册入缓存中
     *
     * @param cryptoContext 加解密上下文
     */
    protected void registerAfterBlossom(CryptoContext cryptoContext) {
        String afterName = null;
        String afterPath = null;
        if (cryptoContext.getAskEncrypt()) {
            // 如果是加密,拼接加密后的文件名
            afterName = EncryptedNameVo.concat(EncryptedNameVo.builder()
                    // 密码摘要算法密文
                    .encryptedPassword(DigestUtil.sha256Hex(cryptoContext.getUserPassword()))
                    // 整数盐对称加密密文
                    .encryptedSalt(AesUtils.getAes(cryptoContext.getUserPassword()).encryptHex(Convert.toStr(cryptoContext.getIntSalt(), null)))
                    // 原文件名
                    .sourceName(FileUtil.getName(cryptoContext.getBeforePath()))
                    .build()
            );

            // WIN系统对文件名长度有要求
            Assert.isTrue(afterName.length() < 255, "加密后的文件名长度超过255,本次加密任务终止,请手动改名,稍后重试");

            // 目标文件要求文件不存在且缓存中没有登记
            afterPath = PathUtils.leftJoin(FileUtil.getParent(cryptoContext.getBeforePath(), 1), afterName);
            Assert.isTrue(notExistAndNotRegister(afterPath), "加密后的文件已经存在,本次加密任务终止,请稍后重试");
            // 登记入缓存
            Blossom afterBlossom = blossomCacheService.lockToGetOrDefault(afterPath);
            // 更新状态信息
            updateCacheAndPublish(Blossom.builder().absPath(afterPath).status(StatusEnum.INPUTTING.getCode()).gmtUpdate(DateUtil.date()).build());
            // 及时记录上下文
            cryptoContext.setAfterPath(afterPath);
        } else {
            // 如果是解密,需要截取源文件名,然后判断是否存在同名文件或者在缓存中被登记(假设多个加密文件解压缩后的文件名都一样),如果存在,需要重命名
            EncryptedNameVo nameVo = EncryptedNameVo.analyse(FileUtil.getName(cryptoContext.getBeforePath()), cryptoContext.getUserPassword());
            afterName = nameVo.getSourceName();
            afterPath = PathUtils.leftJoin(FileUtil.getParent(cryptoContext.getBeforePath(), 1), afterName);

            TimeInterval timer = DateUtil.timer();
            while (!notExistAndNotRegister(afterPath)) {
                if (NumberUtil.compare(timer.intervalMs(), 5000L) >= 0) {
                    throw new CryptoBusinessException(StrUtil.format("解密后的文件超时[{}ms]创建失败,请稍后重试", 5000L));
                }
                // 说明文件已经存在,需要重命名,增加一些随机数,需要考虑源文件名是否有扩展名的情况
                afterName = StrUtil.format("{}_{}{}{}"
                        , FileUtil.mainName(nameVo.getSourceName())
                        , RandomUtil.randomInt(1, 3000)
                        , StrUtil.isNotBlank(FileUtil.extName(nameVo.getSourceName())) ? "." : ""
                        , FileUtil.extName(nameVo.getSourceName())
                );
                afterPath = PathUtils.leftJoin(FileUtil.getParent(cryptoContext.getBeforePath(), 1), afterName);
            }

            // 登记入缓存
            Blossom afterBlossom = blossomCacheService.lockToGetOrDefault(afterPath);
            // 更新状态信息
            updateCacheAndPublish(Blossom.builder().absPath(afterPath).status(StatusEnum.INPUTTING.getCode()).gmtUpdate(DateUtil.date()).build());
            // 及时记录上下文
            cryptoContext.setAfterPath(afterPath);
        }
    }

    /**
     * 判断一个文件是否不存在且缓存中没有登记
     *
     * @param absPath 绝对路径
     * @return true:不存在且缓存中没有登记;false:存在或者缓存中已经登记
     */
    private boolean notExistAndNotRegister(String absPath) {
        if (!FileUtil.exist(absPath)) {
            // 说明文件不存在
            String idByPath = getIdByPath(absPath);
            Blossom cacheMapValue = redisCache.getCacheMapValue(CacheConstants.BLOSSOM_MAP, idByPath);
            if (cacheMapValue == null) {
                // 说明缓存中没有登记
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接临时文件名
     *
     * @param beforePath 源文件绝对路径
     */
    private String concatTmpName(String beforePath) {
        return StrUtil.format("{}_{}_{}.{}"
                , "0000000000"
                , getIdByPath(beforePath)
                , DateUtil.format(DateUtil.date(), "yyyyMMdd_HHmmss")
                , ComConstants.TMP_EXT_NAME
        );
    }

    /**
     * 定义加解密操作对应输入输出流
     *
     * @param cryptoContext 加解密上下文
     */
    protected void registerCryptoStream(CryptoContext cryptoContext) {
        String beforePath = cryptoContext.getBeforePath();
        String tmpPath = cryptoContext.getTmpPath();
        Assert.notBlank(beforePath);
        Assert.notBlank(tmpPath);

        // 准备输入输出流
        BufferedInputStream bis = FileUtil.getInputStream(FileUtil.file(beforePath));
        BufferedOutputStream bos = FileUtil.getOutputStream(FileUtil.file(tmpPath));

        // 登记上下文
        cryptoContext.setBis(bis);
        cryptoContext.setBos(bos);
    }

    /**
     * 注册盐值,如果是解密,然后校验密码
     *
     * @param cryptoContext 加解密上下文
     */
    protected void registerSalt(CryptoContext cryptoContext) {
        if (cryptoContext.getAskEncrypt()) {
            // 要求加密,生成盐值
            cryptoContext.setIntSalt(RandomUtil.randomInt(1, 3000));
            // 无需校验密码
        } else {
            // 如何当前正在解密,(1)从文件名中截取盐值的对称加密字符串不分,并解密得到盐值;(2)校验密码
            EncryptedNameVo nameVo = EncryptedNameVo.analyse(FileUtil.getName(cryptoContext.getBeforePath()), cryptoContext.getUserPassword());
            cryptoContext.setIntSalt(nameVo.getSalt());
        }
    }

    /**
     * 核心IO流加解密
     *
     * @param cryptoContext 加解密上下文
     */
    protected void coreIoCrypto(CryptoContext cryptoContext) {
        // 缓冲区大小
        Integer bufferSize = Optional.ofNullable(cryptoContext.getBufferSize()).filter(size -> size != null && size > 0).orElse(ComConstants.DEFAULT_BUFFER_SIZE);
        // 周期性计算百分比后的消费者
        BiConsumer<CryptoContext, Integer> percentageConsumer = getPercentageConsumer();

        try {
            // 每次实际读取到字节数
            int len;
            // 已经读取的字节数
            long total = 0;
            // 缓冲区
            byte[] buffer = new byte[bufferSize];
            // 整数盐
            Integer intSalt = cryptoContext.getIntSalt();
            // 计时器,控制打印频率
            TimeInterval timer = DateUtil.timer();
            // 源文件大小,用于计算百分比
            long beforeSize = FileUtil.size(FileUtil.file(cryptoContext.getBeforePath()));

            // 读取源文件的输入流,写入到临时文件的输出流
            while ((len = cryptoContext.getBis().read(buffer)) != -1) {
                // 此处已经将字节读取到缓冲区,不能在此缓冲区中直接修改,应该用一个新的字节数组来接收加盐后的字节,定义临时缓冲区副本
                byte[] newBuffer = new byte[len];
                // 对每个字节加减整数盐后重新收集:(1)加密时,增加整数盐;(2)解密时,减去整数盐
                for (int i = 0; i < len; i++) {
                    newBuffer[i] = (byte) (buffer[i] + (cryptoContext.getAskEncrypt() ? intSalt : -intSalt));
                }
                // 加盐后的字节写入到临时文件的输出流
                cryptoContext.getBos().write(newBuffer);
                // 更新已经读取的字节数
                total += len;

                // 流读取的频率是非常快的,如果每次都更新缓存和发布消息,会导致卡死,所以需要通过间隔时间控制频率
                if (timer.intervalMs() > 1000L) {
                    // 计算当前百分比
                    Integer percentage = Convert.toInt(StrUtil.replaceLast(NumberUtil.formatPercent(NumberUtil.div(total, beforeSize, 4), 0), "%", ""));
                    // 消费
                    percentageConsumer.accept(cryptoContext, percentage);
                    // 重置计时器,重新计时直至下一次周期
                    timer.restart();
                }
            }
            // 防止最后一次结果丢失,循环结束后指定百分比更新一次
            percentageConsumer.accept(cryptoContext, 100);

            // 清除计时器
            timer.clear();
            timer = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IoUtil.close(cryptoContext.getBis());
            IoUtil.close(cryptoContext.getBos());
        }
    }

    /**
     * 处理加解密过程中每隔N秒计算一次百分比的消费者,需要更新缓存、发布消息、更新前端进度条等操作,一些操作可以再开异步线程处理
     */
    private BiConsumer<CryptoContext, Integer> getPercentageConsumer() {
        return (cryptoContext, percentage) -> {
            // TODO
            Console.error("[{}]当前百分比:{}", FileUtil.getName(cryptoContext.getBeforePath()), percentage);
        };
    }


    /**
     * 处理临时文件改名并拷贝到源文件同目录下,生成最终目标文件
     * <p>如果是加密,由于随机整数盐的存在,基本不用考虑重名问题;</p>
     * <p>如果是解密,可能目录下已经有同名文件,此时需要重命名</p>
     * <p>经测试即便是关闭了IO流仍然可能存在文件占用导致改名失败的问题,解决方案是在指定的时间内多次尝试;删除文件同理;</p>
     */
    protected void tmpToAfter(CryptoContext cryptoContext) {
        // 注册最终文件信息
        registerAfterBlossom(cryptoContext);
        // 处理临时文件改名
        handleTmpRenameToAfter(cryptoContext);
        // 处理源文件与临时文件删除
        handleSourceAndTmpDelete(cryptoContext);
        // 最后开放新生成的文件
        handleAfterOpen(cryptoContext);
    }

    private void handleTmpRenameToAfter(CryptoContext cryptoContext) {
        long intervalMs = 2000L;
        long maxMs = 60_000L;

        // 业务执行
        Consumer<Void> actionCr = aVoid -> {
            IoUtil.close(cryptoContext.getBis());
            IoUtil.close(cryptoContext.getBos());
            File tmpFile = FileUtil.file(cryptoContext.getTmpPath());
            String afterName = FileUtil.getName(cryptoContext.getAfterPath());
            FileUtil.rename(tmpFile, afterName, false, true);
            if (!FileUtil.exist(cryptoContext.getAfterPath())) {
                // 说明改名失败,抛出异常触发进入下一循环
                throw new CryptoBusinessException();
            }
        };
        // 成功回调
        Consumer<Void> successCr = aVoid -> {
            // 改名成功说明临时文件已经不存在,最终文件生成成功
            updateCacheAndPublish(Blossom.builder().absPath(cryptoContext.getBeforePath()).status(StatusEnum.ALMOST.getCode()).message(StatusEnum.ALMOST.getMessage()).build());
            updateCacheAndPublish(Blossom.builder().absPath(cryptoContext.getTmpPath()).status(StatusEnum.ALMOST.getCode()).message(StatusEnum.ALMOST.getMessage()).build());
            updateCacheAndPublish(Blossom.builder().absPath(cryptoContext.getAfterPath()).status(StatusEnum.ALMOST.getCode()).message(StatusEnum.ALMOST.getMessage()).build());
        };
        // 失败回调
        Consumer<Void> errorCr = aVoid -> {
            throw new CryptoBusinessException("临时文件(超时)改名失败,请稍后重试");
        };
        // 执行
        super.executeRepeatedlyWithFinalCheck(intervalMs, maxMs, actionCr, successCr, errorCr);
    }

    /**
     * 处理源文件与临时文件删除
     */
    private void handleSourceAndTmpDelete(CryptoContext cryptoContext) {
        String msgPiece = cryptoContext.getAskEncrypt() ? "加密" : "解密";

        // 源文件与临时文件都需要删除,注意顺序,安全起见最后才能删除源文件
        LinkedList<String> pathList = ListUtil.toLinkedList(cryptoContext.getTmpPath(), cryptoContext.getBeforePath());

        for (String path : pathList) {
            long intervalMs = 2000L;
            long maxMs = 60_000L;

            // 业务执行
            Consumer<Void> actionCr = aVoid -> {
                FileUtil.del(FileUtil.file(path));
                if (FileUtil.exist(path)) {
                    // 说明删除失败,抛出异常触发进入下一循环
                    throw new CryptoBusinessException();
                }
            };
            // 成功回调
            Consumer<Void> successCr = aVoid -> {
                // 删除成功
                updateCacheAndPublish(Blossom.builder().absPath(path).status(StatusEnum.ABSENT.getCode()).message(StrUtil.format("{}成功,文件已删除", msgPiece)).build());
            };
            // 失败回调
            Consumer<Void> errorCr = aVoid -> {
                throw new CryptoBusinessException("文件(超时)删除失败,请稍后重试");
            };
            // 执行
            super.executeRepeatedlyWithFinalCheck(intervalMs, maxMs, actionCr, successCr, errorCr);
        }

    }

    private void handleAfterOpen(CryptoContext cryptoContext) {
        long size = FileUtil.size(FileUtil.file(cryptoContext.getAfterPath()));

        String msgPiece = cryptoContext.getAskEncrypt() ? "加密" : "解密";
        updateCacheAndPublish(Blossom
                .builder()
                .absPath(cryptoContext.getAfterPath())
                .status(StatusEnum.FREE.getCode())
                .size(size)
                .readableFileSize(FileUtil.readableFileSize(size))
                .message(StrUtil.format("{}成功,文件已生成", msgPiece))
                .gmtUpdate(DateUtil.date())
                .build());
    }

    /**
     * 全局回滚
     * <p>加解密全流程异常捕获</p>
     *
     * @param cryptoContext 加解密上下文
     * @param throwable     异常
     */
    protected void globalRollback(CryptoContext cryptoContext, Throwable throwable) {
        if (cryptoContext == null) {
            return;
        }

        // 删除可能存在的临时文件以及最终文件
        List<String> pathList = ListUtil.toLinkedList(cryptoContext.getTmpPath(), cryptoContext.getAfterPath());
        // 文件可能不存在,因为无法确定异常发生在哪个阶段
        pathList = pathList.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        for (String path : pathList) {
            long intervalMs = 2000L;
            long maxMs = 60_000L;
            // 业务执行
            Consumer<Void> actionCr = aVoid -> {
                IoUtil.close(cryptoContext.getBis());
                IoUtil.close(cryptoContext.getBos());
                FileUtil.del(FileUtil.file(path));
                if (FileUtil.exist(path)) {
                    // 说明删除失败,抛出异常触发进入下一循环
                    throw new CryptoBusinessException();
                }
            };
            // 成功回调
            Consumer<Void> successCr = aVoid -> {
                // 删除成功
                updateCacheAndPublish(Blossom.builder().absPath(path).status(StatusEnum.ABSENT.getCode()).message("[$##$]-文件已删除").build());
            };
            // 失败回调
            Consumer<Void> errorCr = aVoid -> {
                throw new CryptoBusinessException("[$##$]-文件(超时)删除失败");
            };
            // 执行
            super.executeRepeatedlyWithFinalCheck(intervalMs, maxMs, actionCr, successCr, errorCr);
        }

        // 重新开放源文件
        updateCacheAndPublish(Blossom
                .builder()
                .absPath(cryptoContext.getBeforePath())
                .status(FileUtil.exist(FileUtil.file(cryptoContext.getBeforePath())) ? StatusEnum.FREE.getCode() : StatusEnum.ABSENT.getCode())
                .message("[$##$]-文件已重新开放")
                .build());

        // 如果是未知异常,需要特殊打印
        if (throwable instanceof CryptoBusinessException) {
            // 说明是已知异常,只需要更新缓存和发布消息,不需要日志打印
        } else {
            // 说明是未知异常,需要特殊打印引起关注
            log.error("加解密过程[{}]发生未知异常", Thread.currentThread().getName(), throwable);
        }
    }
}
