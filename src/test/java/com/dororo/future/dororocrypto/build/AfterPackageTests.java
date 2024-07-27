package com.dororo.future.dororocrypto.build;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.dev.RedisReleaseHelper;
import com.dororo.future.dororocrypto.util.FileUtils;
import lombok.*;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;


/**
 * 将`maven-package`得到的JAR包处理为EXE安装包
 *
 * @author Dororo
 * @date 2024-01-13 12:28
 */
public class AfterPackageTests {
    private final static String PROJECT_PATH = SystemUtil.getUserInfo().getCurrentDir();
    private final static String OUTPUT_PATH = FileUtil.file(PROJECT_PATH, ".environments/.deploy").getAbsolutePath();
    private static String JAR_PATH = null;
    private static String VERSION = "1.0.0";// 版本号,需要符合操作系统文件命名规范


    @Test
    public void Jar2ExeSetup() {
        // 准备工作
        preparation();
        // 模板渲染
        templateEngine();
        // 打开目录
        FileUtils.openDirectory(OUTPUT_PATH);
    }

    @SneakyThrows
    private void preparation() {
        // 输出目录不存在则创建
        FileUtil.mkdir(OUTPUT_PATH);
        // 找到JAR包
        copyJar();
        // 处理配置文件
        handleConfig();
        // 处理REDIS免安装版本的解压缩以及配置文件渲染
        redisRelease();
    }

    private void redisZipCopy() {
        FileUtil.copy(FileUtil.file(PROJECT_PATH, ".attachments/.backups/redis_x64_3.0.504.zip"), FileUtil.file(OUTPUT_PATH, "redis_x64_3.0.504.zip"), true);
    }

    private void redisConfOnly() {
        List<String> whiteList = ListUtil.toList("up.bat", "up_redis_windows.conf");
        for (String dirName : ListUtil.toList("redis01", "redis02")) {
            for (File loopFile : FileUtil.loopFiles(FileUtil.file(OUTPUT_PATH, dirName))) {
                if (!whiteList.contains(FileUtil.getName(loopFile))) {
                    FileUtil.del(loopFile);
                }
            }
        }
    }

    private static void handleConfig() {
        // JAR包打包进EXE后无法修改配置文件,因此需要将配置文件放在外部`config`目录下,方便内部SpringBoot读取
        File outputConfig = FileUtil.file(OUTPUT_PATH, "config");
        FileUtil.mkdir(outputConfig);

        // `config`目录至少有一个文件,否则`innoSetup`将报错
        FileUtil.touch(FileUtil.file(outputConfig, ".keep"));

        // 部分配置文件需要拷贝
        ArrayList<String> list = ListUtil.toList("application-thread-pool.yml", "application-redis.yml");
        list.forEach(config -> FileUtil.copy(FileUtil.file(PROJECT_PATH, "src/main/resources", config), FileUtil.file(outputConfig, config), true));
    }

    private void copyJar() {
        // 如果没有指定JAR_PATH,则默认取target目录下的第一个JAR文件
        if (JAR_PATH == null) {
            File jar = Arrays.stream(FileUtil.ls(FileUtil.file(PROJECT_PATH, "target").getAbsolutePath())).filter(file -> {
                String extName = FileUtil.extName(file);
                return StrUtil.isNotBlank(extName) && StrUtil.equalsIgnoreCase(extName, "jar");
            }).findFirst().orElse(null);
            Optional.ofNullable(jar).filter(Objects::nonNull).ifPresent(file -> JAR_PATH = file.getAbsolutePath());
        }
        // JAR文件必须存在
        Assert.isTrue(StrUtil.isNotBlank(JAR_PATH) && FileUtil.exist(JAR_PATH) && FileUtil.isFile(JAR_PATH), "JAR文件不存在,请先MAVEN打包项目");
        // 拷贝到输出目录
        FileUtil.copy(FileUtil.file(JAR_PATH), FileUtil.file(OUTPUT_PATH, FileUtil.getName(JAR_PATH)), true);
    }

    private void redisRelease() {
        RedisReleaseHelper.RedisEnvParam masterParam = RedisReleaseHelper.RedisEnvParam.builder()
                .outputPath(FileUtil.file(OUTPUT_PATH, "redis01").getAbsolutePath())
                .templateDirPath(FileUtil.file(PROJECT_PATH, ".attachments/.templates/.redis/.master").getAbsolutePath())
                .masterPort("6389")
                .slavePort("6390")
                .masterPassword("dororosheep.cn")
                .build();
        RedisReleaseHelper.RedisEnvParam slaveParam = RedisReleaseHelper.RedisEnvParam.builder()
                .outputPath(FileUtil.file(OUTPUT_PATH, "redis02").getAbsolutePath())
                .templateDirPath(FileUtil.file(PROJECT_PATH, ".attachments/.templates/.redis/.slave").getAbsolutePath())
                .masterPort("6389")
                .slavePort("6390")
                .masterPassword("dororosheep.cn")
                .build();
        RedisReleaseHelper.release(ListUtil.toList(masterParam, slaveParam));

    }

    /**
     * 渲染`.exe4j`以及`.iss`模板
     */
    private void templateEngine() {
        Assert.notBlank(VERSION, "版本号不能为空");
        File file = FileUtil.file(PROJECT_PATH, ".attachments/.templates/.exe");
        for (File f : FileUtil.ls(file.getAbsolutePath())) {
            String extName = FileUtil.extName(f);
            if (StrUtil.equalsIgnoreCase(extName, "ftl")) {
                // TODO
            } else {
                // 直接拷贝
                FileUtil.copy(f, FileUtil.file(OUTPUT_PATH, FileUtil.getName(f)), true);
            }
        }
    }
}