package com.dororo.future.dororocrypto.build;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Console;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.util.PathUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * 使用Maven插件(maven-surefire-plugin)加测试类的方式,确保打JAR包之前,已经将dist目录拷贝到了static目录下
 *
 * @author Dororo
 */
public class BeforePackageTests {
    public static final String projectPath = SystemUtil.getUserInfo().getCurrentDir();


    /**
     * 将前端工程下`dist`目录的内容拷贝到后端工程的`static`目录下
     */
    @Test
    public void copyDist() {
        String distPath = PathUtils.leftJoin(projectPath, "web-user-interface-design", "dist");
        String staticPath = PathUtils.leftJoin(projectPath, "src", "main", "resources", "static");

        // 判断`dist`目录是否存在
        Assert.isTrue(FileUtil.exist(distPath) && FileUtil.isDirectory(distPath), "前端工程下dist目录不存在");

        // 查询`index.html`文件的时间是否超过10分钟,如果是则发出警告
        if (DateUtil.between(FileUtil.lastModifiedTime(FileUtil.file(distPath, "index.html")), new Date(), DateUnit.MINUTE, false) > 10) {
            Console.error("前端工程下dist目录下的index.html文件最后修改时间超过10分钟,请决定是否需要重新执行`npm run build`命令");
        }

        // 删除`static`目录下除了`.gitkeep`的所有文件
        FileUtil.loopFiles(FileUtil.file(staticPath)).stream().forEach(file -> {
            if (!file.getName().equals(".gitkeep")) {
                FileUtil.del(file);
            }
        });

        // 拷贝
        FileUtil.copyContent(FileUtil.file(distPath), FileUtil.file(staticPath), true);
    }
}
