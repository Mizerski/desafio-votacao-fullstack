package com.mizerski.backend.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Serviço especializado para tratamento centralizado de exceções JWT.
 * Segue os padrões arquiteturais do projeto para tratamento de erros.
 */
@Service
@Slf4j
public class JwtExceptionHandlerService {

    private final Map<String, BiConsumer<String, String>> exceptionHandlers;

    /**
     * Construtor que inicializa os handlers de exceções JWT
     */
    public JwtExceptionHandlerService() {
        this.exceptionHandlers = new HashMap<>();
        initializeExceptionHandlers();
    }

    /**
     * Inicializa os mapeamentos de exceções para handlers específicos
     */
    private void initializeExceptionHandlers() {
        exceptionHandlers.put("ExpiredJwtException", this::handleExpiredToken);
        exceptionHandlers.put("MalformedJwtException", this::handleMalformedToken);
        exceptionHandlers.put("UnsupportedJwtException", this::handleUnsupportedToken);
        exceptionHandlers.put("SecurityException", this::handleInvalidSignature);
        exceptionHandlers.put("JwtException", this::handleInvalidSignature);
        exceptionHandlers.put("IllegalArgumentException", this::handleInvalidArgument);
    }

    /**
     * Trata exceções relacionadas ao processamento de tokens JWT
     * Retorna true se a exceção foi tratada com sucesso, false caso contrário
     * 
     * @param exception Exceção a ser tratada
     * @param userEmail Email do usuário (pode ser null se não extraído)
     * @return true se a exceção foi tratada
     */
    public boolean handleJwtException(Exception exception, String userEmail) {
        String exceptionName = exception.getClass().getSimpleName();

        BiConsumer<String, String> handler = exceptionHandlers.get(exceptionName);
        if (handler != null) {
            handler.accept(userEmail, exception.getMessage());
            return true;
        }

        // Handler padrão para exceções não mapeadas
        handleUnexpectedError(exception);
        return false;
    }

    /**
     * Trata tokens expirados
     * 
     * @param userEmail Email do usuário (pode ser null)
     * @param message   Mensagem da exceção
     */
    private void handleExpiredToken(String userEmail, String message) {
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            log.warn("Token JWT expirado para usuário: {}", userEmail);
        } else {
            log.warn("Token JWT expirado: {}", message);
        }
    }

    /**
     * Trata tokens malformados
     * 
     * @param userEmail Email do usuário (não usado neste caso)
     * @param message   Mensagem da exceção
     */
    private void handleMalformedToken(String userEmail, String message) {
        log.warn("Token JWT malformado: {}", message);
    }

    /**
     * Trata tokens não suportados
     * 
     * @param userEmail Email do usuário (não usado neste caso)
     * @param message   Mensagem da exceção
     */
    private void handleUnsupportedToken(String userEmail, String message) {
        log.warn("Token JWT não suportado: {}", message);
    }

    /**
     * Trata assinaturas inválidas
     * 
     * @param userEmail Email do usuário (não usado neste caso)
     * @param message   Mensagem da exceção
     */
    private void handleInvalidSignature(String userEmail, String message) {
        log.warn("Assinatura JWT inválida: {}", message);
    }

    /**
     * Trata argumentos inválidos
     * 
     * @param userEmail Email do usuário (não usado neste caso)
     * @param message   Mensagem da exceção
     */
    private void handleInvalidArgument(String userEmail, String message) {
        log.warn("Token JWT com argumento inválido: {}", message);
    }

    /**
     * Trata erros inesperados
     */
    private void handleUnexpectedError(Exception exception) {
        log.warn("Erro inesperado ao processar token JWT: {}", exception.getMessage());
    }

    /**
     * Adiciona um novo handler para uma exceção específica
     * 
     * @param exceptionName Nome da classe da exceção
     * @param handler       Handler que recebe userEmail e message
     */
    public void addExceptionHandler(String exceptionName, BiConsumer<String, String> handler) {
        exceptionHandlers.put(exceptionName, handler);
    }

    /**
     * Remove um handler de exceção
     * 
     * @param exceptionName Nome da classe da exceção
     */
    public void removeExceptionHandler(String exceptionName) {
        exceptionHandlers.remove(exceptionName);
    }

    /**
     * Verifica se existe handler para uma exceção
     * 
     * @param exceptionName Nome da classe da exceção
     * @return true se existe handler
     */
    public boolean hasHandler(String exceptionName) {
        return exceptionHandlers.containsKey(exceptionName);
    }

    /**
     * Obtém todos os handlers configurados
     * 
     * @return Map com todos os handlers
     */
    public Map<String, BiConsumer<String, String>> getAllHandlers() {
        return new HashMap<>(exceptionHandlers);
    }

    /**
     * Extrai o email do usuário de uma ExpiredJwtException se possível
     * 
     * @param exception Exceção expirada
     * @return Email do usuário ou null se não disponível
     */
    public String extractUserEmailFromExpiredException(Exception exception) {
        try {
            if ("ExpiredJwtException".equals(exception.getClass().getSimpleName())) {
                // Usando reflection para acessar claims de forma segura
                var method = exception.getClass().getMethod("getClaims");
                var claims = method.invoke(exception);
                if (claims != null) {
                    var subjectMethod = claims.getClass().getMethod("getSubject");
                    return (String) subjectMethod.invoke(claims);
                }
            }
        } catch (Exception e) {
            log.debug("Não foi possível extrair email da exceção: {}", e.getMessage());
        }
        return null;
    }
}