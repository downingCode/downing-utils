package com.downing.utils.security;

import com.downing.utils.common.DowningResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author downing
 * @descript 异常拦截
 */
@ControllerAdvice
public class DowningExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 拦截所有业务异常
     */
    @ExceptionHandler(Exception.class)
    public DowningResult handleException(Exception exception) {
        return new DowningResult(500, exception.getMessage());
    }

}
