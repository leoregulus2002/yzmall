package cn.yz.yzmall.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI getDocket(){
        Info info = new Info().title("《言宗商城》后端接口说明")
                .description("详细说明了言宗商城后端接口规范")
                .version("v2.0.1")
                .contact(new Contact()
                        .name("yz")
                        .url("www.yz.cn")
                        .email("yz@yz.com"));

        return new OpenAPI().info(info).externalDocs(new ExternalDocumentation()
                .description("SpringDoc Wiki Documentation")
                .url("https://springdoc.org/v2"));
    }
}
