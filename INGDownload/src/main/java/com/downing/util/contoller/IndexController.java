package com.downing.util.contoller;

import com.downing.util.service.DownService;
import com.downing.util.service.DownService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author downing
 * @desc 网页测试下载
 * @date 2021/6/24 14:33
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private DownService downService;

    @Autowired
    private DownService2 downService2;

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/down")
    public String download(@RequestParam("url") String url) {
        downService.start(url);
        return "下载中...";
    }

    @GetMapping("/down2")
    public String download2(@RequestParam("url") String url) {
        return downService2.start(url);
    }
}
