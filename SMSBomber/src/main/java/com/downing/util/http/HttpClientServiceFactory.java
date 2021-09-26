package com.downing.util.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class HttpClientServiceFactory {
    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RequestConfig requestConfig;

    private final static String DEFAULT_CHARSET = "UTF-8";

    private ThreadLocal<Map<String, String>> params = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private ThreadLocal<Map<String, String>> headers = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private ThreadLocal<String> charset = ThreadLocal.withInitial(() -> DEFAULT_CHARSET);

    private ThreadLocal<HttpClientRequestMethod> method = ThreadLocal.withInitial(() -> HttpClientRequestMethod.valueOf("get"));
    private ThreadLocal<String> url = ThreadLocal.withInitial(() -> "");
    private ThreadLocal<HttpRequestBase> httpRequest = ThreadLocal.withInitial(HttpGet::new);

    /**
     * 归约方法
     *
     * @param handler 核心处理业务方法
     */
    public Object reduce(BiFunction<HttpResponse, String, Object> handler) {
        //初始化客户端
        initHttpRequest();
        //获取字符集
        String charset = this.charset.get();
        //定义头部
        Header[] headerArr = new Header[headers.get().size()];
        headers.get().entrySet().parallelStream().map(h -> new BasicHeader(h.getKey(), h.getValue())).collect(Collectors.toSet()).toArray(headerArr);
        HttpRequestBase http = this.httpRequest.get();
        http.setHeaders(headerArr);
        //定义基本配置
        http.setConfig(requestConfig);
        //获取请求体(处理传递的参数问题)
        HttpRequestBase handlerHttp = getHandlerHttpRequest(http);
        try {
            //处理json请求
            if(HttpClientRequestMethod.JSON.equals(this.method.get())){
                HttpPost httpPost = (HttpPost) handlerHttp;
                String paramJson = JSONObject.toJSONString(this.params.get());
                StringEntity entity = new StringEntity(paramJson,"utf-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            //发起请求
            HttpResponse httpResponse = httpClient.execute(handlerHttp);
            return handler.apply(httpResponse, charset);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 初始化请求体
     */
    private void initHttpRequest() {
        HttpClientRequestMethod method = this.method.get();
        String url = this.url.get();
        HttpRequestBase hrb;
        if (method.equals(HttpClientRequestMethod.GET)) {
            hrb = new HttpGet(url);
        } else if (method.equals(HttpClientRequestMethod.POST)) {
            hrb = new HttpPost(url);
        } else if (method.equals(HttpClientRequestMethod.PATCH)) {
            hrb = new HttpPatch(url);
        } else if (method.equals(HttpClientRequestMethod.PUT)) {
            hrb = new HttpPut(url);
        } else if (method.equals(HttpClientRequestMethod.DELETE)) {
            hrb = new HttpDelete(url);
        }else if (method.equals(HttpClientRequestMethod.JSON)) {
            hrb = new HttpPost(url);
        } else {
            throw new RuntimeException("没有对应的请求方法处理");
        }
        this.httpRequest.set(hrb);
    }

    /**
     * 将请求参数加入请求体中，并返回
     *
     * @param http 请求体
     */
    private HttpRequestBase getHandlerHttpRequest(HttpRequestBase http) {
        if (http instanceof HttpGet) {
            //特殊处理get参数
            handlerGetParams();
            //更改设定好的url
            http.setURI(URI.create(this.url.get()));
            return http;
        } else if (http instanceof HttpEntityEnclosingRequestBase) {
            //form表单
            UrlEncodedFormEntity urlEncodedFormEntity = handlerOtherParams();
            HttpEntityEnclosingRequest httpEntity = (HttpEntityEnclosingRequestBase) http;
            httpEntity.setEntity(urlEncodedFormEntity);
            return (HttpRequestBase) httpEntity;
        }
        throw new RuntimeException("处理失败");
    }

    //获取表单对象（除get方法外，所有请求参数组装到表单对象中）
    private UrlEncodedFormEntity handlerOtherParams() {
        String charset = this.charset.get();
        Map<String, String> params = this.params.get();
        //准备参数集合
        List<NameValuePair> paramsList = ((Set<Map.Entry<String, String>>) params.entrySet())
                .parallelStream()
                .map(p -> new BasicNameValuePair(p.getKey(), p.getValue()))
                .collect(Collectors.toList());

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        //模拟表单操作
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlEncodedFormEntity;
    }

    //处理get请求参数
    private void handlerGetParams() {
        if (!HttpClientRequestMethod.GET.equals(this.method.get())) {
            return;
        }
        //没有参数就返回
        Map<String, String> params = this.params.get();
        if (params.isEmpty()) {
            return;
        }
        this.setParams(null);
        String url = this.url.get();
        Set<Map.Entry<String, String>> set = params.entrySet();
        if (!params.isEmpty()) {
            String paramsString = set.parallelStream().map(
                    p -> p.getKey() + "=" + p.getValue()
            ).collect(Collectors.joining("&"));
            this.url.set(url.concat("?").concat(paramsString));
        }
    }

    // 核心处理方法

    /**
     * @param httpResponse 请求响应体
     * @param charset      字符集
     */
    public String getResult(HttpResponse httpResponse, String charset) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            try {
                return EntityUtils.toString(httpResponse.getEntity(), charset);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("请求失败");
            }
        }
        try {
            return EntityUtils.toString(httpResponse.getEntity(), charset);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("请求失败result");
        }
    }

    public HttpClientServiceFactory setCharset(String charset) {
        this.charset.set(charset);
        return this;
    }

    public HttpClientServiceFactory setHeaders(Map<String, String> headers) {
        this.headers.set(headers);
        return this;
    }

    public HttpClientServiceFactory setParams(Map<String, String> params) {
        this.params.set(params);
        return this;

    }

    public HttpClientServiceFactory setMethod(HttpClientRequestMethod method) {
        this.method.set(method);
        return this;
    }

    public HttpClientServiceFactory setUrl(String url) {
        this.url.set(url);
        return this;
    }
}
