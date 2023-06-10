package com.dorandoran.doranserver.config;

import com.dorandoran.doranserver.dto.UserInfoDto;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class Swagger3Config {

    @Bean
    public Docket api(TypeResolver typeResolver) {
        return new Docket(DocumentationType.OAS_30)
                .additionalModels(typeResolver.resolve(UserInfoDto.class))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dorandoran.doranserver.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("도란도란 API 문서")
                .description("도란도란 서버가 제공하는 모든 API를 문서화한 페이지 입니다.")
                .version("1.0")
                .termsOfServiceUrl("https://www.example.com")
                .contact(new Contact("대표 연락처", "https://github.com/jang-199", "jw1010110@gmail.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }
}
