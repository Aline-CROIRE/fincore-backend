package com.fincore.transaction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transactionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinCore Transaction Service API")
                        .description("Financial Operations, Money Transfers, and Audit History with RabbitMQ Event Bus")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FinCore Engineering")
                                .email("engineering@fincore.com")));
    }
}