package com.dororo.future.dororocrypto.util;

import cn.hutool.core.util.StrUtil;

import javax.swing.filechooser.FileSystemView;
import java.util.StringJoiner;

/**
 * 路径工具类
 *
 * @author Dororo
 * @version 5.0.0
 * @date 2024-01-06 21:52 基于Hutool二开,结合ChatGPT
 */
public class PathUtils {

    /**
     * 拼接多个路径片段，并使用正斜杠（/）作为分隔符。
     * 适用于Unix/Linux系统的路径格式。
     *
     * @param pathPieces 要拼接的路径片段数组
     * @return 使用正斜杠拼接的路径字符串
     */
    public static String leftJoin(String... pathPieces) {
        if (pathPieces == null || pathPieces.length == 0) {
            return "";
        }

        // 定义一个字符串拼接器,使用正斜杠作为分隔符
        StringJoiner joiner = new StringJoiner("/");
        for (String path : pathPieces) {
            if (StrUtil.isBlank(path)) {
                continue;
            }
            // 一次性替换所有不规范的分隔符,只要是连续的正反斜杠都会被替换为单个正斜杠
            String normalizedPath = path.replaceAll("[/\\\\]+", "/");
            // 去除路径片段的前后斜杠
            String trimmedPath = normalizedPath.replaceAll("^/|/$", "");
            joiner.add(trimmedPath);
        }
        return joiner.toString();
    }

    /**
     * 拼接多个路径片段，并使用反斜杠（\\）作为分隔符。
     * 适用于Windows系统的路径格式。
     *
     * @param pathPieces 要拼接的路径片段数组
     * @return 使用反斜杠拼接的路径字符串
     */
    public static String rightJoin(String... pathPieces) {
        String forwardSlashPath = leftJoin(pathPieces);
        return forwardSlashPath.replace("/", "\\");
    }

    /**
     * 获取当前用户的桌面路径
     */
    public static String desktop() {
        String desktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        return desktopPath;
    }
}
