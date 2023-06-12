package cn.yz.yzmall.controller;

import cn.yz.yzmall.entity.Orders;
import cn.yz.yzmall.service.OrderService;
import cn.yz.yzmall.service.PayService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.alipay.v3.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/order")
@Tag(name = "订单接口管理")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PayService payService;
    @PostMapping("/add")
    public ResultVO add(String cids, @RequestBody Orders orders,@RequestHeader("token")String token){
        ResultVO resultVO = null;
        try {
            Map<String, String> map = orderService.addOrder(cids, orders);
            String orderId = map.get("orderId");
            if (orderId != null){
                resultVO = payService.pay(map);
            }else {
                resultVO = new ResultVO(ResStatus.NO,"订单提交失败",null);
            }

        } catch (ParseException e) {
        } catch (SQLException e){
            resultVO = new ResultVO(ResStatus.NO,"订单提交失败",null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return resultVO;

    }

    @GetMapping("/list")
    @Operation(summary = "订单列表")
    @Parameters({
            @Parameter(name = "userId",description = "用户id",required = true),
            @Parameter(name = "status",description = "订单状态"),
            @Parameter(name = "pageNum",description = "页码 ",required = true),
            @Parameter(name = "limit",description = "每页条数",required = true)
    })
    public ResultVO list(@RequestHeader("token")String token,String userId,String status,int pageNum,int limit){
        return orderService.listOrder(userId,status,pageNum,limit);
    }
}
