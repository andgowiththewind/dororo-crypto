package com.dororo.future.dororocrypto.components;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 请输入类描述
 *
 * @author Dororo
 * @date 2024-01-13 19:26
 */
@Component
public class EnvironmentHolder {
    private final Environment environment;

    public EnvironmentHolder(Environment environment) {
        this.environment = environment;
    }


    public String get(String key) {
        return environment.getProperty(key);
    }

}
