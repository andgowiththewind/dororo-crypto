package com.dororo.future.dororocrypto.dev;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.util.FileUtils;
import com.dororo.future.dororocrypto.util.PathUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.*;
import lombok.experimental.Accessors;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * 部署REDIS免安装版本X2
 *
 * @author Dororo
 * @date 2024-01-13 13:56 开发环境和生产环境
 */
public class RedisReleaseHelper {
    public static final String PROJECT_PATH = SystemUtil.getUserInfo().getCurrentDir();

    @SneakyThrows
    public static void release(List<RedisEnvParam> redisEnvParams) {
        if (CollectionUtil.isEmpty(redisEnvParams)) {
            return;
        }
        File redisZip = FileUtil.file(PROJECT_PATH, ".attachments/.backups/redis_x64_3.0.504.zip");
        for (RedisEnvParam envParam : redisEnvParams) {
            // 将ZIP分别解压缩到两个目录
            new ZipFile(redisZip).extractAll(envParam.getOutputPath());
            Console.log("[{}]解压缩至:[{}]", FileUtil.mainName(redisZip).toUpperCase(), envParam.getOutputPath());
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

        // 渲染一个能同时启动2个REDIS实例的脚本
        // TODO 尚未完善-应该改造为相对路径
        // batUp2Redis(redisEnvParams);

        // 替代方案
        File file = FileUtil.file(PROJECT_PATH, ".attachments/.templates/.redis/打开两个REDIS.bat");
        File targetFile = FileUtil.file(
                FileUtil.getParent(redisEnvParams.get(0).getOutputPath(), 1)
                , "打开两个REDIS.bat"
        );
        FileUtil.copy(file, targetFile, true);
    }

    @SneakyThrows
    private static void batUp2Redis(List<RedisEnvParam> redisEnvParams) {
        String outputPath = FileUtil.getParent(redisEnvParams.get(0).getOutputPath(), 1);
        // 渲染所需参数
        List<JSONObject> list = ListUtil.toList(
                JSONUtil.createObj().putOpt("name", "master").putOpt("port", "6391").putOpt("batPath", PathUtils.rightJoin(outputPath, "redis01", "up.bat"))
                , JSONUtil.createObj().putOpt("name", "slave").putOpt("port", "6392").putOpt("batPath", PathUtils.rightJoin(outputPath, "redis02", "up.bat"))
        );
        JSONObject dataModel = JSONUtil.createObj().putOpt("list", list);

        // 模板位置
        String templatePath = PathUtils.rightJoin(PROJECT_PATH, ".attachments/.templates/.redis/up2redis.bat.ftl");
        // 渲染至
        String targetPath = PathUtils.rightJoin(outputPath, "up2redis.bat");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);// 定义模板引擎
        cfg.setDirectoryForTemplateLoading(FileUtil.file(FileUtil.getParent(templatePath, 1)));// 定义模板文件位置
        Template template = cfg.getTemplate(FileUtil.getName(templatePath));
        FileWriter writer = new FileWriter(targetPath);
        template.process(BeanUtil.beanToMap(dataModel), writer);
        writer.close();
        Console.log("已渲染：[{}]", FileUtil.getName(targetPath));

        FileUtils.openDirectory(outputPath);
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class RedisEnvParam {
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
