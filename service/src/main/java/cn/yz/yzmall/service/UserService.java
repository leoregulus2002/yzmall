package cn.yz.yzmall.service;

import cn.yz.yzmall.vo.ResultVO;
import org.springframework.stereotype.Service;

public interface UserService {

    ResultVO userResgit(String username,String password);
    ResultVO checkLogin(String username,String password);
}
