package com.dororo.future.dororocrypto.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.dororo.future.dororocrypto.constant.ComConstants;
import com.dororo.future.dororocrypto.dto.Blossom;
import com.dororo.future.dororocrypto.enums.CryptoStatusEnum;
import com.dororo.future.dororocrypto.enums.StatusEnum;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.vo.req.InsightReqVo;
import com.dororo.future.dororocrypto.vo.res.InsightResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表格数据服务
 *
 * @author Dororo
 * @date 2024-01-08 05:02
 */
@Slf4j
@Service
public class TableDataService {
    @Autowired
    private BlossomCacheService blossomCacheService;


    public InsightResVo conditionPagingQuery(InsightReqVo reqVo) {
        // 校验
        validate(reqVo);
        // 只处理条件查询不分页
        List<File> all = handleConditional(reqVo);
        // 处理分页
        List<File> filter = handlePaging(reqVo, all);
        // File转Blossom全部信息来自缓存
        List<Blossom> blossoms = filter.stream().map(f -> blossomCacheService.lockToGetOrDefault(FileUtil.getAbsolutePath(f))).collect(Collectors.toList());
        // 封装
        InsightResVo resVo = InsightResVo.builder().list(blossoms).total(Convert.toLong(all.size())).build();

        return resVo;
    }

    private List<File> handleConditional(InsightReqVo reqVo) {
        TimeInterval timer = DateUtil.timer();
        List<File> matchList = FileUtil.loopFiles(reqVo.getParams().getFolderPath()).stream().filter(file -> {
            String name = FileUtil.getName(file);
            String extName = FileUtil.extName(file);
            String absPath = FileUtil.getAbsolutePath(file);
            // 排除文件夹
            if (FileUtil.isDirectory(file)) {
                return false;
            }
            // 排除自定义临时文件类型
            if (StrUtil.isNotBlank(extName) && StrUtil.equalsIgnoreCase(ComConstants.TMP_EXT_NAME, extName)) {
                return false;
            }
            // 根据文件名判断是否曾经由当前系统加密
            boolean encryptedByThis = StrUtil.isNotBlank(name) && StrUtil.equalsIgnoreCase(name, ComConstants.ENCRYPTED_PREFIX);
            Integer cryptoStatus = reqVo.getParams().getCryptoStatus();
            if (NumberUtil.compare(cryptoStatus, CryptoStatusEnum.ALL.getCode()) == 0) {
                // 用户期望全选,无需过滤
            } else if (NumberUtil.compare(cryptoStatus, CryptoStatusEnum.ENCRYPTED.getCode()) == 0) {
                // 用户期望仅查看加密文件
                if (!encryptedByThis) {
                    return false;
                }
            } else if (NumberUtil.compare(cryptoStatus, CryptoStatusEnum.DECRYPTED.getCode()) == 0) {
                // 用户期望仅查看尚未加密文件
                if (encryptedByThis) {
                    return false;
                }
            }
            // 模拟mybatis,根据对象的各个属性进行筛选
            if (reqVo.getModel() != null) {
                if (StrUtil.isNotBlank(reqVo.getModel().getName())) {
                    // 用户输入了文件名期望模糊查询
                    if (!StrUtil.containsIgnoreCase(name, reqVo.getModel().getName())) {
                        return false;
                    }
                }
                if (StrUtil.isNotBlank(reqVo.getModel().getAbsPath())) {
                    // 用户输入了绝对路径期望模糊查询
                    if (!StrUtil.containsIgnoreCase(absPath, reqVo.getModel().getAbsPath())) {
                        return false;
                    }
                }
                // 其他字段...
            }


            // TODO 结合缓存中记录的文件加解密过程状态,进行筛选
            Blossom blossom = blossomCacheService.lockToGetOrDefault(absPath);
            ArrayList<Integer> codeWhiteList = ListUtil.toList(
                    StatusEnum.FREE.getCode(),
                    StatusEnum.WAITING.getCode(),
                    StatusEnum.OUTPUTTING.getCode(),
                    StatusEnum.ALMOST.getCode()
            );
            if (!codeWhiteList.contains(blossom.getStatus())) {
                return false;
            }


            return true;
        }).collect(Collectors.toList());

        log.debug("耗时[{}ms]获取总文件数[{}]", timer.intervalMs(), matchList.size());
        return matchList;
    }

    private List<File> handlePaging(InsightReqVo reqVo, List<File> all) {
        if (CollectionUtil.isEmpty(all)) {
            return new ArrayList<>();
        }
        Integer pageNum = reqVo.getPage().getPageNum();
        Integer pageSize = reqVo.getPage().getPageSize();
        // 禁止使用 List.subList(start, end)
        List<File> after = new ArrayList<>();
        int[] startEnd = PageUtil.transToStartEnd(pageNum - 1, pageSize);
        for (int i = 0; i < all.size(); i++) {
            if (i >= startEnd[0] && i < startEnd[1]) {
                after.add(all.get(i));
            }
        }

        return after;
    }

    private void validate(InsightReqVo reqVo) {
        try {
            String folderPath = reqVo.getParams().getFolderPath();
            Assert.notBlank(folderPath, "文件目录不能为空");
            Assert.isTrue(FileUtil.exist(folderPath), "文件目录不存在");
            Assert.isTrue(FileUtil.isDirectory(folderPath), "文件目录必须是文件夹");

            Integer cryptoStatus = reqVo.getParams().getCryptoStatus();
            Assert.notNull(cryptoStatus, "加解密状态类型不能为空");
            Assert.isTrue(CryptoStatusEnum.valueList().contains(cryptoStatus), "加解密状态类型[{}]不存在", cryptoStatus);

        } catch (IllegalArgumentException e) {
            throw new CryptoBusinessException(e.getMessage());
        }
    }
}
