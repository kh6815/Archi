package com.architecture.archi.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Archi Swagger")
                .description("Archi 관한 REST API")
                .version("1.0.0");
    }

//    private final String devUrl;
//    private final String prodUrl;
//
//    public SwaggerConfig(
//            @Value("${votogether.openapi.dev-url}") final String devUrl,
//            @Value("${votogether.openapi.prod-url}") final String prodUrl
//    ) {
//        this.devUrl = devUrl;
//        this.prodUrl = prodUrl;
//    }
//
//    @Bean
//    public OpenAPI openAPI() {
//        final Server devServer = new Server();
//        devServer.setUrl(devUrl);
//        devServer.description("개발 환경 서버 URL");
//
//        final Server prodServer = new Server();
//        prodServer.setUrl(prodUrl);
//        prodServer.description("운영 환경 서버 URL");
//
//        final Info info = new Info()
//                .title("Archi Swagger")
//                .description("Archi 관한 REST API")
//                .version("v1.0.0");
//
//        return new OpenAPI()
//                .info(info)
//                .servers(List.of(devServer, prodServer));
//    }
}
