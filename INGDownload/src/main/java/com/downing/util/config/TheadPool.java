package com.downing.util.config;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author downing
 * @desc
 * @date 2021/6/25 18:09
 */
public class TheadPool {

    private static ThreadPoolExecutor instance = null;

    public TheadPool() {
    }

    public static ThreadPoolExecutor getExecutor() {
        if (instance == null) {
            synchronized (TheadPool.class) {
                if (instance == null) {
                    //new 创建对象非原子操作 分为三步：1.分配内存空间 2.初始化成员变量 3 对象指向内存地址
                    instance = new ThreadPoolExecutor(128, 1024, 60L, TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }
        return instance;
    }
}
