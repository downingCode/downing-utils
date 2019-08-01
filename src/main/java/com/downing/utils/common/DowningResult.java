package com.downing.utils.common;

import lombok.Data;

/**
 * @author downing
 * @descript
 */
@Data
public class DowningResult {

    private Integer code;
    private String message;
    private Object data;

    public DowningResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public DowningResult(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
