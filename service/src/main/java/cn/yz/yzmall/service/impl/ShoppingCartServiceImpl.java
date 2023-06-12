package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.ShoppingCartMapper;
import cn.yz.yzmall.entity.ShoppingCart;
import cn.yz.yzmall.entity.ShoppingCartVO;
import cn.yz.yzmall.service.ShoppingCartService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    @Override
    public ResultVO addShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.setCartTime(sdf.format(new Date()));
        int i = shoppingCartMapper.insert(shoppingCart);
        if (i>0){
            return new ResultVO(ResStatus.OK,"success",null);
        }else {
            return new ResultVO(ResStatus.NO,"添加失败",null);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ResultVO listShoppingCartByUserId(int userId) {
        List<ShoppingCartVO> shoppingCartVOS = shoppingCartMapper.selectShopcartByUserId(userId);
        return new ResultVO(ResStatus.OK,"success",shoppingCartVOS);
    }

    @Override
    public ResultVO updateShoppingCartNumByUserId(int cartId, int cartNum) {
        int i = shoppingCartMapper.updateCartNumByCartId(cartId, cartNum);
        if (i>0){
            return new ResultVO(ResStatus.OK,"success",null);
        }
        return new ResultVO(ResStatus.NO,"fail",null);
    }

    @Override
    public ResultVO listShoppingcartsByCids(String cids) {
        String[] split = cids.split(",");
        List<Integer> cartIds = new ArrayList<>();
        for (String s : split) {
            cartIds.add(Integer.parseInt(s));
        }
        List<ShoppingCartVO> shoppingCartVOS = shoppingCartMapper.selectShopcartByCid(cartIds);
        return new ResultVO(ResStatus.OK,"success",shoppingCartVOS);
    }
}
