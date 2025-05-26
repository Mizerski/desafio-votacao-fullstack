package com.mizerski.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuração do OpenAPI/Swagger com suporte à autenticação JWT.
 * Permite testar endpoints protegidos diretamente no Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Configura a documentação OpenAPI com autenticação JWT
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .addServersItem(createLocalServer())
                .addSecurityItem(createSecurityRequirement())
                .components(createComponents());
    }

    /**
     * Cria informações básicas da API
     */
    private Info createApiInfo() {
        return new Info()
                .title("Sistema de Votação - API")
                .description(
                        """
                                API REST para sistema de votação com pautas e sessões.

                                ##  Autenticação
                                Esta API utiliza **JWT (Bearer Token)** para autenticação.

                                ### Como usar:
                                1. **Registre-se** ou **faça login** usando os endpoints `/api/auth/register` ou `/api/auth/login`
                                2. **Copie o token** retornado no campo `token` da resposta
                                3. **Clique no botão "Authorize" 🔒** no topo desta página
                                4. **Cole o token** no campo (sem o prefixo "Bearer ")
                                5. **Clique em "Authorize"** e depois **"Close"**
                                6. Agora você pode **testar todos os endpoints protegidos**!

                                ##  Funcionalidades
                                - **Autenticação JWT** com refresh tokens
                                - **Gerenciamento de usuários** com roles (USER, ADMIN, MODERATOR)
                                - **Criação de pautas** para votação
                                - **Sessões de votação** com tempo configurável
                                - **Registro de votos** com validações de duplicação
                                - **Result Pattern** para tratamento robusto de erros
                                - **Idempotência** em operações críticas

                                """)
                .version("1.0.0")
                .contact(createContact());
    }

    /**
     * Cria informações de contato
     */
    private Contact createContact() {
        return new Contact()
                .name("Equipe de Desenvolvimento")
                .url("https://github.com/Mizerski/desafio-votacao-fullstack/tree/main/backend-java");
    }

    /**
     * Cria servidor local para desenvolvimento
     */
    private Server createLocalServer() {
        return new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desenvolvimento");
    }

    /**
     * Cria requisito de segurança para JWT
     */
    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME);
    }

    /**
     * Cria componentes de segurança
     */
    private Components createComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme());
    }

    /**
     * Cria esquema de segurança JWT
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description(
                        """
                                **JWT Bearer Token**

                                Para obter um token:
                                1. Use `/api/auth/login` com suas credenciais
                                2. Ou use `/api/auth/register` para criar uma nova conta
                                3. Copie o valor do campo `token` da resposta
                                4. Cole aqui (sem o prefixo "Bearer ")

                                **Exemplo de token:**
                                ```
                                eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiVVNFUiIsIm5hbWUiOiJKb8OjbyBTaWx2YSIsInVzZXJJZCI6IjEyMzQ1Njc4LTkwYWItY2RlZi0xMjM0LTU2Nzg5MGFiY2RlZiIsInN1YiI6ImpvYW9AZXhhbXBsZS5jb20iLCJpYXQiOjE3MDk1NjQ4MDAsImV4cCI6MTcwOTY1MTIwMH0.example
                                ```
                                """);
    }
}