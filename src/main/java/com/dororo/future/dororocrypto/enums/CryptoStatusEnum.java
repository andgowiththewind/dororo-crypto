package com.dororo.future.dororocrypto.enums;

import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 加密状态枚举
 *
 * @author Dororo
 * @date 2024-01-08 00:58
 */
@Getter
@AllArgsConstructor
public enum CryptoStatusEnum {
    DECRYPTED(0, "未加密"),
    ENCRYPTED(1, "已加密"),
    ALL(2, "全部（加密 & 未加密）"),
    ;

    private Integer code;
    private String name;

    public static List<Integer> valueList() {
        return Arrays.stream(CryptoStatusEnum.values()).map(CryptoStatusEnum::getCode).collect(Collectors.toList());
    }

    public static CryptoStatusEnum get(Integer code) {
        Assert.notNull(code, "状态码不能为空");
        Assert.isTrue(valueList().contains(code), "状态码[{}]不存在", code);
        for (CryptoStatusEnum statusEnum : CryptoStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
