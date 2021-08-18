package com.downing.util.util;

/**
 * @author downing
 * @desc
 * @date 2021/6/29 10:25
 */
public class TimeUtil {

    /**
     * 对下载所用的时间格式进行处理
     */
    public static String executeTime(long time) {
        if (time < 1000) {
            return time + "ms";
        } else if (time < 1000 * 60) {
            return time / 1000 + "s";
        } else if (time < 1000 * 60 * 60) {
            return time / (1000 * 60) + "min";
        } else {
            return time / (1000 * 60 * 60) + "h";
        }
    }
}
