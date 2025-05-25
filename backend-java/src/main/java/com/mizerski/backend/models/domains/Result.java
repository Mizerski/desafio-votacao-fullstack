package com.mizerski.backend.models.domains;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Classe para representar resultados de operações sem usar exceptions.
 * Melhora a performance evitando o custo de stack traces.
 * 
 * @param <T> Tipo do valor de sucesso
 */
public class Result<T> {

    private final T value;
    private final String errorMessage;
    private final String errorCode;
    private final boolean success;

    private Result(T value, String errorMessage, String errorCode, boolean success) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.success = success;
    }

    /**
     * Cria um resultado de sucesso
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, null, true);
    }

    /**
     * Cria um resultado de erro
     */
    public static <T> Result<T> error(String errorCode, String errorMessage) {
        return new Result<>(null, errorMessage, errorCode, false);
    }

    /**
     * Cria um resultado de erro apenas com mensagem
     */
    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(null, errorMessage, "GENERIC_ERROR", false);
    }

    /**
     * Verifica se a operação foi bem-sucedida
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Verifica se houve erro
     */
    public boolean isError() {
        return !success;
    }

    /**
     * Obtém o valor se sucesso, senão retorna Optional.empty()
     */
    public Optional<T> getValue() {
        return success ? Optional.ofNullable(value) : Optional.empty();
    }

    /**
     * Obtém o valor ou lança exception se erro
     */
    public T getValueOrThrow() {
        if (!success) {
            throw new RuntimeException(errorMessage);
        }
        return value;
    }

    /**
     * Obtém o valor ou retorna um valor padrão
     */
    public T getValueOrDefault(T defaultValue) {
        return success ? value : defaultValue;
    }

    /**
     * Obtém a mensagem de erro
     */
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    /**
     * Obtém o código de erro
     */
    public Optional<String> getErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    /**
     * Executa uma ação se sucesso
     */
    public Result<T> onSuccess(Consumer<T> action) {
        if (success && value != null) {
            action.accept(value);
        }
        return this;
    }

    /**
     * Executa uma ação se erro
     */
    public Result<T> onError(Consumer<String> action) {
        if (!success) {
            action.accept(errorMessage);
        }
        return this;
    }

    /**
     * Mapeia o valor se sucesso
     */
    public <U> Result<U> map(Function<T, U> mapper) {
        if (success) {
            try {
                return Result.success(mapper.apply(value));
            } catch (Exception e) {
                return Result.error("MAPPING_ERROR", "Erro ao mapear resultado: " + e.getMessage());
            }
        }
        return Result.error(errorCode, errorMessage);
    }

    /**
     * FlatMap para encadear operações
     */
    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (success) {
            try {
                return mapper.apply(value);
            } catch (Exception e) {
                return Result.error("FLATMAP_ERROR", "Erro ao executar flatMap: " + e.getMessage());
            }
        }
        return Result.error(errorCode, errorMessage);
    }
}