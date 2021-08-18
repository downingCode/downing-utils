package com.downing.util.service;

import com.downing.util.config.DownConfig;
import com.downing.util.config.TheadPool;
import com.downing.util.thead.DownThread;
import com.downing.util.thead.DownThread2;
import com.downing.util.util.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author downing
 * @desc 下载v2.0
 * @date 2021/6/24 16:34
 */
@Service
public class DownService2 {

    private String path = "";
    private String fileName = "";
    private int threadCount = 5;//线程数
    private DownThread2[] childThread;//存放下载子线程
    private long[] startPost;//存放下载子线程
    private CountDownLatch latch;

    @Autowired
    private DownConfig downConfig;

    @PostConstruct
    public void init() {
        path = downConfig.getPath();
        threadCount = downConfig.getTheadCount();
        childThread = new DownThread2[downConfig.getTheadCount()];
        latch = new CountDownLatch(downConfig.getTheadCount());
        startPost = new long[downConfig.getTheadCount()];
    }

    public String start(String downUrl) {
        try {
            long beginTime = System.currentTimeMillis();
            if (StringUtils.isBlank(downUrl)) {
                System.out.println("文件地址不存在！");
                return "文件地址不存在";
            }
            URL url = new URL(downUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (http.getResponseCode() != HttpURLConnection.HTTP_OK || http.getInputStream() == null) {
                System.out.println("文件不存在或请求被拒绝！");
                return "文件不存在或请求被拒绝";
            }
            long totalSize = http.getContentLength();//获取总字节数
            long avgSize = totalSize / threadCount;//平均每个线程下载大小
            //long remainCount = totalSize % threadCount;//平均后剩余字节数
            System.out.println("下载文件大小：" + totalSize / 1024 + "k,平均每个线程下载" + avgSize / 1024 + "k");
            String newUrl = http.getURL().getFile();
            this.fileName = HttpUtil.getUrlFileName(newUrl);//文件名
            System.out.println("文件名：" + this.fileName);
            File file = new File(path + fileName);
            if (file.exists() && file.length() == totalSize) {
                System.out.println("文件已下载");
                return "文件已下载";
            }
            //分析临时文件
            startPost = analyzeTempFile(path, fileName, totalSize, startPost);
            ThreadPoolExecutor executor = TheadPool.getExecutor();
            for (int i = 0; i < threadCount; i++) {
                //每段数据的起始位置为(avgSize * i + 已下载长度)
                startPost[i] += avgSize * i;
                long endPost;
                if (i == threadCount - 1) {//最后一个线程加上剩余字节
                    endPost = totalSize;
                } else {
                    endPost = avgSize * (i + 1) - 1;
                }
                DownThread2 thread = new DownThread2(downUrl, this.path, fileName, latch, i, startPost[i], endPost);
                childThread[i] = thread;
                executor.execute(thread);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                //下载中断。写入文件
                e.printStackTrace();
                System.out.println("下载文件中断。。。");
                return "下载文件中断。。。";
            }
            //下载完合并临时文件
            rangeTempFile(childThread);
            long endTime = System.currentTimeMillis();
            System.out.println("下载总字节数：" + totalSize + "总耗时：" + (endTime - beginTime) / 1000 + " s");
            System.out.println("下载完成！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "下载完成!";
    }

    /**
     * 分析临时文件
     * fileLength 完整文件大小
     * startPost 各临时文件起始位置
     */
    public long[] analyzeTempFile(String filePath, String fileName, long fileLength, long[] startPost) {
        File file = new File(filePath + fileName);
        long localFileSize = file.length();
        if (file.exists()) {
            if (localFileSize < fileLength) {//下载未完成
                File fileDir = new File(filePath);
                File[] files = fileDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File tempFile : files) {
                        //目标文件名+"_"+编号
                        String tempFileName = tempFile.getName();
                        if (tempFileName.startsWith(fileName + "_")) {
                            long length = tempFile.length();
                            int fileLongNum = Integer.parseInt(tempFileName.substring(tempFileName.lastIndexOf("_") + 1));
                            startPost[fileLongNum] = length;
                        }
                    }
                }
            }
        } else {//目标文件不存在，创建新文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return startPost;
    }

    /**
     * 合并临时文件
     */
    public void rangeTempFile(DownThread2[] childThread) {
        try {
            System.out.println("开始合并文件...");
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.path + this.fileName));
            // 遍历所有临时文件，按顺序把下载内容写入目标文件中
            for (int i = 0; i < childThread.length; i++) {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(childThread[i].tempFile));
                int len;
                long count = 0;
                byte[] b = new byte[1024];
                while ((len = inputStream.read(b)) != -1) {
                    count += len;
                    out.write(b, 0, len);
                    if ((count % 4096) == 0) {
                        out.flush();
                    }
                }
                inputStream.close();
                // 写完删除临时文件
                childThread[i].tempFile.delete();
                System.out.println("第" + i + "个临时文件合并完成！");
            }
            out.flush();
            out.close();
            System.out.println("合并文件完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
