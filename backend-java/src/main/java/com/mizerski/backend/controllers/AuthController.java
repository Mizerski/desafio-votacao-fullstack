package com.mizerski.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.LoginRequest;
import com.mizerski.backend.dtos.request.RegisterRequest;
import com.mizerski.backend.dtos.response.AuthResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.AuthService;
import com.mizerski.backend.services.ErrorMappingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por operações de autenticação.
 * Inclui login, registro, refresh token e validação de tokens.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação e autorização")
@Slf4j
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(AuthService authService, ErrorMappingService errorMappingService) {
        super(errorMappingService);
        this.authService = authService;
    }

    /**
     * Realiza login do usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Tentativa de login para email: {}", request.getEmail());

        Result<AuthResponse> result = authService.login(request);

        logResult("Login", request.getEmail(), result);

        return handleGetOperation(result);
    }

    /**
     * Registra novo usuário
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Cria nova conta de usuário e retorna token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email ou documento já cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @Idempotent(expireAfterSeconds = 300, includeUserId = false)
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Tentativa de registro para email: {}", request.getEmail());

        Result<AuthResponse> result = authService.register(request);

        logResult("Registro", request.getEmail(), result);

        if (result.isSuccess()) {
            return ResponseEntity.status(201).body(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Atualiza token usando refresh token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Atualizar token", description = "Gera novo token JWT usando refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido"),
            @ApiResponse(responseCode = "400", description = "Refresh token não fornecido")
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @Parameter(description = "Refresh token", required = true) @RequestParam String refreshToken) {

        log.info("Tentativa de refresh token");

        Result<AuthResponse> result = authService.refreshToken(refreshToken);

        logResult("Refresh Token", "token", result);

        return handleGetOperation(result);
    }

    /**
     * Realiza logout do usuário
     */
    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Invalida o token JWT atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Token JWT no header Authorization", required = true) @RequestHeader("Authorization") String authorizationHeader) {

        log.info("Tentativa de logout");

        // Extrai o token do header Authorization
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Result<Void> result = authService.logout(token);

        logResult("Logout", "token", result);

        return handleDeleteOperation(result);
    }

    /**
     * Valida se um token é válido
     */
    @GetMapping("/validate")
    @Operation(summary = "Validar token", description = "Verifica se o token JWT é válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validado"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    public ResponseEntity<Boolean> validateToken(
            @Parameter(description = "Token JWT no header Authorization", required = true) @RequestHeader("Authorization") String authorizationHeader) {

        log.debug("Validação de token solicitada");

        // Extrai o token do header Authorization
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Result<Boolean> result = authService.validateToken(token);

        return handleGetOperation(result);
    }

    /**
     * Obtém informações do usuário a partir do token
     */
    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário", description = "Retorna informações do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do usuário retornados"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<AuthResponse.UserInfo> getCurrentUser(
            @Parameter(description = "Token JWT no header Authorization", required = true) @RequestHeader("Authorization") String authorizationHeader) {

        log.debug("Solicitação de dados do usuário atual");

        // Extrai o token do header Authorization
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Result<AuthResponse.UserInfo> result = authService.getUserFromToken(token);

        return handleGetOperation(result);
    }

    /**
     * Endpoint de health check para autenticação
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se o serviço de autenticação está funcionando")
    @ApiResponse(responseCode = "200", description = "Serviço funcionando")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running");
    }
}