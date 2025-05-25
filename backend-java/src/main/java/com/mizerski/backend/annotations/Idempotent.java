package com.mizerski.backend.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar métodos que precisam de tratamento de idempotência.
 * Evita operações duplicadas e melhora a performance da aplicação.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * Chave para identificar a operação idempotente.
     * Se não especificada, será gerada automaticamente baseada nos parâmetros.
     */
    String key() default "";

    /**
     * Tempo de expiração do cache de idempotência em segundos.
     * Padrão: 300 segundos (5 minutos)
     */
    int expireAfterSeconds() default 300;

    /**
     * Se deve incluir o ID do usuário na chave de idempotência.
     * Útil para operações específicas por usuário.
     */
    boolean includeUserId() default false;
}