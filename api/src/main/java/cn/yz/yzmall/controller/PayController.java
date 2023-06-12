package cn.yz.yzmall.controller;

import cn.yz.yzmall.service.PayService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
@CrossOrigin
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/callback")
    public String paySuccess(HttpServletRequest request){
        System.out.println("back----------");
        ResultVO resultVO = payService.alipayCallback(request);
        if (resultVO.getCode()== ResStatus.OK){
            return "success";
        }
        return "fail";
    }

    @GetMapping("/returnback")
    public String returnSuccess(HttpServletRequest request){
        System.out.println("return----------");

        return "fail";
    }
}
