package com.dororo.future.dororocrypto.enums;

import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 加解密全过程状态枚举
 *
 * @author Dororo
 * @date 2024-01-08 20:23
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    ABSENT(-1, "文件不存在或已删除", "文件不存在或已删除"),
    FREE(0, "空闲", "存在但未被分配任务"),
    WAITING(10, "排队中", "分配了任务,处于排队线程中但未进入工作线程处理"),
    OUTPUTTING(20, "输出中", "作为源文件,正在输出到其他文件"),
    INPUTTING(30, "输入中", "作为目标文件,正在接收其他文件的输出"),
    ALMOST(40, "即将完成", "即将完成,但未完成(仍需处理改名等收尾操作)"),
    // 40完成后会回归至0,即空闲状态
    ;
    private Integer code;
    private String message;
    private String detail;

    public static List<Integer> codeList() {
        return Arrays.stream(StatusEnum.values()).map(StatusEnum::getCode).collect(Collectors.toList());
    }

    public static StatusEnum get(Integer code) {
        Assert.notNull(code, "状态码不能为空");
        Assert.isTrue(codeList().contains(code), "状态码[{}]不存在", code);
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}