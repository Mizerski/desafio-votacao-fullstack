package com.mizerski.backend.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.ErrorMappingService;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe base para controllers que padroniza tratamento de erros.
 */
@Slf4j
public abstract class BaseController {

    @Autowired
    protected ErrorMappingService errorMappingService;

    /**
     * Cria ResponseEntity para operações de criação com Result pattern
     * 
     * @param result   Resultado da operação
     * @param idGetter Função para extrair ID do objeto criado
     * @return ResponseEntity com status 201 e Location header
     */
    protected <T> ResponseEntity<?> handleCreateOperation(Result<T> result,
            java.util.function.Function<T, String> idGetter) {
        if (result.isSuccess()) {
            T value = result.getValue().orElse(null);
            if (value != null) {
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(idGetter.apply(value))
                        .toUri();

                return ResponseEntity.created(location).body(value);
            }
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Cria ResponseEntity para operações de busca com Result pattern
     * 
     * @param result Resultado da operação
     * @return ResponseEntity com status apropriado
     */
    protected <T> ResponseEntity<T> handleGetOperation(Result<T> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Cria ResponseEntity para operações de atualização com Result pattern
     * 
     * @param result Resultado da operação
     * @return ResponseEntity com status apropriado
     */
    protected <T> ResponseEntity<T> handleUpdateOperation(Result<T> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Cria ResponseEntity para operações de deleção
     * 
     * @param success Se a operação foi bem-sucedida
     * @return ResponseEntity com status 204 (No Content) ou erro
     */
    protected ResponseEntity<Void> handleDeleteOperation(boolean success) {
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Cria ResponseEntity para operações de deleção com Result pattern
     * 
     * @param result Resultado da operação de deleção
     * @return ResponseEntity com status apropriado
     */
    protected ResponseEntity<Void> handleDeleteOperation(Result<Void> result) {
        if (result.isSuccess()) {
            return ResponseEntity.noContent().build();
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Log simples para operações
     * 
     * @param operation  Nome da operação
     * @param identifier Identificador do recurso
     * @param success    Se a operação foi bem-sucedida
     */
    protected void logOperation(String operation, String identifier, boolean success) {
        if (success) {
            log.info("{} executado com sucesso: {}", operation, identifier);
        } else {
            log.warn("Falha em {}: {}", operation, identifier);
        }
    }

    /**
     * Log estruturado para operações de busca
     * 
     * @param operation  Nome da operação
     * @param identifier Identificador do recurso
     */
    protected void logQuery(String operation, String identifier) {
        log.debug("Executando {} para: {}", operation, identifier);
    }

    /**
     * Log para operações com Result pattern
     * 
     * @param operation  Nome da operação
     * @param identifier Identificador do recurso
     * @param result     Resultado da operação
     */
    protected <T> void logResult(String operation, String identifier, Result<T> result) {
        if (result.isSuccess()) {
            log.info("{} executado com sucesso: {}", operation, identifier);
        } else {
            log.warn("Falha em {}: {} - Erro: {}", operation, identifier,
                    result.getErrorMessage().orElse("Erro desconhecido"));
        }
    }
}