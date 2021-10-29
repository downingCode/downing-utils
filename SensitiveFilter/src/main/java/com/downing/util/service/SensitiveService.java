package com.downing.util.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.downing.util.entity.SensitiveWord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


@Service
public class SensitiveService {

    /**
     * 敏感词库
     */
    public static HashMap sensitiveWordMap;

    @Value("classpath:sensitiveWord.txt")
    private Resource sensitiveFile;

    @SuppressWarnings("rawtypes")
    public String sensitiveWordFiltering(String text) {
        // 从数据库中获取敏感词对象集合（调用的方法来自Dao层，此方法是service层的实现类）
        //List<SensitiveWord> sensitiveWords = sensitiveWordDao.getSensitiveWordListAll();

//        List<SensitiveWord> sensitiveWords = new ArrayList<>();
//        SensitiveWord w1 = new SensitiveWord();
//        w1.setWord("");
//        sensitiveWords.add(w1);
//
//        SensitiveWord w2 = new SensitiveWord();
//        w2.setWord("臭傻逼");
//        sensitiveWords.add(w2);
        Set<String> sensitiveList = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(sensitiveFile.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sensitiveList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 初始化敏感词库对象
        // 构建敏感词库
        // 传入SensitiveWordEngine类中的敏感词库
        SensitiveWordEngine.sensitiveWordMap = this.initKeyWord2(sensitiveList);
        System.out.println("开始替换...");
        long beginTime = System.currentTimeMillis();
        //获取过滤后的文字
        String replaceText = SensitiveWordEngine.replaceSensitiveWord(text, 2, "*");
        long endTime = System.currentTimeMillis();
        System.out.println("敏感词替换结束！耗时："+ (endTime - beginTime) +"ms");
        // 得到敏感词有哪些，传入2表示获取所有敏感词
        return replaceText;
    }

    /**
     * 初始化敏感词
     */
    public Map initKeyWord(List<SensitiveWord> sensitiveWords) {
        try {
            // 从敏感词集合对象中取出敏感词并封装到Set集合中
            Set<String> keyWordSet = new HashSet<>(sensitiveWords.size());
            for (SensitiveWord s : sensitiveWords) {
                keyWordSet.add(s.getWord().trim());
            }
            // 将敏感词库加入到HashMap中
            addSensitiveWordToHashMap(keyWordSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
     * 初始化敏感词
     */
    public Map initKeyWord2(Set<String> sensitiveWords) {
        try {
            // 从敏感词集合对象中取出敏感词并封装到Set集合中
//            Set<String> keyWordSet = new HashSet<>(sensitiveWords.size());
//            for (String word : sensitiveWords) {
//                keyWordSet.add(word.trim());
//            }
            // 将敏感词库加入到HashMap中
            addSensitiveWordToHashMap(sensitiveWords);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
     * 封装敏感词库
     */
    @SuppressWarnings("rawtypes")
    private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        // 初始化HashMap对象并控制容器的大小
        sensitiveWordMap = new HashMap<>(keyWordSet.size());
        // 敏感词
        String key;
        // 用来按照相应的格式保存敏感词库数据
        Map nowMap;
        // 用来辅助构建敏感词库
        Map<String,String> newWorMap;
        // 使用一个迭代器来循环敏感词集合
        for (String s : keyWordSet) {
            key = s;
            // 等于敏感词库，HashMap对象在内存中占用的是同一个地址，所以此nowMap对象的变化，sensitiveWordMap对象也会跟着改变
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                // 截取敏感词当中的字，在敏感词库中字为HashMap对象的Key键值
                char keyChar = key.charAt(i);
                // 判断这个字是否存在于敏感词库中
                Object wordMap = nowMap.get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<>(1);
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                // 如果该字是当前敏感词的最后一个字，则标识为结尾字
                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
                //System.out.println("封装敏感词库过程：" + sensitiveWordMap);
            }
            //System.out.println("查看敏感词库数据:" + sensitiveWordMap);
        }
    }
}
