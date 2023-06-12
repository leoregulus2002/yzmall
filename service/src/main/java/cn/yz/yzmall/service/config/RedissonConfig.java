package cn.yz.yzmall.service.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("#{'${redisson.cluster.nodes}'.split(',')}")
    private String[] nodeAddress;
    @Value("${redisson.password}")
    private String password;
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(nodeAddress)
                .setPassword(password);
//        config.useSingleServer()
//                .setPassword(password)
//                .setAddress("redis://192.168.190.100:6380");
        return Redisson.create(config);
    }
}
