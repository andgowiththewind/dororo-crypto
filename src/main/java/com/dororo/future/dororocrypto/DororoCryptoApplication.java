package com.dororo.future.dororocrypto;

import com.dororo.future.dororocrypto.util.StartUpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DororoCryptoApplication {


    public static void main(String[] args) {
        StartUpUtils.redisStartUp();
        SpringApplication.run(DororoCryptoApplication.class, args);
        StartUpUtils.bannerPrint();
        StartUpUtils.openUrl();
    }
}
