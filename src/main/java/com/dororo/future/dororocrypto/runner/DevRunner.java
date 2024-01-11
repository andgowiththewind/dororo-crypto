package com.dororo.future.dororocrypto.runner;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.dororo.future.dororocrypto.util.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 开发环境启动类
 *
 * @author Dororo
 * @date 2024-01-08 12:18
 */
@Component
public class DevRunner implements ApplicationRunner {
    @Value("${DEV_ING}")
    private Boolean devIng;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!(devIng != null && devIng)) {
            return;
        }
        // prepareDevFiles();


    }

    private static void prepareDevFiles() {
        // 如果是开发环境,准备一些测试文件
        String folder = PathUtils.rightJoin(PathUtils.desktop(), "测试目录");
        if (!(FileUtil.exist(folder) && FileUtil.isDirectory(folder))) {
            FileUtil.mkdir(folder);
        }
        ArrayList<String> fileNameList = ListUtil.toList("测试文件001.txt", "测试文件002.txt", "测试文件003.txt", "config", ".gitkeep");
        fileNameList.forEach(fileName -> {
            String filePath = PathUtils.rightJoin(folder, fileName);
            if (!(FileUtil.exist(filePath) && FileUtil.isFile(filePath))) {
                FileUtil.touch(filePath);
            }
            FileUtil.writeUtf8String("测试文件内容" + DateUtil.now(), filePath);
        });
    }
}
