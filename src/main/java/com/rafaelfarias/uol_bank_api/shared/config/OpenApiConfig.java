package com.rafaelfarias.uol_bank_api.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI/Swagger da aplicação.
 * A UI fica disponível em /swagger-ui.html e o contrato em /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI uolBankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UOL Bank API")
                        .description("API REST de banco digital: gestão de contas, transferência de fundos "
                                + "entre contas, consulta de movimentações financeiras e notificações.")
                        .version("v1")
                        .contact(new Contact().name("Rafael Farias"))
                        .license(new License().name("MIT")));
    }
}
