package cn.yz.yzmall.interception;

import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class CheckTokenInterception implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)){
            return true;
        }

        String token = request.getHeader("token");
        if (token == null){
            ResultVO resultVO = new ResultVO(ResStatus.AUTH_FAIL_NOT, "请先登陆", null);
            doResponse(response,resultVO);
        }else {
            String s = stringRedisTemplate.opsForValue().get(token);
            if (s == null){
                ResultVO resultVO = new ResultVO(ResStatus.AUTH_FAIL_NOT, "请先登陆", null);
                doResponse(response,resultVO);
            }else {
                stringRedisTemplate.boundValueOps(token).expire(30, TimeUnit.MINUTES);
                return true;
            }
        }
        return false;

    }

    private void doResponse(HttpServletResponse response, ResultVO resultVO) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(resultVO);
        out.print(s);
        out.flush();
        out.close();


    }
}
