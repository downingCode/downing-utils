package com.downing.util.util;

/**
 * @author downing
 * @desc
 * @date 2021/7/1 10:57
 */
public class FileUtil {

    /**
     * 获取文件大小，格式化文件数字
     */
    public static String getFormatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f", fileSize * 1.0 / 1024) + "KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f", fileSize * 1.0 / (1024 * 1024)) + "MB";
        } else {
            return String.format("%.2f", fileSize * 1.0 / (1024 * 1024 * 1024)) + "GB";
        }
    }
}
