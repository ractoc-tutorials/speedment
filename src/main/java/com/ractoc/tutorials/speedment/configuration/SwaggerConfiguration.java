package com.ractoc.tutorials.speedment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket produceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ractoc.mycollection.backend"))
                .paths(testPaths()::test)
                .build();
    }

   private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("My-Catalogue Rest APIs")
                .description("This page lists all the rest apis for My-Catalogue endpoint.")
                .version("1.0.0")
                .build();
    }

    private Predicate<String> testPaths() {
        return testPath("/catalogue.*").and((testPath("/error.*")).negate());
    }

    private Predicate<String> testPath(String s) {
        return PathSelectors.regex(s)::apply;
    }
}
