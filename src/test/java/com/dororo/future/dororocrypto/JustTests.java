package com.dororo.future.dororocrypto;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.dororo.future.dororocrypto.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class JustTests {


    @Test
    public void test01() {
        try {
            // 创建进程构造器对象并设置要运行的命令及参数
            // ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "C:/dororovm/dev/projects/gitee/2414/dororo-crypto/.environments/.dev/打开两个REDIS.bat");
            // ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "cd C:/dororovm/dev/projects/gitee/2414/dororo-crypto/.environments/.dev && call 1.bat");
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "cd C:\\Program Files (x86)\\dororoCryptoAnswer && call 打开两个REDIS.bat");

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
}
