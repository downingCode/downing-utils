package com.downing.utils.service.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author downing
 * @Date 2020/5/20 10:17
 * @Description 任务执行器  窗口
 **/
@Slf4j
public class TaskExecutor implements Runnable {

    // LinkedBlockingQueue构造的时候若没有指定大小，则默认大小为Integer.MAX_VALUE
    private final LinkedBlockingQueue<QueueTaskHandler> tasks;

    public TaskExecutor(LinkedBlockingQueue<QueueTaskHandler> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        try {
            QueueTaskHandler take = tasks.take();
            try {
                take.execute();
            } catch (Exception e) {
                log.error("任务处理失败");
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            log.error("服务停止");
            e.printStackTrace();
        }
    }
}
