package com.downing.util.controller;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author downing
 * @desc
 * @date 2021/8/19 10:55
 */
public class BaseController {
    private static final String CONTENT_TYPE     = "text/html;charset=utf-8";

    /**
     * 输出结果
     * @param response          HttpServletResponse
     * @param content           结果内容
     * @throws IOException      输出失败时抛出此异常
     */
    protected void printResult(HttpServletResponse response, String content) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
        PrintWriter out = response.getWriter();
        out.write(content);
        out.close();
    }
}
