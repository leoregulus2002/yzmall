package cn.yz.yzmall.controller;


import cn.yz.yzmall.entity.ShoppingCart;
import cn.yz.yzmall.service.ShoppingCartService;
import cn.yz.yzmall.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopcart")
@CrossOrigin
@Tag(name = "购物车接口管理")
public class ShopCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping ("/add")
    @Operation(summary = "添加购物车")
    public ResultVO addShoppingCart(@RequestBody ShoppingCart cart,@RequestHeader("token")String token){
        return shoppingCartService.addShoppingCart(cart);
    }
    @Operation(summary = "购物车列表")
    @Parameters({
            @Parameter(name = "userId",description ="用户Id",required = true),
    })
    @GetMapping("/list")
    private ResultVO list(Integer userId,@RequestHeader("token")String token){
        return shoppingCartService.listShoppingCartByUserId(userId);
    }

    @Operation(summary = "修改购物车数量")
    @PutMapping("/update/{cid}/{cnum}")
    private ResultVO update(@PathVariable("cid") Integer cartId,
                            @PathVariable("cnum") Integer cartNum,
                            @RequestHeader("token")String token){
        return shoppingCartService.updateShoppingCartNumByUserId(cartId,cartNum);
    }

    @Operation(summary = "选中的购物车列表")
    @Parameters({
            @Parameter(name = "cids",description ="购物车集合",required = true),
    })
    @GetMapping("/listbycids")
    private ResultVO list(String cids, @RequestHeader("token")String token){
        return shoppingCartService.listShoppingcartsByCids(cids);
    }
}


