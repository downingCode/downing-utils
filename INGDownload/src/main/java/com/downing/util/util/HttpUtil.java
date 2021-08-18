package com.downing.util.util;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;

/**
 * @author downing
 * @desc
 * @date 2021/6/27 10:13
 */
public class HttpUtil {

    /**
     * 获取下载地址文件名，不存在返回 download
     */
    public static String getUrlFileName(String url) {
        try {
            String decodeUrl = URLDecoder.decode(url, "UTF-8");
            int pos = decodeUrl.indexOf('?');
            if (pos > 0) {
                decodeUrl = decodeUrl.substring(0, pos);
            }
            pos = decodeUrl.lastIndexOf('/');
            return decodeUrl.substring(pos + 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "download";
        }
    }

    public static void setHeader(URLConnection con) {
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        con.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        con.setRequestProperty("Accept-Encoding", "aa");
        con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.setRequestProperty("Keep-Alive", "300");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
        con.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Referer", "https://m.weibo.cn/");
    }
}
