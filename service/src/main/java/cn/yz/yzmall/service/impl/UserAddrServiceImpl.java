package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.UserAddrMapper;
import cn.yz.yzmall.entity.UserAddr;
import cn.yz.yzmall.service.UserAddrService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserAddrServiceImpl implements UserAddrService {

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    public ResultVO listAddrsByUserId(int userId) {
        Example example = new Example(UserAddr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId)
                .andEqualTo("status",1);
        List<UserAddr> userAddrs = userAddrMapper.selectByExample(example);
        return new ResultVO(ResStatus.OK,"success",userAddrs);
    }
}
