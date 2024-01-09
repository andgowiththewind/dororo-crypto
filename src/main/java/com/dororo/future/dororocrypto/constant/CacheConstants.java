package com.dororo.future.dororocrypto.constant;

import java.util.Map;

/**
 * 缓存常量
 *
 * @author Dororo
 * @date 2024-01-07 05:03
 */
public class CacheConstants {
    /**
     * 物理文件在REDIS中对应映射
     */
    public static final String BLOSSOM_MAP = "blossomMap";

    /**
     * 校验用户输入的密码,记录结果到缓存中,密码已经摘要算法加密
     */
    public static final String PASS_CHECK_MAP = "passCheckMap";

    /**
     * 系统参数
     */
    public static final String SYS_PARAM_MAP = "sysParams";

    /**
     * sysParams：map中的键
     */
    public static final class SysParamHKey {
        public static String CRYPTO_STATUS_OPTIONS = "cryptoStatusOptions";
        public static String TOP_DIRECTORY_PATH_OPTIONS = "topDirectoryPathOptions";
        public static final String ENCRYPTED_PREFIX = "encryptedPrefix";
    }

    // 操作Blossom增删改时,分布式锁锁前缀
    public static final String PREFIX_LOCK_BLOSSOM = "lockBlossom";
    // 提交加解密请求时后端实现防抖,锁前缀
    public static final String MISSION_ANTI_SHAKE = "missionAntiShake";
}
