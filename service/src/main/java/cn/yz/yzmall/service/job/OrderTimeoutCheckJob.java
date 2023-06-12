package cn.yz.yzmall.service.job;

import cn.yz.yzmall.dao.OrdersMapper;
import cn.yz.yzmall.entity.Orders;
import cn.yz.yzmall.service.OrderService;
import cn.yz.yzmall.service.config.AlipayConfig;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class OrderTimeoutCheckJob {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderService orderService;
    @Scheduled(cron = "0/10 * * * * ?")
    public void checkAndCloseOrder(){
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        Date time = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
        criteria.andEqualTo("status",1)
                .andLessThan("createTime",time);
        List<Orders> orders = ordersMapper.selectByExample(example);

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getAlipayConfig());
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            for (Orders order : orders) {
                bizContent.put("out_trade_no", order.getOrderId());
                request.setBizContent(bizContent.toString());
                AlipayTradeQueryResponse response = alipayClient.execute(request);

                if ("TRADE_SUCCESS".equalsIgnoreCase(response.getTradeStatus())){
                    // 支付成功 修改status = 2
                    Orders updateOrder = new Orders();
                    updateOrder.setOrderId(order.getOrderId());
                    updateOrder.setStatus("2");
                    ordersMapper.updateByPrimaryKeySelective(updateOrder);
                }else if("40004".equals(response.getCode()) || "WAIT_BUYER_PAY".equalsIgnoreCase(response.getTradeStatus())){
                    // 取消支付
                    AlipayTradeCancelRequest alipayTradeCancelRequest = new AlipayTradeCancelRequest();
                    alipayTradeCancelRequest.setBizContent(bizContent.toString());
                    alipayClient.execute(alipayTradeCancelRequest);
                    orderService.closeOrder(order.getOrderId());
                }
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }



    }
}
