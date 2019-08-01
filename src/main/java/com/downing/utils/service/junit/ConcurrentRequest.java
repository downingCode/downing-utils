package com.downing.utils.service.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author downing
 * @descript 并发线程
 */
public class ConcurrentRequest {

    /**
     * 多线程并发执行某项任务
     *
     * @param concurrentThreads    并发线程数，可以用来模拟并发访问用户数
     * @param times                总共执行多少次
     * @param task                 任务
     * @param executeTimeoutMillis 执行任务总超时
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static <T> void concurrentRequset(long concurrentThreads, int times, final Callable<T> task, HandlerResult<T> handler, long executeTimeoutMillis)
            throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool((int) concurrentThreads);
        List<Future<T>> results = new ArrayList<>(times);

        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            results.add(executor.submit(task));
        }
        executor.shutdown();

        boolean executeCompleteWithinTimeout = executor.awaitTermination(executeTimeoutMillis, TimeUnit.MILLISECONDS);
        if (!executeCompleteWithinTimeout) {
            System.out.println("Execute tasks out of timeout [" + executeTimeoutMillis + "ms]");
            /*
             * 取消所有任务
             */
            for (Future<T> r : results) {
                r.cancel(true);
            }
        } else {
            long totalCostTimeMillis = System.currentTimeMillis() - startTimeMillis;

            for (Future<T> r : results) {
                if (handler != null) {
                    handler.handler(r.get());
                }
            }

            System.out.println("concurrent threads: " + concurrentThreads + ", times: " + times);
            System.out.println("total cost time(ms): " + totalCostTimeMillis + "ms, avg time(ms): " + ((double) totalCostTimeMillis / times));
            System.out.println("tps: " + (double) (times * 1000) / totalCostTimeMillis);
        }
    }
}
