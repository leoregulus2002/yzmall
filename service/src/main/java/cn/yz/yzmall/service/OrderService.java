package cn.yz.yzmall.service;

import cn.yz.yzmall.entity.Orders;
import cn.yz.yzmall.vo.ResultVO;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public interface OrderService {

    Map<String,String> addOrder(String cid, Orders orders) throws ParseException, SQLException;

    int updateOrderStatus(String orderId,String status);

    void closeOrder(String orderId);
    ResultVO listOrder(String userId,String status,int pageNum,int limit);
}
