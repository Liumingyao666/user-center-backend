package com.liumingyao.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 自定义Swagger接口文档的配置
 *
 * 表示这个类是个配置类，会把这个类注入到IOC容器中
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描路径，获取controller层的接口
                .apis(RequestHandlerSelectors.basePackage("com.liumingyao.usercenter.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    /**
     * api信息
     *
     * @return
     */
    public ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //标题
                .title("用户中心")
                //简介
                .description("用户中心接口文档")
                //作者、网址http:localhost:8088/doc.html(这里注意端口号要与项目一致，如果你的端口号后面还加了前缀，就需要把前缀加上)、邮箱
                .contact(new Contact("liumingyao","http:localhost:8080/doc.html","1806859182@qq.com"))
                //版本
                .version("1.0")
                .build();
    }
}


