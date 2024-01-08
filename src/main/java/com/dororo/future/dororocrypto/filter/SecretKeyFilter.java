package com.dororo.future.dororocrypto.filter;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.dororo.future.dororocrypto.components.RedisCache;
import com.dororo.future.dororocrypto.constant.CacheConstants;
import com.dororo.future.dororocrypto.exception.CryptoBusinessException;
import com.dororo.future.dororocrypto.vo.common.PassCheckVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.RelativeRedirectFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 密码合理性校验过滤器
 *
 * @author Dororo
 * @date 2024-01-09 01:37
 */
@Slf4j
@Component
public class SecretKeyFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        // 需要过滤的接口,模式:equal
        List<String> equalList = ListUtil.toList("/crypto/cryptoSubmit", "/sys/checkSecretKey");
        boolean anyMatch = equalList.stream().anyMatch(s -> s.equals(requestURI));
        return !anyMatch;
    }


    /**
     * 对密码格式进行校验,校验通过则将密码摘要加密后记录在缓存中
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 设计上前端从请求头中传递密码
        String userPassword = request.getHeader("userPassword");
        Assert.notBlank(userPassword, "密码不能为空");
        // 对密码进行URL解码
        userPassword = URLUtil.decode(userPassword, "UTF-8");
        // 计算密码摘要
        String sha256Hex = DigestUtil.sha256Hex(userPassword);
        // 查询缓存中是否已经校验过该密码
        PassCheckVo cacheVo = redisCache.getCacheMapValue(CacheConstants.PASS_CHECK_MAP, sha256Hex);
        // 如果缓存中存在
        if (cacheVo != null) {
            if (cacheVo.getCheck() != null && cacheVo.getCheck()) {
                // 且历史记录已经曾经校验通过,这直接放行
                filterChain.doFilter(request, response);
                return;
            } else {
                // 存在历史记录但是记录校验不通过,如果有提示信息,则直接把提示信息返回
                if (StrUtil.isNotBlank(cacheVo.getErrorMessage())) {
                    throw new CryptoBusinessException(cacheVo.getErrorMessage());
                }
            }
        }
        // 如果缓存中不存在,说明是首次校验
        try {
            userPassValidate(userPassword);
            // 校验通过,将校验结果记录到缓存中
            redisCache.setCacheMapValue(CacheConstants.PASS_CHECK_MAP, sha256Hex, PassCheckVo.builder().sha256Key(sha256Hex).check(true).build());
            redisCache.expire(CacheConstants.PASS_CHECK_MAP, 60 * 60 * 24 * 7);
            filterChain.doFilter(request, response);
            return;
        } catch (IllegalArgumentException e) {
            // 校验失败,同样记录错误信息到缓存中
            redisCache.setCacheMapValue(CacheConstants.PASS_CHECK_MAP, sha256Hex, PassCheckVo.builder().sha256Key(sha256Hex).check(false).errorMessage(e.getMessage()).build());
            redisCache.expire(CacheConstants.PASS_CHECK_MAP, 60 * 60 * 24 * 7);
            // 校验失败,直接抛出异常
            throw new CryptoBusinessException(e.getMessage());
        }
    }

    private void userPassValidate(String userPassword) {
        Assert.isTrue((userPassword.length() >= 8 && userPassword.length() <= 24), "密码长度要求8-24个字符,当前：" + userPassword.length() + "个字符");
        Assert.isFalse(Validator.hasChinese(userPassword), "密码不能包含中文");
        Assert.isTrue(ReUtil.contains("[A-Z]", userPassword), "密码要求至少包含一个大写字母");
        Assert.isTrue(ReUtil.contains("[a-z]", userPassword), "密码要求至少包含一个小写字母");
        Assert.isTrue(ReUtil.contains("[0-9]", userPassword), "密码要求至少包含一个数字");
        // 至少包含一个特殊字符 (常用特殊字符: ! @ # $ % ^ & * - + = ?)
        Assert.isTrue(ReUtil.contains("[!@#$%^&*\\-+=?]", userPassword), "密码要求至少包含一个特殊字符,常用特殊字符: ! @ # $ % ^ & * - + = ?");
    }
}
