package com.downing.utils.service.junit;

import com.downing.utils.common.HttpUtil;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author downing
 * @descript
 */
public class NetWordCallable implements Callable {

    /**
     * 请求地址
     */
    private String method;
    private String url;
    private Map<String, String> headers;
    private Map<String, Object> paramMap;

    public NetWordCallable(String method, String url, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public NetWordCallable(String method, String url, Map<String, String> headers, Map<String, Object> paramMap) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.paramMap = paramMap;
    }

    @Override
    public Object call() throws Exception {
        if ("GET".equalsIgnoreCase(this.method)) {
            return HttpUtil.doGet(this.url, this.headers);
        } else if ("POST".equalsIgnoreCase(this.method)) {
            return HttpUtil.doPost(this.url, this.headers, this.paramMap);
        } else {
            throw new RuntimeException("请求方式错误");
        }
    }
}
