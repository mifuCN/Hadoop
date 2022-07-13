package com.mifu.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Auther: mifu
 * @Date: 2022/07/03 09:51
 * @Description: SwaggerConfig
 * /swagger-ui.html
 */
@Configuration    //声明配置类
@EnableSwagger2 //开启Swagger注解配置
public class SwaggerConfig {

    @Bean   //配置Swagger插件
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)  //类型
                .groupName("HadoopWebApi")    //组名
                .apiInfo(webApiInfo())  //引入下面的方法
                .select()
                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))    //当路径中含有admin/error则就不会显示
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("Hadoop网盘API文档")
                .description("本文档描述了本次小学期期间所写的hadoop接口")
                .version("1.0")
                .contact(new Contact("mifu", "https://www.201314.tk/", "1755786251@qq.com"))
                .build();
    }
}

