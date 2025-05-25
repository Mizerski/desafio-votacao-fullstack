package com.mizerski.backend.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

/**
 * Anotação para validar se uma string é um UUID válido.
 * Utiliza regex para verificar o formato padrão de UUID.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
public @interface ValidUUID {

    /**
     * Mensagem de erro padrão
     */
    String message() default "Deve ser um UUID válido";

    /**
     * Grupos de validação
     */
    Class<?>[] groups() default {};

    /**
     * Payload para metadados
     */
    Class<? extends Payload>[] payload() default {};
}