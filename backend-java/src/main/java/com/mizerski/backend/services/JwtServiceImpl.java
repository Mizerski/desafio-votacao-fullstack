package com.mizerski.backend.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mizerski.backend.models.entities.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço JWT para gerenciamento de tokens.
 * Utiliza a biblioteca JJWT para operações com JWT.
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas em millisegundos
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 dias em millisegundos
    private long refreshExpiration;

    /**
     * Gera um token JWT para o usuário
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Gera um token JWT com claims extras
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Gera um refresh token
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Constrói o token JWT com os claims e tempo de expiração especificados
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {

        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails cannot be null");
        }

        // Adiciona claims específicos do usuário
        if (userDetails instanceof UserEntity userEntity) {
            extraClaims.put("userId", userEntity.getId());
            extraClaims.put("role", userEntity.getRole().name());
            extraClaims.put("name", userEntity.getName());
        }

        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + expiration);

        String username = userDetails.getUsername();
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();

        log.debug("Token JWT gerado para usuário: {}", username);
        return token;
    }

    /**
     * Extrai o username (email) do token
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai o ID do usuário do token
     */
    @Override
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extrai o role do usuário do token
     */
    @Override
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extrai um claim específico do token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Verifica se o token é válido para o usuário
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida um refresh token
     */
    @Override
    public boolean isRefreshTokenValid(String refreshToken, UserDetails userDetails) {
        try {
            final String username = extractUsername(refreshToken);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(refreshToken);
        } catch (Exception e) {
            log.warn("Refresh token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se o token expirou
     */
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai todos os claims do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtém a chave de assinatura
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Obtém o tempo de expiração do token em segundos
     */
    @Override
    public long getExpirationTime() {
        return jwtExpiration / 1000; // Converte de millisegundos para segundos
    }
}