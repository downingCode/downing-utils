package com.downing.util.service;

import com.downing.util.SmsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author downing
 * @desc
 * @date 2021/8/18 17:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SmsApplication.class)
public class SendServiceTest {

    @Autowired
    private SendService sendService;

    @Test
    public void sendGet() {
        sendService.sendGet("",null,null);
    }

    @Test
    public void sendPost() {
    }
}