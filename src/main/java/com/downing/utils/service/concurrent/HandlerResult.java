package com.downing.utils.service.concurrent;

/**
 * @author downing
 * @descript
 */
public interface HandlerResult<T> {

    /**
     * 结果处理器
     *
     * @param result
     * @return
     */
    Object handler(T result);
}
