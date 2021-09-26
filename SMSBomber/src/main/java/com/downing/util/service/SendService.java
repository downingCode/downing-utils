package com.downing.util.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.downing.util.http.HttpClientRequestMethod;
import com.downing.util.http.HttpClientServiceFactory;
import com.downing.util.vo.ResultVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author downing
 * @desc
 * @date 2021/8/18 16:56
 */
@Service
public class SendService {
    @Autowired
    private HttpClientServiceFactory httpClient;

    @Value("classpath:boom.json")
    private Resource resource;

    @Value("classpath:test.json")
    private Resource testResource;

    public List<ResultVO> sendBoom(String phone) {
        //获取配置列表
        JSONArray jsonArray = parseJson(phone, resource);
        List<ResultVO> resultVO = new ArrayList<>(jsonArray.size());
        for (Object obj : jsonArray) {
            try {
                JSONObject jsonObject = (JSONObject) obj;
                String reqMethod = jsonObject.getString("type");
                String url = jsonObject.getString("url");
                String headerStr = jsonObject.getString("headers");
                String reqName = jsonObject.getString("name");
                Map<String, String> headers = new ConcurrentHashMap<>();
                if (StringUtils.isNotBlank(headerStr)) {
                    headers = (Map<String, String>) JSONObject.parse(headerStr);
                } else {
                    headers.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
                }
                //判断请求类型
                if (HttpClientRequestMethod.GET.toString().equals(reqMethod)) {
                    String cookie = jsonObject.getString("cookie");
                    ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();
                    if (StringUtils.isNotBlank(cookie)) {
                        headers.put("cookie", cookie);
                    }
                    ResultVO result = sendGet(url, headers, params);
                    result.setName(reqName);
                    resultVO.add(result);
                } else if (HttpClientRequestMethod.POST.toString().equals(reqMethod)) {
                    String paramStr = jsonObject.getString("param");
                    Map<String, String> paramMap = (Map<String, String>) JSONObject.parse(paramStr);
                    ResultVO result = sendPost(url, HttpClientRequestMethod.POST, headers, paramMap);
                    result.setName(reqName);
                    resultVO.add(result);
                } else if (HttpClientRequestMethod.JSON.toString().equals(reqMethod)) {
                    String paramStr = jsonObject.getString("param");
                    Map<String, String> paramMap = (Map<String, String>) JSONObject.parse(paramStr);
                    ResultVO result = sendPost(url, HttpClientRequestMethod.JSON, headers, paramMap);
                    result.setName(reqName);
                    resultVO.add(result);
                } else {
                    ResultVO result = new ResultVO(reqName, url, "请求接口方法配置不匹配", new Date());
                    resultVO.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultVO;
    }

    public JSONArray parseJson(String phone, Resource resource) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            StringBuilder message = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                message.append(line);
            }
            String jsonString = message.toString();
            String handlerJson = jsonString.replaceAll("target_Phone", phone);
            return (JSONArray) JSONObject.parse(handlerJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultVO sendGet(String url, Map<String, String> headers, Map<String, String> params) {
        ResultVO vo = new ResultVO();
        vo.setUrl(url);
        String result = (String) httpClient
                .setMethod(HttpClientRequestMethod.GET)
                .setUrl(url)
                .setHeaders(headers)
                .setParams(params)
                .reduce(httpClient::getResult);
        vo.setContent(result);
        vo.setReqStatus("请求成功");
        vo.setRespStatus(handlerResultCode(result));
        vo.setReqDate(new Date());
        return vo;
    }

    public ResultVO sendPost(String url, HttpClientRequestMethod method, Map<String, String> headers, Map<String, String> params) {
        ResultVO vo = new ResultVO();
        vo.setUrl(url);
        String result = (String) httpClient
                .setMethod(method)
                .setUrl(url)
                .setParams(params)
                .setHeaders(headers)
                .reduce(httpClient::getResult);
        vo.setContent(result);
        vo.setReqStatus("请求成功");
        vo.setRespStatus(handlerResultCode(result));
        vo.setReqDate(new Date());
        return vo;
    }

    public String handlerResultCode(String result) {
        if (result.contains("200") || result.contains("成功") || result.contains("success") || result.contains("true")|| result.contains("\"status\":1")) {
            return "发送成功";
        } else {
            return "发送失败";
        }
    }
}
