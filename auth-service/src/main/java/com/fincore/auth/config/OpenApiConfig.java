package com.fincore.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinCore Auth Service API")
                        .description("Identity and JWT Authentication Management for FinCore Digital Banking Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FinCore Engineering")
                                .email("engineering@fincore.com")));
    }
}