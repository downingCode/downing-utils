package com.downing.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author downing
 * @desc
 * @date 2021/6/24 17:39
 */
@Component
@ConfigurationProperties(prefix = "download")
@PropertySource(value = {"classpath:download.yml"}, encoding = "utf-8", factory = YamlPropertySourceFactory.class)
public class DownConfig {

    private String path;
    private int theadCount;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTheadCount() {
        return theadCount;
    }

    public void setTheadCount(int theadCount) {
        this.theadCount = theadCount;
    }
}
