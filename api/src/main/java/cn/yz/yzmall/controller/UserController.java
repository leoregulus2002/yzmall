package cn.yz.yzmall.controller;

import cn.yz.yzmall.entity.Users;
import cn.yz.yzmall.service.UserService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

//    @ApiOperation("用户登陆")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "username",value ="用户登陆账号",required = true),
//            @ApiImplicitParam(dataType = "string",name = "password",value ="用户登陆密码",required = true),
//    })
    @GetMapping("/login")
    public ResultVO login(@RequestParam("username") String username,
                          @RequestParam("password") String password){
        return userService.checkLogin(username,password);
    }
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name = "username",value ="用户注册账号",required = true),
            @ApiImplicitParam(dataType = "string",name = "password",value ="用户注册密码",required = true),
    })
    @PostMapping("/regist")
    public ResultVO regist(@RequestBody Users users){
        return userService.userResgit(users.getUsername(),users.getPassword());
    }

    @Operation(summary = "校验token")
    @PostMapping("/check")
    public ResultVO userTokenCheck(@RequestHeader("token")String token){
        return new ResultVO(ResStatus.OK,"success",null);
    }

}
