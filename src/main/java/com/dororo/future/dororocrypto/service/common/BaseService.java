package com.dororo.future.dororocrypto.service.common;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.DigestUtil;
import com.dororo.future.dororocrypto.util.PathUtils;
import org.springframework.stereotype.Service;


/**
 * @author Dororo
 * @date 2024-01-07 19:15
 */
@Service
public class BaseService {

    /**
     * 根据绝对路径获取全局ID
     *
     * @param absPath 绝对路径
     * @return 经过SHA-256摘要算法后的全局ID,同样的绝对路径得到的摘要算法结果一致
     */
    public static String getIdByPath(String absPath) {
        Assert.notBlank(absPath);
        // 统一转正斜杠"/"
        absPath = PathUtils.leftJoin(absPath);
        // 使用比MD5更强更严谨的`SHA-256`摘要算法,将文件绝对路径转换为唯一ID
        String sha256Hex = DigestUtil.sha256Hex(absPath);
        return sha256Hex;
    }
}
