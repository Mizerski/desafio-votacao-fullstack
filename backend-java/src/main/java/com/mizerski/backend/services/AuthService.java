package com.mizerski.backend.services;

import com.mizerski.backend.dtos.request.LoginRequest;
import com.mizerski.backend.dtos.request.RegisterRequest;
import com.mizerski.backend.dtos.response.AuthResponse;
import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço de autenticação.
 * Responsável por login, registro e operações relacionadas à autenticação.
 */
public interface AuthService {

    /**
     * Realiza o login do usuário
     * 
     * @param request Dados de login
     * @return Result com resposta de autenticação ou erro
     */
    Result<AuthResponse> login(LoginRequest request);

    /**
     * Registra um novo usuário
     * 
     * @param request Dados de registro
     * @return Result com resposta de autenticação ou erro
     */
    Result<AuthResponse> register(RegisterRequest request);

    /**
     * Atualiza o token usando refresh token
     * 
     * @param refreshToken Token de refresh
     * @return Result com nova resposta de autenticação ou erro
     */
    Result<AuthResponse> refreshToken(String refreshToken);

    /**
     * Realiza logout do usuário (invalidação do token)
     * 
     * @param token Token a ser invalidado
     * @return Result indicando sucesso ou erro
     */
    Result<Void> logout(String token);

    /**
     * Verifica se um token é válido
     * 
     * @param token Token a ser verificado
     * @return Result indicando se o token é válido
     */
    Result<Boolean> validateToken(String token);

    /**
     * Obtém informações do usuário a partir do token
     * 
     * @param token Token JWT
     * @return Result com informações do usuário ou erro
     */
    Result<AuthResponse.UserInfo> getUserFromToken(String token);
}