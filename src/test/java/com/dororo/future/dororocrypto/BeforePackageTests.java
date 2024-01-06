package com.dororo.future.dororocrypto;

import cn.hutool.system.SystemUtil;
import org.junit.jupiter.api.Test;

/**
 * 使用Maven插件(maven-surefire-plugin)加测试类的方式,确保打JAR包之前,已经将dist目录拷贝到了static目录下
 *
 * @author Dororo
 */
public class BeforePackageTests {
    public static final String projectPath = SystemUtil.getUserInfo().getCurrentDir();

    @Test
    public void copyDist() {
        System.out.println("TODO copyDist");
    }

    @Test
    public void changeLogbackCharset() {
        System.out.println("TODO changeLogbackCharset");
    }
}
