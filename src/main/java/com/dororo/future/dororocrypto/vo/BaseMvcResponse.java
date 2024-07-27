package com.dororo.future.dororocrypto.vo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseMvcResponse implements Serializable {
    public final static Integer DEFAULT_SUCCESS_CODE = Convert.toInt(HttpStatus.HTTP_OK);
    public final static Integer DEFAULT_ERROR_CODE = Convert.toInt(HttpStatus.HTTP_INTERNAL_ERROR);

    @Builder.Default
    private Integer code = DEFAULT_SUCCESS_CODE;
    @Builder.Default
    private String msg = "success";
    private Object data;

    public static BaseMvcResponse success() {
        return BaseMvcResponse.builder().build();
    }

    public static BaseMvcResponse success(Integer code) {
        return BaseMvcResponse.builder().code(code).build();
    }

    public static BaseMvcResponse success(String message) {
        return BaseMvcResponse.builder().msg(message).build();
    }

    public static BaseMvcResponse success(Integer code, String message) {
        return BaseMvcResponse.builder().code(code).msg(message).build();
    }

    public static BaseMvcResponse successData(Object data) {
        return BaseMvcResponse.builder().data(data).build();
    }

    public static BaseMvcResponse error(String message) {
        return BaseMvcResponse.builder().code(DEFAULT_ERROR_CODE).msg(message).build();
    }

    public static BaseMvcResponse error(Integer code, String message) {
        return BaseMvcResponse.builder().code(code).msg(message).build();
    }

    public static BaseMvcResponse errorData(Object data) {
        return BaseMvcResponse.builder().code(DEFAULT_ERROR_CODE).data(data).build();
    }


    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        Integer code = this.getCode();
        if (code != null && NumberUtil.equals(Convert.toLong(code), Convert.toLong(DEFAULT_SUCCESS_CODE))) {
            return true;
        }
        return false;
    }
}
