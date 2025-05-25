package com.mizerski.backend.services;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface para serviço de gerenciamento de tokens JWT.
 * Responsável por gerar, validar e extrair informações dos tokens.
 */
public interface JwtService {

    /**
     * Gera um token JWT para o usuário
     * 
     * @param userDetails Detalhes do usuário
     * @return Token JWT gerado
     */
    String generateToken(UserDetails userDetails);

    /**
     * Gera um token JWT com claims extras
     * 
     * @param extraClaims Claims adicionais
     * @param userDetails Detalhes do usuário
     * @return Token JWT gerado
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Extrai o username (email) do token
     * 
     * @param token Token JWT
     * @return Username extraído
     */
    String extractUsername(String token);

    /**
     * Extrai o ID do usuário do token
     * 
     * @param token Token JWT
     * @return ID do usuário
     */
    String extractUserId(String token);

    /**
     * Extrai o role do usuário do token
     * 
     * @param token Token JWT
     * @return Role do usuário
     */
    String extractUserRole(String token);

    /**
     * Verifica se o token é válido para o usuário
     * 
     * @param token       Token JWT
     * @param userDetails Detalhes do usuário
     * @return true se o token é válido
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Verifica se o token expirou
     * 
     * @param token Token JWT
     * @return true se o token expirou
     */
    boolean isTokenExpired(String token);

    /**
     * Obtém o tempo de expiração do token em segundos
     * 
     * @return Tempo de expiração em segundos
     */
    long getExpirationTime();

    /**
     * Gera um refresh token
     * 
     * @param userDetails Detalhes do usuário
     * @return Refresh token gerado
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Valida um refresh token
     * 
     * @param refreshToken Token de refresh
     * @param userDetails  Detalhes do usuário
     * @return true se o refresh token é válido
     */
    boolean isRefreshTokenValid(String refreshToken, UserDetails userDetails);
}