package com.downing.utils.service.queue;
/**
 * @Author downing
 * @Date 2020/5/20 11:34
 * @Description
 **/
public class QueueTest {

    public static void main(String[] args) {
        CalcTask printTask1 = new CalcTask();
        PrintTask printTask2 = new PrintTask();
        TaskQueue taskQueue = new TaskQueue();
        taskQueue.addTask(printTask1);
        taskQueue.addTask(printTask2);
    }
}
