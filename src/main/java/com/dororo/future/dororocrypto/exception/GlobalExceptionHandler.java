package com.dororo.future.dororocrypto.exception;

import cn.hutool.core.util.StrUtil;
import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限校验异常
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public BaseMvcResponse handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return BaseMvcResponse.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseMvcResponse handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return BaseMvcResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }


    /**
     * 请求路径中缺少必需的路径变量
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public BaseMvcResponse handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return BaseMvcResponse.error(HttpStatus.BAD_REQUEST.value(), String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseMvcResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        String msg = String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue());
        return BaseMvcResponse.error(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseMvcResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String name = e.getParameterName();
        String type = e.getParameterType();
        String msg = StrUtil.format("类型为[{}]的请求参数[{}]不存在,请检查", type, name);
        log.error(StrUtil.format("[{}]-{}", requestURI, msg), e);
        return BaseMvcResponse.error(HttpStatus.BAD_REQUEST.value(), msg);
    }


    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseMvcResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return BaseMvcResponse.error("系统异常,请联系管理员");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public BaseMvcResponse handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return BaseMvcResponse.error("系统异常,请联系管理员");
    }

    /**
     * 自定义验证异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public BaseMvcResponse handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return BaseMvcResponse.error(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 自定义验证异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return BaseMvcResponse.error(HttpStatus.BAD_REQUEST.value(), message);
    }


    /**
     * 自定义业务异常:加解密过程中出现的异常
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CryptoBusinessException.class)
    public BaseMvcResponse handleCryptoBusinessException(CryptoBusinessException e) {
        log.debug(e.getMessage(), e);
        return BaseMvcResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }
}