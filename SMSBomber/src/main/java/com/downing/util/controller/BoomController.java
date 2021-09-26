package com.downing.util.controller;

import com.downing.util.service.SendService;
import com.downing.util.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author downing
 * @desc
 * @date 2021/8/18 18:00
 */
@RestController
@RequestMapping("/sms")
public class BoomController extends BaseController {

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private SendService sendService;

    @GetMapping("/sendBoom")
    public void sendBoom(@RequestParam("phone") String phone, @RequestParam(value = "refresh", defaultValue = "false") boolean refresh) throws IOException {
        List<ResultVO> resultList = sendService.sendBoom(phone);
//        List<ResultVO> resultList = new ArrayList<>();
//        ResultVO v1 = new ResultVO();
//        v1.setContent("测试");
//        v1.setUrl("http://www.baidu.com");
//        v1.setReqDate(new Date());
//        resultList.add(v1);
        StringBuilder bf = new StringBuilder();
        bf.append("<!DOCTYPE html>\n");
        bf.append("<html lang=\"en\">\n");
        bf.append("<head>\n");
        bf.append("<meta charset=\"utf-8\">\n");
        bf.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n");
        bf.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
        bf.append("<meta name=\"description\" content=\"\">\n");
        bf.append("<meta name=\"author\" content=\"\">\n");
        if (refresh) {
            // 每10秒自动刷新页面
            bf.append("<meta http-equiv=\"refresh\" content=\"360\">\n");
        }
        bf.append("<title>boom服务状态</title>\n");
        bf.append("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css\"/>\n");
        bf.append("<link href=\"/server_helper/stylesheets/dashboard.css\" rel=\"stylesheet\"/>\n");
        bf.append("<style>\n");
        bf.append("tr td{ text-align: center; }\n");
        bf.append(" </style>\n");
        bf.append("</head>\n");
        bf.append("<body>\n");
        bf.append("<div class=\"container-fluid\">\n");
        bf.append("<div class=\"row\">\n");
        bf.append("<div class=\"col-md-10 col-md-offset-1\">\n");
        bf.append("\t<br/><br/><table class=\"table table-hover\" style=\"height:auto\">\n");
        bf.append("\t\t<thead><td>请求接口</th><td>请求地址</th><td>创建时间</th><td>请求状态</th><td>响应状态</th><td>返回内容</th></thead>\n");
        if (resultList.isEmpty()) {
            bf.append("\t\t<tr class=\"table table-bordered\" style=\"vertical-align:middle;\">\n");
            bf.append("<td colspan=\"7\">列表为空</td>\n");
            bf.append("\t\t</tr>\n");
        } else {
            for (ResultVO result : resultList) {
                bf.append("\t\t<tr class=\"table table-bordered\" style=\"vertical-align:middle;\">\n");
                bf.append("\t\t\t<td>").append(result.getName()).append("</td>\n");
                bf.append("\t\t\t<td>").append(result.getUrl()).append("</td>\n");
                bf.append("\t\t\t<td>").append(result.getReqDate()).append("</td>\n");
                if("未发送请求".equals(result.getReqStatus())){
                    bf.append("\t\t\t<td><font color=\"red\">").append(result.getReqStatus()).append("</font></td>\n");
                }else{
                    bf.append("\t\t\t<td><font color=\"green\">").append(result.getReqStatus()).append("</font></td>\n");
                }
                if("发送失败".equals(result.getRespStatus())){
                    bf.append("\t\t\t<td><font color=\"red\">").append(result.getRespStatus()).append("</font></td>\n");
                }else{
                    bf.append("\t\t\t<td><font color=\"green\">").append(result.getRespStatus()).append("</font></td>\n");
                }
                bf.append("\t\t\t<td>").append(result.getContent()).append("</td>\n");
                bf.append("\t\t</tr>\n");
            }
        }
        bf.append("\t</table>\n");
        bf.append("</div>\n");
        bf.append("</div>\n");
        bf.append("</div>\n");
        bf.append("</body>\n");
        bf.append("</html>\n");
        this.printResult(response, bf.toString());
    }


    @GetMapping("/testBoom")
    public void testBoom(@RequestParam("phone") String phone) throws IOException {
        List<ResultVO> resultList = sendService.sendBoom(phone);
    }
}
