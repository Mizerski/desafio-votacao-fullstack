package com.mizerski.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.LoginRequest;
import com.mizerski.backend.dtos.request.RegisterRequest;
import com.mizerski.backend.dtos.response.AuthResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.enums.UserRole;
import com.mizerski.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço de autenticação.
 * Utiliza Result Pattern para tratamento de erros sem exceptions.
 */
@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ExceptionMappingService exceptionMappingService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            ExceptionMappingService exceptionMappingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.exceptionMappingService = exceptionMappingService;
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
     * Registra um novo usuário
     */
    @Override
    @Idempotent(expireAfterSeconds = 300, includeUserId = false)
    public Result<AuthResponse> register(RegisterRequest request) {
        try {
            // Validações de negócio
            if (userRepository.existsByEmail(request.getEmail())) {
                return Result.error("DUPLICATE_EMAIL", "Email já cadastrado");
            }

            if (request.getDocument() != null && !request.getDocument().trim().isEmpty()) {
                if (userRepository.existsByDocument(request.getDocument())) {
                    return Result.error("DUPLICATE_DOCUMENT", "Documento já cadastrado");
                }
            }

            // Cria o usuário
            UserEntity user = UserEntity.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .document(request.getDocument())
                    .role(request.getRole() != null ? request.getRole() : UserRole.USER)
                    .isActive(true)
                    .isEmailVerified(false)
                    .isAccountNonExpired(true)
                    .isAccountNonLocked(true)
                    .isCredentialsNonExpired(true)
                    .build();

            UserEntity savedUser = userRepository.save(user);

            // Gera tokens
            String accessToken = jwtService.generateToken(savedUser);

            // Cria resposta
            AuthResponse.UserInfo userInfo = createUserInfo(savedUser);
            AuthResponse response = AuthResponse.createBearerToken(
                    accessToken,
                    jwtService.getExpirationTime(),
                    userInfo);

            log.info("Usuário registrado com sucesso: {}", savedUser.getEmail());
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