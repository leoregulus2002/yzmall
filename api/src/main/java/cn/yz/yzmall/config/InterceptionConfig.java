package cn.yz.yzmall.config;

import cn.yz.yzmall.interception.CheckTokenInterception;
import cn.yz.yzmall.interception.SetTimeInterception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptionConfig implements WebMvcConfigurer {
    @Autowired
    private CheckTokenInterception checkTokenInterception;
    @Autowired
    private SetTimeInterception setTimeInterception;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkTokenInterception)
                .addPathPatterns("/shopcart/**")
                .addPathPatterns("/useraddr/**")
                .addPathPatterns("/order/**")
                .addPathPatterns("/user/check");
        registry.addInterceptor(setTimeInterception)
                .addPathPatterns("/**");
    }
}
