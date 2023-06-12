package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.service.OrderService;
import cn.yz.yzmall.service.PayService;
import cn.yz.yzmall.service.config.AlipayConfig;
import cn.yz.yzmall.utils.ParamsUtils;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private OrderService orderService;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;
    @Value ("${alipay.returnUrl}")
    private String returnUrl;
    @Override
    public ResultVO pay(Map<String, String> map) {
        String untitled = map.get("untitled");
        String total = map.get("total");
        String orderId = map.get("orderId");
        DefaultAlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(alipayConfig.getAlipayConfig());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        JSONObject bizContent = new JSONObject();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", Double.parseDouble(total));
        bizContent.put("subject", untitled);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        return new ResultVO(ResStatus.OK,"提交成功",response.getBody());
    }
    @Override
    public ResultVO alipayCallback(HttpServletRequest request){
        Map<String, String> map;
        try {
            map = ParamsUtils.ParamstoMap(request);
            if (map != null){
                int i = orderService.updateOrderStatus(map.get("out_trade_no"), "2");
                if (i >0){
                    return new ResultVO(ResStatus.OK,"success",map);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new ResultVO(ResStatus.NO,"fail",null);
    }
}
