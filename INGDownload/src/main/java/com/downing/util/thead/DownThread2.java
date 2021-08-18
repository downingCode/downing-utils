package com.downing.util.thead;

import com.downing.util.util.HttpUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * @author downing
 * @desc
 * @date 2021/6/24 15:33
 */
public class DownThread2 extends Thread {

    private final String url;
    private final long beginPost;
    private final long endPost;
    public File tempFile;
    private final CountDownLatch latch;

    public DownThread2(String url, String fileDir, String fileName, CountDownLatch latch, int taskId, long beginPost, long endPost) {
        this.url = url;
        this.beginPost = beginPost;
        this.endPost = endPost;
        this.latch = latch;
        try {
            tempFile = new File(fileDir + fileName + "_" + taskId);
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        download2();
    }

    /**
     * 临时文件
     */
    public void download2() {
        try {
            //检测是否已经下载完成片段
            if (tempFile.length() == endPost - beginPost + 1 || tempFile.length() == endPost - beginPost) {
                System.out.println("已下载完成的文件：" + tempFile.getName());
                latch.countDown();
                return;
            }
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile.getPath(), true));
            URL url = new URL(this.url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            HttpUtil.setHeader(http);
            http.setRequestProperty("User-Agent", "NetFox");
            http.setRequestProperty("RANGE", "bytes=" + beginPost + "-" + endPost);//设置读取文件字节位置
            InputStream is = http.getInputStream();//获取文件输入流
            byte[] b = new byte[1024];//每次读取1024字节
            int len;//每次读取的字节数
            int readCount = 0;//总字节数
            long beginTime = System.currentTimeMillis();
            while ((len = is.read(b, 0, 1024)) != -1 && readCount <= endPost - beginPost) {//保证读完
                readCount = len + readCount;
                out.write(b, 0, len);//写入文件
                b = new byte[1024];// 重新赋值，避免重新读入旧内容
                //每读满4096个byte（一个内存页），往磁盘上flush一下
                if (readCount % 4096 == 0) {
                    out.flush();
                }
                System.out.printf(tempFile.getName() + "下载进度：%.2f%%, 已下载：%.2fMB", readCount / (double) (endPost - beginPost) * 100, readCount / 1024.0 / 1024);
                System.out.println("\n");
            }
            http.disconnect();//关闭连接
            out.flush();
            is.close();
            out.close();
            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "子线程共下载" + readCount + "字节文件，耗时：" + (endTime - beginTime) / 1000 + "s");
            latch.countDown();
        } catch (IOException e) {
            latch.countDown();
            e.printStackTrace();
        }
    }
}
