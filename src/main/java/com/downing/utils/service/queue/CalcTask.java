package com.downing.utils.service.queue;

/**
 * @Author downing
 * @Date 2020/5/20 14:06
 * @Description
 **/
public class CalcTask implements QueueTaskHandler {

    @Override
    public void execute() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println(9 / 3);
    }
}
