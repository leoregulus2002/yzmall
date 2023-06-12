package cn.yz.yzmall.service;

import cn.yz.yzmall.vo.ResultVO;

public interface UserAddrService {

    ResultVO listAddrsByUserId(int userId);
}
