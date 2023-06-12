package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.ShoppingCart;
import cn.yz.yzmall.entity.ShoppingCartVO;
import cn.yz.yzmall.general.GeneralDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartMapper extends GeneralDao<ShoppingCart> {

    List<ShoppingCartVO> selectShopcartByUserId(int userId);
    int updateCartNumByCartId(@Param("cartId") int cartId, @Param("cartNum") int cartNum);

    List<ShoppingCartVO> selectShopcartByCid(List<Integer> cids);
}