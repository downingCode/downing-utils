package com.downing.util.http;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class HttpClientClose extends Thread {

    @Autowired
    private PoolingHttpClientConnectionManager manager;

    private volatile boolean shutdown;

    public HttpClientClose() {
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    PoolStats totalStats = manager.getTotalStats();
                    int available = totalStats.getAvailable();//获取可用线程
                    int pending = totalStats.getPending();// 获取阻塞的线程数
                    int leased = totalStats.getLeased();//获取当前正在使用的连接数量
                    int max = totalStats.getMax();
                    //通过连接池关闭超时连接
                    manager.closeExpiredConnections();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        super.run();
    }

    // 容器关闭时候，强制关掉所有的http连接
    @PreDestroy
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
