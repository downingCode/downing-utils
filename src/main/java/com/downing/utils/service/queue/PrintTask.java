package com.downing.utils.service.queue;

/**
 * @Author downing
 * @Date 2020/5/20 11:36
 * @Description
 **/
public class PrintTask implements QueueTaskHandler {

    @Override
    public void execute() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("test");
    }
}
