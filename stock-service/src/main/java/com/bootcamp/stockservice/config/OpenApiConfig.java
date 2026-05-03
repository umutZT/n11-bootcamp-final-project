package com.bootcamp.stockservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8768")
                .description("Direct service access (development)");

        Server gatewayServer = new Server()
                .url("http://localhost:8763")
                .description("Via API Gateway (recommended)");

        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Paste JWT obtained from POST /api/user/signin");

        return new OpenAPI()
                .info(new Info()
                        .title("Stock Service API")
                        .version("1.0.0")
                        .description("Stock reservation ledger for saga-based order processing")
                        .contact(new Contact().name("Ecommerce Saga Team"))
                        .license(new License().name("Bootcamp Project")))
                .servers(List.of(gatewayServer, localServer))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme));
    }
}
