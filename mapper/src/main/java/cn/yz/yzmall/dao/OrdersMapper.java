package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.Orders;
import cn.yz.yzmall.entity.OrdersVO;
import cn.yz.yzmall.general.GeneralDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersMapper extends GeneralDao<Orders> {
    List<OrdersVO> selectOrders(@Param("userId") String userId,
                                @Param("status") String status,
                                @Param("start") int start,
                                @Param("limit") int limit);
}