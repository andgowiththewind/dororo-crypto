package com.dororo.future.dororocrypto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 自定义业务异常
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CryptoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String message;

    public CryptoException(String message) {
        super(message);
        this.message = message;
    }
}
