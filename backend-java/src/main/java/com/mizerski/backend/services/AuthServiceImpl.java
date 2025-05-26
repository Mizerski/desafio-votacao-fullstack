package com.mizerski.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.request.LoginRequest;
import com.mizerski.backend.dtos.request.RegisterRequest;
import com.mizerski.backend.dtos.response.AuthResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço de autenticação.
 * Utiliza Result Pattern para tratamento de erros sem exceptions.
 * Delega criação de usuários para UserService mantendo responsabilidade única.
 */
@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ExceptionMappingService exceptionMappingService;
    private final UserService userService;

    public AuthServiceImpl(
            UserRepository userRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            ExceptionMappingService exceptionMappingService,
            UserService userService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.exceptionMappingService = exceptionMappingService;
        this.userService = userService;
    }

    /**
     * Realiza o login do usuário
     */
    @Override
    public Result<AuthResponse> login(LoginRequest request) {
        try {
            // Validação de entrada
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return Result.error("INVALID_EMAIL", "Email é obrigatório");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return Result.error("INVALID_PASSWORD", "Senha é obrigatória");
            }

            // Busca o usuário
            UserEntity user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                    .orElse(null);

            if (user == null) {
                return Result.error("INVALID_CREDENTIALS", "Email ou senha inválidos");
            }

            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            if (!authentication.isAuthenticated()) {
                return Result.error("INVALID_CREDENTIALS", "Email ou senha inválidos");
            }

            // Atualiza último login
            user.updateLastLogin();
            userRepository.save(user);

            // Gera tokens
            String accessToken = jwtService.generateToken(user);

            // Cria resposta
            AuthResponse.UserInfo userInfo = createUserInfo(user);
            AuthResponse response = AuthResponse.createBearerToken(
                    accessToken,
                    jwtService.getExpirationTime(),
                    userInfo);

            log.info("Login realizado com sucesso para usuário: {}", user.getEmail());
            return Result.success(response);

        } catch (AuthenticationException e) {
            log.warn("Falha na autenticação para email: {}", request.getEmail());
            return Result.error("INVALID_CREDENTIALS", "Email ou senha inválidos");
        } catch (Exception e) {
            log.error("Erro inesperado durante login: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Registra um novo usuário delegando criação para UserService
     */
    @Override
    @Idempotent(expireAfterSeconds = 300, includeUserId = false)
    public Result<AuthResponse> register(RegisterRequest request) {
        try {
            // Converte RegisterRequest para CreateUserRequest
            CreateUserRequest createUserRequest = CreateUserRequest.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .document(request.getDocument())
                    .role(request.getRole())
                    .build();

            // Delega criação do usuário para UserService
            Result<UserResponse> userResult = userService.createUser(createUserRequest);

            if (userResult.isError()) {
                // Propaga o erro do UserService
                return Result.error(userResult.getErrorCode().orElse("USER_CREATION_FAILED"),
                        userResult.getErrorMessage().orElse("Erro ao criar usuário"));
            }

            UserResponse userResponse = userResult.getValue().orElse(null);
            if (userResponse == null) {
                return Result.error("USER_CREATION_FAILED", "Erro ao criar usuário");
            }

            // Busca o usuário criado para gerar token
            UserEntity user = userRepository.findByEmailAndIsActiveTrue(userResponse.getEmail())
                    .orElse(null);

            if (user == null) {
                return Result.error("USER_NOT_FOUND", "Usuário criado não encontrado");
            }

            // Gera tokens (responsabilidade do AuthService)
            String accessToken = jwtService.generateToken(user);

            // Cria resposta de autenticação
            AuthResponse.UserInfo userInfo = createUserInfo(user);
            AuthResponse response = AuthResponse.createBearerToken(
                    accessToken,
                    jwtService.getExpirationTime(),
                    userInfo);

            log.info("Usuário registrado e autenticado com sucesso: {}", user.getEmail());
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao registrar usuário: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Atualiza o token usando refresh token
     */
    @Override
    public Result<AuthResponse> refreshToken(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return Result.error("INVALID_REFRESH_TOKEN", "Refresh token é obrigatório");
            }

            String userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail == null) {
                return Result.error("INVALID_REFRESH_TOKEN", "Refresh token inválido");
            }

            UserEntity user = userRepository.findByEmailAndIsActiveTrue(userEmail)
                    .orElse(null);

            if (user == null) {
                return Result.error("USER_NOT_FOUND", "Usuário não encontrado");
            }

            if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
                return Result.error("INVALID_REFRESH_TOKEN", "Refresh token inválido ou expirado");
            }

            // Gera novos tokens
            String newAccessToken = jwtService.generateToken(user);

            // Cria resposta
            AuthResponse.UserInfo userInfo = createUserInfo(user);
            AuthResponse response = AuthResponse.createBearerToken(
                    newAccessToken,
                    jwtService.getExpirationTime(),
                    userInfo);

            log.info("Token atualizado com sucesso para usuário: {}", user.getEmail());
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao atualizar token: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Realiza logout do usuário (invalidação do token)
     */
    @Override
    public Result<Void> logout(String token) {
        try {
            // Em uma implementação real, você poderia adicionar o token a uma blacklist
            // Por simplicidade, apenas validamos se o token é válido
            if (token == null || token.trim().isEmpty()) {
                return Result.error("INVALID_TOKEN", "Token é obrigatório");
            }

            String userEmail = jwtService.extractUsername(token);
            if (userEmail == null) {
                return Result.error("INVALID_TOKEN", "Token inválido");
            }

            log.info("Logout realizado para usuário: {}", userEmail);
            return Result.success(null);

        } catch (Exception e) {
            log.error("Erro durante logout: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Verifica se um token é válido
     */
    @Override
    public Result<Boolean> validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return Result.success(false);
            }

            String userEmail = jwtService.extractUsername(token);
            if (userEmail == null) {
                return Result.success(false);
            }

            UserEntity user = userRepository.findByEmailAndIsActiveTrue(userEmail)
                    .orElse(null);

            if (user == null) {
                return Result.success(false);
            }

            boolean isValid = jwtService.isTokenValid(token, user);
            return Result.success(isValid);

        } catch (Exception e) {
            log.warn("Erro ao validar token: {}", e.getMessage());
            return Result.success(false);
        }
    }

    /**
     * Obtém informações do usuário a partir do token
     */
    @Override
    public Result<AuthResponse.UserInfo> getUserFromToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return Result.error("INVALID_TOKEN", "Token é obrigatório");
            }

            String userEmail = jwtService.extractUsername(token);
            if (userEmail == null) {
                return Result.error("INVALID_TOKEN", "Token inválido");
            }

            UserEntity user = userRepository.findByEmailAndIsActiveTrue(userEmail)
                    .orElse(null);

            if (user == null) {
                return Result.error("USER_NOT_FOUND", "Usuário não encontrado");
            }

            if (!jwtService.isTokenValid(token, user)) {
                return Result.error("INVALID_TOKEN", "Token inválido ou expirado");
            }

            AuthResponse.UserInfo userInfo = createUserInfo(user);
            return Result.success(userInfo);

        } catch (Exception e) {
            log.error("Erro ao obter usuário do token: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Cria objeto UserInfo a partir da entidade User
     */
    private AuthResponse.UserInfo createUserInfo(UserEntity user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .lastLogin(user.getLastLogin())
                .build();
    }
}