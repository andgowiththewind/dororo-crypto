package com.dororo.future.dororocrypto;

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
    public static final String redisPackagePath = PathUtils.rightJoin(projectPath, ".attachments/.backups/redis_x64_3.0.504.zip");

    @Test
    public void devDeploy() {
        // 清理历史文件
        cleanUpHistoricalFiles();
        // 解压缩REDIS免安装版压缩包
        redisRelease();
        // 渲染一个能同时启动2个REDIS实例的脚本
        renderUp2RedisScript();
    }

    private void cleanUpHistoricalFiles() {
        FileUtil.loopFiles(envPath).forEach(file -> {
            if (FileUtil.isFile(file) && !StrUtil.equalsIgnoreCase(FileUtil.getName(file), ".gitkeep")) {
                FileUtil.del(file);
            }
        });
    }

    @SneakyThrows
    private void redisRelease() {
        Assert.isTrue(FileUtil.exist(redisPackagePath) && FileUtil.isFile(redisPackagePath), StrUtil.format("REDIS压缩包不存在：[{}]", redisPackagePath));
        for (EnvParam envParam : getEnvParams()) {
            // 将ZIP分别解压缩到两个目录
            new ZipFile(redisPackagePath).extractAll(envParam.getOutputPath());
            Console.log("[{}]解压缩至:[{}]", FileUtil.mainName(redisPackagePath).toUpperCase(), envParam.getOutputPath());
            // 渲染BAT脚本
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);// 定义模板引擎
            cfg.setDirectoryForTemplateLoading(FileUtil.file(envParam.getTemplateDirPath()));// 定义模板文件位置
            // 模板仓库的文件逐个渲染
            for (File file : FileUtil.ls(envParam.getTemplateDirPath())) {
                boolean isTemplate = StrUtil.isNotBlank(FileUtil.extName(file)) && StrUtil.equalsIgnoreCase(FileUtil.extName(file), "ftl");// 是否是模板文件
                if (!isTemplate) {
                    // 非模板文件直接拷贝
                    File targetFile = FileUtil.file(PathUtils.rightJoin(envParam.getOutputPath(), FileUtil.getName(file)));
                    FileUtil.copy(file, targetFile, true);
                } else {
                    // 模板文件执行渲染
                    Template template = cfg.getTemplate(FileUtil.getName(file));
                    String targetFilePath = PathUtils.rightJoin(envParam.getOutputPath(), FileUtil.getName(file).replace(".ftl", ""));
                    FileWriter fileWriter = new FileWriter(targetFilePath);
                    template.process(BeanUtil.beanToMap(envParam), fileWriter);
                    fileWriter.close();
                    Console.log("已渲染:[{}]", FileUtil.getName(targetFilePath));
                }
            }
        }
    }

    @SneakyThrows
    private void renderUp2RedisScript() {
        // 渲染所需参数
        List<JSONObject> list = ListUtil.toList(
                JSONUtil.createObj().putOpt("name", "master").putOpt("port", "6391").putOpt("batPath", PathUtils.rightJoin(envPath, "redis01", "up.bat"))
                , JSONUtil.createObj().putOpt("name", "slave").putOpt("port", "6392").putOpt("batPath", PathUtils.rightJoin(envPath, "redis02", "up.bat"))
        );
        JSONObject dataModel = JSONUtil.createObj().putOpt("list", list);

        // 模板位置
        String templatePath = PathUtils.rightJoin(projectPath, ".attachments/.templates/.redis/up2redis.bat.ftl");
        // 渲染至
        String targetPath = PathUtils.rightJoin(envPath, "up2redis.bat");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);// 定义模板引擎
        cfg.setDirectoryForTemplateLoading(FileUtil.file(FileUtil.getParent(templatePath, 1)));// 定义模板文件位置
        Template template = cfg.getTemplate(FileUtil.getName(templatePath));
        FileWriter writer = new FileWriter(targetPath);
        template.process(BeanUtil.beanToMap(dataModel), writer);
        writer.close();
        Console.log("已渲染：[{}]", FileUtil.getName(targetPath));

        FileUtils.openFile(targetPath);

    }

    private static List<EnvParam> getEnvParams() {
        String masterPort = "6391";
        String slavePort = "6392";
        String masterPassword = "dororosheep.cn";
        EnvParam envParamMaster = EnvParam.builder()
                .outputPath(PathUtils.rightJoin(envPath, "redis01"))
                .templateDirPath(PathUtils.rightJoin(projectPath, ".attachments/.templates/.redis/.master"))
                .masterPassword(masterPassword)
                .slavePort(slavePort)
                .masterPort(masterPort)
                .build();

        EnvParam envParamSlave = EnvParam.builder()
                .outputPath(PathUtils.rightJoin(envPath, "redis02"))
                .templateDirPath(PathUtils.rightJoin(projectPath, ".attachments/.templates/.redis/.slave"))
                .masterPassword(masterPassword)
                .slavePort(slavePort)
                .masterPort(masterPort)
                .build();

        return ListUtil.of(envParamMaster, envParamSlave);
    }

    @Data
    @Builder
    public static class EnvParam {
        // 环境输出目录
        private String outputPath;
        // 模板文件存放目录
        private String templateDirPath;
        // 主节点端口
        private String masterPort;
        // 从节点端口
        private String slavePort;
        // 主节点密码
        private String masterPassword;
    }

}
