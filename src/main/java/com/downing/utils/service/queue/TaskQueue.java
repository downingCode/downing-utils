package com.downing.utils.service.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author downing
 * @Date 2020/5/20 10:34
 * @Description 机构 队列
 **/
@Slf4j
public class TaskQueue {

    // LinkedBlockingQueue构造的时候若没有指定大小，则默认大小为Integer.MAX_VALUE
    private LinkedBlockingQueue<QueueTaskHandler> tasks = new LinkedBlockingQueue<>(50000);

    // 类似于一个线程总管 保证所有的任务都在队列之中
    private ExecutorService service = Executors.newSingleThreadExecutor();

    //执行器
    private TaskExecutor taskExecutor =  new TaskExecutor(tasks);

    // 检查服务是否运行
    private volatile boolean running = true;

    public boolean addTask(QueueTaskHandler taskHandler) {
        if (!running) {
            log.warn("service is stop");
            return false;
        }
        //offer 队列已经满了，无法再加入的情况下
        boolean success = tasks.offer(taskHandler);
        if (!success) {
            log.error("添加任务到队列失败");
        }
        service.submit(taskExecutor);
        return success;
    }

    @PreDestroy
    public void destroy() {
        running = false;
        service.shutdownNow();
    }

}
