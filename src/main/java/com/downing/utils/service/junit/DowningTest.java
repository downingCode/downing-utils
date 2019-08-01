package com.downing.utils.service.junit;

import java.util.concurrent.ExecutionException;

/**
 * @author downing
 * @descript 并发测试类
 */
public class DowningTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ConcurrentRequest.concurrentRequset(1000, 1000,
                new NetWordCallable("get", "http://localhost:8899//index", null), (HandlerResult<String>) result -> {
                    System.out.println(result);
                    return null;
                }, 6000);
    }
}
