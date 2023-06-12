package cn.yz.yzmall.controller;

import cn.yz.yzmall.service.UserAddrService;
import cn.yz.yzmall.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Api(value = "用户地址接口",tags = "用户地址接口管理")
@RequestMapping("/useraddr")
public class UserAddrController {
    @Autowired
    private UserAddrService userAddrService;


    @GetMapping("/list")
    @ApiOperation("地址列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer",name = "userId",value ="用户Id",required = true),
    })
    public ResultVO listAddr(Integer userId,@RequestHeader("token")String token){
        return userAddrService.listAddrsByUserId(userId);
    }
}
