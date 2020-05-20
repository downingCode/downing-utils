package com.downing.utils.service.queue;

/**
 * @Author downing
 * @Date 2020/5/20 10:14
 * @Description 任务队列统一处理器  办事的人  具体的事自己决定
 **/
public interface QueueTaskHandler {

    void execute() throws Exception;
}
