package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.UsersMapper;
import cn.yz.yzmall.entity.Users;
import cn.yz.yzmall.service.UserService;
import cn.yz.yzmall.utils.MD5Utils;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Transactional
    public ResultVO userResgit(String username, String password) {
        synchronized (this){
            Example example = new Example(Users.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("username",username);
            List<Users> users = usersMapper.selectByExample(example);
            if (users.size() == 0){
                String md5Password = MD5Utils.md5(password);
                Users user = new Users();
                user.setUsername(username);
                user.setPassword(md5Password);
                user.setUserImg("/img/default.png");
                user.setUserRegtime(new Date());
                user.setUserModtime(new Date());
                int i = usersMapper.insertUseGeneratedKeys(user);
                if (i > 0){
                    return new ResultVO(ResStatus.OK,"注册成功",user);
                }else {
                    return new ResultVO(ResStatus.NO,"注册失败",null);
                }
            }else {
                return new ResultVO(ResStatus.NO,"用户名已被注册",null);
            }
        }

    }

    @Override
    public ResultVO checkLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        List<Users> users = usersMapper.selectByExample(example);
        if (users.size() == 0){
            return new ResultVO(ResStatus.NO,"用户名不存在",null);
        }else {
            String md5Password = MD5Utils.md5(password);
            if (md5Password.equals(users.get(0).getPassword())){
                Map<String,Object> map = new HashMap<>();
                String token = JWT.create()
                        .withClaim("username",username)
                        .withJWTId(users.get(0).getUserId()+"")
                        .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                        .sign(Algorithm.HMAC256("YZ6666"));
                try {
                    String userInfo = objectMapper.writeValueAsString(users.get(0));
                    stringRedisTemplate.boundValueOps(token).set(userInfo,30, TimeUnit.MINUTES);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return new ResultVO(ResStatus.OK,token, users.get(0));
            }else {
                return new ResultVO(ResStatus.NO,"账号密码错误！",null);
            }
        }
    }
}
