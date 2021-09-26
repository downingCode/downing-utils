package com.downing.util.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author downing
 * @desc
 * @date 2021/8/19 10:46
 */
@Data
@NoArgsConstructor
public class ResultVO {

    private String name;
    private String url;
    private String content;
    private String reqStatus = "未发送请求";
    private String respStatus = "未知";
    private Date reqDate;

    public ResultVO(String name, String url, String content, Date reqDate) {
        this.name = name;
        this.url = url;
        this.content = content;
        this.reqDate = reqDate;
    }
}
