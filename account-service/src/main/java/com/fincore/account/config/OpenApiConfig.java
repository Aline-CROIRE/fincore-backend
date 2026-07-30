package com.fincore.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinCore Bank Account Service API")
                        .description("Financial Accounts Management and Balance System with Idempotency Support")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FinCore Engineering")
                                .email("engineering@fincore.com")));
    }
}