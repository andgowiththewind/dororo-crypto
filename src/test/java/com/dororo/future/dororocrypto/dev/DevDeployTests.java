package com.dororo.future.dororocrypto.dev;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.util.FileUtils;
import com.dororo.future.dororocrypto.util.PathUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 部署开发环境
 *
 * @author Dororo
 * @date 2024-01-06 21:41
 */
public class DevDeployTests {
    public static final String projectPath = SystemUtil.getUserInfo().getCurrentDir();
    public static final String envPath = PathUtils.rightJoin(projectPath, ".environments/.dev");

    @Test
    public void devDeploy() {
        // 清理历史文件
        cleanUpHistoricalFiles();

        // 解压缩REDIS免安装版压缩包
        redisReleaseAction();
    }

    private void cleanUpHistoricalFiles() {
        FileUtil.loopFiles(envPath).forEach(file -> {
            if (FileUtil.isFile(file) && !StrUtil.equalsIgnoreCase(FileUtil.getName(file), ".gitkeep")) {
                FileUtil.del(file);
            }
        });
    }

    private void redisReleaseAction() {
        RedisReleaseHelper.RedisEnvParam masterParam = RedisReleaseHelper.RedisEnvParam.builder()
                .outputPath(FileUtil.file(envPath, "redis01").getAbsolutePath())
                .templateDirPath(FileUtil.file(projectPath, ".attachments/.templates/.redis/.master").getAbsolutePath())
                .masterPort("6389")
                .slavePort("6390")
                .masterPassword("dororosheep.cn")
                .build();
        RedisReleaseHelper.RedisEnvParam slaveParam = RedisReleaseHelper.RedisEnvParam.builder()
                .outputPath(FileUtil.file(envPath, "redis02").getAbsolutePath())
                .templateDirPath(FileUtil.file(projectPath, ".attachments/.templates/.redis/.slave").getAbsolutePath())
                .masterPort("6389")
                .slavePort("6390")
                .masterPassword("dororosheep.cn")
                .build();

        RedisReleaseHelper.release(ListUtil.toList(masterParam, slaveParam));
    }

}
