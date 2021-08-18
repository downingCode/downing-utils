package com.downing.util.thead;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author downing
 * @desc
 * @date 2021/6/24 15:33
 */
public class DownThread extends Thread {

    private final String url;
    private final String fileName;
    private final long beginPost;
    private final long endPost;
    private RandomAccessFile osf;

    public DownThread(String url, String fileName, long beginPost, long endPost) {
        this.url = url;
        this.fileName = fileName;
        this.beginPost = beginPost;
        this.endPost = endPost;
    }

    @Override
    public void run() {
        download();
    }

    /**
     * 断点续传和多线程下载的关键代码关键位置：即设置断点 http2.setRequestProperty("RANGE","bytes="+startl+"-");//设置断点位置，向服务器请求从文件的哪个字节开始读取。
     * osf.seek(startl);//设置本地文件从哪个字节开始写入 如果是单线程，则首先要判断下载文件是否已经存在
     * 及DownloadFile.java 里的 fileName = "C:\\eclipse.zip";
     * 如果存在则开始断点续传，方法同多线程：
     * 因为断点续传是从上次传输中断的字节开始，则首先要得到上次中断的位置，既是文件长度（针对单线程）f.length()
     * 然后设置HTTP请求头属性RANGE，该属性告知服务器从哪个自己开始读取文件。
     * 设置本地文件写入起始字节，及接从上次传输断点继续写入（断点续传） osf.seek(offset)
     * 该方法设定从offset后一个字节开始写入文件
     * 注意：多线程不能用文件长度做为写文件起始字节，需有配置文件记录上次读写的位置，迅雷下载既是使用该种方法。
     */
    public void download() {
        try {
            osf = new RandomAccessFile(fileName, "rw");
            URL url = new URL(this.url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "NetFox");
            http.setRequestProperty("RANGE", "bytes=" + beginPost + "-");//设置读取文件字节位置
            InputStream is = http.getInputStream();//获取文件输入流
            osf.seek(beginPost);//设置文件起始光标位置
            byte[] b = new byte[1024];//每次读取1024字节
            int i;//每次读取的字节数
            int j = 0;//总字节数
            long beginTime = System.currentTimeMillis();
            while ((i = is.read(b, 0, 1024)) != -1 && j < endPost) {//保证读完
                osf.write(b, 0, i);//写入文件
                b = new byte[1024];// 重新赋值，避免重新读入旧内容
                j = i + j;
            }
            http.disconnect();//关闭连接
            osf.close();
            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "子线程共下载" + j + "k字节文件，耗时：" + (endTime - beginTime) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
