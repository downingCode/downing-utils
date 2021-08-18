package com.downing.util.service;

import com.downing.util.config.DownConfig;
import com.downing.util.config.TheadPool;
import com.downing.util.thead.DownThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author downing
 * @desc 下载v1.0 无断点续传
 * @date 2021/6/24 16:34
 */
@Service
public class DownService {

    private String path = "";
    private int threadCount = 5;//线程数
    private List<Thread> childThread;//存放下载子线程

    @Autowired
    private DownConfig downConfig;

    @PostConstruct
    public void init() {
        path = downConfig.getPath();
        threadCount = downConfig.getTheadCount();
        childThread = new ArrayList<>(downConfig.getTheadCount());
    }

    public void start(String downUrl) {
        try {
            URL url = new URL(downUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (http.getResponseCode() != HttpURLConnection.HTTP_OK || http.getInputStream() == null) {
                System.out.println("文件不存在");
                return;
            }
            long totalSize = http.getContentLength();//获取总字节数
            long avgSize = totalSize / threadCount;//平均每个线程下载大小
            long remainCount = totalSize % threadCount;//平均后剩余字节数
            System.out.println("下载文件大小：" + totalSize / 1024 + "k,平均每个线程下载" + avgSize / 1024 + "k");
            String fileName = path;
            String newUrl = http.getURL().getFile();
            if (newUrl != null || newUrl.length() > 0) {
                newUrl = java.net.URLDecoder.decode(newUrl, "UTF-8");
                int pos = newUrl.indexOf('?');
                if (pos > 0) {
                    newUrl = newUrl.substring(0, pos);
                }
                pos = newUrl.lastIndexOf('/');
                fileName += newUrl.substring(pos + 1);
            }
            System.out.println("文件名：" + fileName);
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            ThreadPoolExecutor executor = TheadPool.getExecutor();
            //启动线程
            for (int i = 1; i <= threadCount; i++) {
                long endPost;
                if (i == threadCount) {//最后一个线程加上剩余字节
                    endPost = avgSize + remainCount;
                } else {
                    endPost = avgSize;
                }
                Thread thread = new Thread(new DownThread(downUrl, fileName, i * avgSize, endPost));
                childThread.add(thread);
                executor.execute(thread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
