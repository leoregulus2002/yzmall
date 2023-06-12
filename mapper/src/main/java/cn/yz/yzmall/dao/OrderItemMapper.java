package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.OrderItem;
import cn.yz.yzmall.general.GeneralDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemMapper extends GeneralDao<OrderItem> {
    List<OrderItem> listOrderItemByOrderId(String orderId);
}