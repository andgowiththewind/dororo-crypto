package com.dororo.future.dororocrypto;

import cn.hutool.core.io.resource.ResourceUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DororoCryptoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DororoCryptoApplication.class, args);
        bannerPrint();
    }

    private static void bannerPrint() {
        // 获取banner.txt的内容
        System.out.println(ResourceUtil.readUtf8Str("banner_copy.txt"));
    }

}
