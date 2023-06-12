package cn.yz.yzmall.service;

import cn.yz.yzmall.vo.ResultVO;
import com.alipay.v3.ApiException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PayService {

    ResultVO pay(Map<String,String> map) throws  ApiException;
    ResultVO alipayCallback(HttpServletRequest request);
}
