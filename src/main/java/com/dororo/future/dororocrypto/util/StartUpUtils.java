package com.dororo.future.dororocrypto.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.components.EnvironmentHolder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Dororo
 * @date 2024-01-13 19:19
 */
public class StartUpUtils {


    public static void redisStartUp() {
        try {
            String batPath = null;
            if (Convert.toBool(SystemUtil.get("DEV_ING"), false)) {
                batPath = PathUtils.leftJoin(SystemUtil.getUserInfo().getCurrentDir(), ".environments/.dev/打开两个REDIS.bat");
            } else {
                // 打包为EXE后,获取的`SystemUtil.getUserInfo().getCurrentDir()`即EXE所在目录
                batPath = PathUtils.leftJoin(SystemUtil.getUserInfo().getCurrentDir(), "打开两个REDIS.bat");
            }

            String parent = FileUtil.getParent(batPath, 1);
            String name = FileUtil.getName(batPath);
            String cmdPart = StrUtil.format("cd {} && call {}", parent, name);

            // 创建进程构造器对象并设置要运行的命令及参数
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", cmdPart);

            // 将标准输入流、错误输出流合并到同一个流中
            processBuilder.redirectErrorStream(true);

            // 开始执行命令
            Process process = processBuilder.start();

            // 等待命令执行完成
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Batch file executed successfully.");
            } else {
                System.err.println("Failed to execute batch file with error code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void bannerPrint() {
        // 获取banner.txt的内容
        System.out.println(ResourceUtil.readUtf8Str("banner_copy.txt"));
    }

    public static void openUrl() {
        try {
            String port = SpringUtil.getBean(EnvironmentHolder.class).get("server.port");
            FileUtils.openUrl(StrUtil.format("http://localhost:{}", port));
        } catch (Exception e) {
            Console.error("打开浏览器失败,请手动打开主页");
        }
    }
}
