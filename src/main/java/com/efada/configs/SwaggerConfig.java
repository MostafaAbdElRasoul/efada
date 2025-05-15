package com.efada.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("Efada APIs")
                .version("1.0")
                .description("API Documentation for Efada App"));
    }
    
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
            .group("Efada APIs")
            .pathsToMatch("/api/v1/users/**")
            .build();
    }
    
    @Bean
    public GroupedOpenApi sessionApi() {
        return GroupedOpenApi.builder()
            .group("Session APIs")
            .pathsToMatch("/api/v1/sessions/**")
            .build();
    }
    
    @Bean
    public GroupedOpenApi registrationApi() {
        return GroupedOpenApi.builder()
            .group("Registration APIs")
            .pathsToMatch("/api/v1/registrations/**")
            .build();
    }
    
    @Bean
    public GroupedOpenApi conferenceApi() {
        return GroupedOpenApi.builder()
            .group("Conferences APIs")
            .pathsToMatch("/api/v1/conferences/**")
            .build();
    }
}
