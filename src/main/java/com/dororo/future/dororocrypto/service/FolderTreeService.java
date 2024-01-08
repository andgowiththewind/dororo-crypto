package com.dororo.future.dororocrypto.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.dto.FolderNode;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.service.common.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Dororo
 * @date 2024-01-07 19:00
 */
@Service
public class FolderTreeService extends BaseService {
    @Autowired
    private RedisCache redisCache;

    public List<FolderNode> getTreeData(String path) {
        // 合法性校验
        checkPath(path);
        // 结果集
        List<FolderNode> result = new ArrayList<>();

        // 树顶级节点ID
        int topId = 0;
        // 线程安全地操作原子类,实现ID自增
        AtomicInteger atomicId = new AtomicInteger(1);
        // 树顶级节点
        FolderNode topNode = FolderNode.builder().id(atomicId.get()).parentId(topId).name(FileUtil.getName(path)).absPath(path).open(true).build();
        result.add(topNode);
        // 递归收集子节点
        recursivelyCollect(topNode, result, atomicId);

        // 返回结果
        return result;
    }


    private void checkPath(String path) {
        try {
            Assert.notBlank(path, "路径不能为空");
            Assert.isTrue(FileUtil.exist(FileUtil.file(path)), "路径不存在");
            Assert.isTrue(FileUtil.isDirectory(path), "路径不是目录");
        } catch (Exception e) {
            throw new CryptoBusinessException(e.getMessage());
        }
        // 合法的情况下,将当前目录缓存到REDIS,先查出
        List<String> beforeList = redisCache.getCacheMapValue(CacheConstants.SYS_PARAM_MAP, CacheConstants.SysParamHKey.TOP_DIRECTORY_PATH_OPTIONS);
        beforeList = Optional.ofNullable(beforeList).orElse(new ArrayList<>());
        // 去重
        Set<String> collect = beforeList.stream().collect(Collectors.toSet());
        collect.add(path);
        List<String> afterList = collect.stream().map(String::trim).collect(Collectors.toList());
        redisCache.setCacheMapValue(CacheConstants.SYS_PARAM_MAP, CacheConstants.SysParamHKey.TOP_DIRECTORY_PATH_OPTIONS, afterList);
    }

    private void recursivelyCollect(FolderNode fatherNode, List<FolderNode> result, AtomicInteger atomicId) {
        // 判断是否有子目录
        List<File> sonFolders = Arrays.stream(FileUtil.ls(fatherNode.getAbsPath())).filter(FileUtil::isDirectory).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(sonFolders)) {
            for (File sonFolder : sonFolders) {
                int id = atomicId.incrementAndGet();
                FolderNode sonNode = FolderNode.builder().id(id).parentId(fatherNode.getId()).name(sonFolder.getName()).absPath(sonFolder.getPath()).build();
                result.add(sonNode);
                recursivelyCollect(sonNode, result, atomicId);// 递归
            }
        }
    }
}
