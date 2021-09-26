package com.downing.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HttpClientService {
    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig requestConfig;

    private ThreadLocal<Map<String, String>> params = ThreadLocal.withInitial(HashMap::new);

    public String doGet(String url, Map<String, String> params, String charset) {
        //判断用户是否传入编码
        charset = getCharset(charset);
        // 判断用户是否传递参数
        if (params != null && !params.isEmpty()) {
            url += "?";
            String paramsToString = params.entrySet().parallelStream().map(p -> p.getKey() + "=" + p.getValue()).collect(Collectors.joining("&"));
            url += paramsToString;
        }
        //根据url请求发起访问
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        //发起请求
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            return getResult(httpResponse, charset);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("sb");
        }
    }

    public String doGet(String url, Map<String, String> params) {
        return doGet(url, params, null);
    }

    public String doGet(String url) {
        return doGet(url, null, null);
    }

    public String doPost(String url, Map<String, String> params, String charset) {
        //定义请求类型
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        //判断是否定义了字符集
        charset = getCharset(charset);
        //定义form表单
        if (params != null && !params.isEmpty()) {
            //准备参数集合
            List<NameValuePair> paramsList = params.entrySet().parallelStream().map(p -> new BasicNameValuePair(p.getKey(), p.getValue())).collect(Collectors.toList());
            //模拟表单操作
            try {

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList, charset);
                //将from对象放到请求体中
                httpPost.setEntity(urlEncodedFormEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //执行请求操作
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                return getResult(httpResponse, charset);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("请求有误");
    }

    public String doPost(String url, Map<String, String> params) {
        return doPost(url, params, null);
    }

    public String doPost(String url) {
        return doPost(url, null, null);
    }

    public String doPut(String url, Map<String, String> params, String charset) {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(requestConfig);
        charset = getCharset(charset);
        if (params != null && !params.isEmpty()) {
            //组装成List进行数据传递
            List<NameValuePair> paramsList = params.entrySet().parallelStream().map(p -> new BasicNameValuePair(p.getKey(), p.getValue())).collect(Collectors.toList());
            //组装from表单
            try {
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList);
                httpPut.setEntity(urlEncodedFormEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse execute = httpClient.execute(httpPut);
                return getResult(execute, charset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("请求失败");
    }

    public String getCharset(String charset) {
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        return charset;
    }

    /**
     * 获取响应请求数据
     */
    public String getResult(HttpResponse httpResponse, String charset) throws IOException {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("请求失败");
        }

        return EntityUtils.toString(httpResponse.getEntity(), charset);
    }
}
