package com.downing.util.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("http.pool")
public class HttpClientConfig {
    // 最大连接数
    private Integer maxTotal;
    // 最大并发连接数
    private Integer defaultMaxPerRoute;
    // 创建连接的最大时间
    private Integer connectTimeout;
    // 连接获取超时时间
    private Integer connectionRequestTimeout;
    // 数据传输最长时间
    private Integer socketTimeout;
    // 提交时检查连接是否可用
    private boolean staleConnectionCheckEnabled;

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(Integer defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isStaleConnectionCheckEnabled() {
        return staleConnectionCheckEnabled;
    }

    public void setStaleConnectionCheckEnabled(boolean staleConnectionCheckEnabled) {
        this.staleConnectionCheckEnabled = staleConnectionCheckEnabled;
    }


    //定义httpClient连接池
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(getMaxTotal()); //最大连接数
        manager.setDefaultMaxPerRoute(getDefaultMaxPerRoute());//最大并发数
        return manager;
    }

    //定义HttpClient

    /**
     * HttpClientBuilder 作用: 从连接池中，动态获取连接
     * 实例化连接池，设置连接池管理
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager manager) {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(manager);
        return builder;
    }

    /**
     * 注入连接池， 用于获取httpClient
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder builder) {
        return builder.build();
    }

    /**
     * RequestConfig请求的配置信息，可以手动的修改请求的超时时间
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(getConnectTimeout())
                .setConnectionRequestTimeout(getConnectionRequestTimeout())
                .setSocketTimeout(getSocketTimeout());
//                .setStaleConnectionCheckEnabled(isStaleConnectionCheckEnabled());
    }

    @Bean
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }
}
