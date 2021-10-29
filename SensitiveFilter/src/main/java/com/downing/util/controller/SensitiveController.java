package com.downing.util.controller;

import com.downing.util.service.SensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class SensitiveController {

    @Autowired
    private SensitiveService sensitiveService;

    /**
     * 敏感词过滤
     */
    @RequestMapping(value = "/word/filter/{text}")
    public void sensitiveWordFiltering(@PathVariable String text) {
        try {
            String replaceText = sensitiveService.sensitiveWordFiltering(text);
            System.out.println(replaceText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
