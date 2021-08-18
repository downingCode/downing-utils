package com.downing.util.service;

import com.downing.util.config.TheadPool;
import com.downing.util.entity.DownInfo;
import com.downing.util.thead.DownThread3;
import com.downing.util.util.HttpUtil;
import org.apache.commons.lang.StringUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author downing
 * @desc 下载v3.0
 * @date 2021/6/24 16:34
 */
public class DownService3 {

    private String path;
    private String fileName = "";
    private int threadCount;//线程数
    private final DownThread3[] childThread;//存放下载子线程
    private long[] startPost;//存放下载子线程
    private DownInfo info;
    private static long totalDownloadSize = 0;
    private static long totalFileSize = 0;

    public DownService3(String path, int threadCount) {
        this.path = path;
        this.threadCount = threadCount;
        this.childThread = new DownThread3[threadCount];
        this.startPost = new long[threadCount];
    }

    public DownInfo start(String downUrl) {
        try {
            info = new DownInfo();
            long beginTime = System.currentTimeMillis();
            if (StringUtils.isBlank(downUrl)) {
                System.out.println("文件地址不存在！");
                info.setMessage("文件地址不存在");
                return info;
            }
            URL url = new URL(downUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (http.getResponseCode() != HttpURLConnection.HTTP_OK || http.getInputStream() == null) {
                System.out.println("文件不存在或请求被拒绝！");
                info.setMessage("文件不存在或请求被拒绝");
                return info;
            }
            totalFileSize = http.getContentLength();//获取总字节数
            long avgSize = totalFileSize / threadCount;//平均每个线程下载大小
            //long remainCount = totalFileSize % threadCount;//平均后剩余字节数
            System.out.println("下载文件大小：" + totalFileSize / 1024 + "k,平均每个线程下载" + avgSize / 1024 + "k");
            String newUrl = http.getURL().getFile();
            this.fileName = HttpUtil.getUrlFileName(newUrl);//文件名
            System.out.println("文件名：" + this.fileName);
            File file = new File(path + fileName);
            if (file.exists() && file.length() == totalFileSize) {
                System.out.println("文件已下载");
                info.setMessage("文件已下载");
                totalFileSize = -1;
                return info;
            }
            //分析临时文件
            startPost = analyzeTempFile(path, fileName, totalFileSize, startPost);
            ThreadPoolExecutor executor = TheadPool.getExecutor();
            boolean downFlag = true;
            for (int i = 0; i < threadCount; i++) {
                //每段数据的起始位置为(avgSize * i + 已下载长度)
                startPost[i] += avgSize * i;
                long endPost;
                if (i == threadCount - 1) {//最后一个线程加上剩余字节
                    endPost = totalFileSize;
                } else {
                    endPost = avgSize * (i + 1) - 1;
                }
                DownThread3 thread = new DownThread3(downUrl, this.path, fileName, i, startPost[i], endPost);
                childThread[i] = thread;
                Future<Boolean> submit = executor.submit(thread);
                boolean flag = submit.get();
                if (!flag) {
                    downFlag = false;
                    System.out.println("childThread-" + i + ":下载失败");
                }
            }
            if (downFlag) {
                //下载完合并临时文件
                rangeTempFile(childThread);
                long endTime = System.currentTimeMillis();
                long costTime = (endTime - beginTime) / 1000;
                System.out.println("下载总字节数：" + totalFileSize + "总耗时：" + costTime + " s");
                System.out.println("下载完成！");
                info.setCostTime(costTime);
                info.setProgress(100);
                info.setStatus(1);
                info.setMessage("下载完成!");
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            info.setStatus(-1);
            info.setMessage("下载失败!");
        }
        return info;
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
    public void rangeTempFile(DownThread3[] childThread) {
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

    /**
     * 当前下载进度
     */
    public double getCurrentProgress() {
        if (totalFileSize == 0) {
            return 0.0d;
        }
        if (totalFileSize == -1) {
            return 1d;
        }
        long sumSize = 0;
        for (DownThread3 thread : childThread) {
            if (thread != null) {
                sumSize += thread.getReadCount();
            }
        }
        return sumSize * 1.0 / totalFileSize;
    }

    public DownInfo getInfo() {
        return info;
    }

    public void setInfo(DownInfo info) {
        this.info = info;
    }
}
