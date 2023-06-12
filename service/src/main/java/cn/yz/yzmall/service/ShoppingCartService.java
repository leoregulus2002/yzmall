package cn.yz.yzmall.service;

import cn.yz.yzmall.entity.ShoppingCart;
import cn.yz.yzmall.vo.ResultVO;

public interface ShoppingCartService {
    ResultVO addShoppingCart(ShoppingCart shoppingCart);
    ResultVO listShoppingCartByUserId(int userId);

    ResultVO updateShoppingCartNumByUserId(int cartId,int cartNum);

    ResultVO listShoppingcartsByCids(String cids);
}
