package com.rafaelfarias.uol_bank_api.shared.web;

import java.time.Instant;
import java.util.List;

/**
 * Representação padronizada de erro retornada pela API.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldValidationError> fieldErrors
) {

    /**
     * Detalhe de erro de validação associado a um campo específico da requisição.
     */
    public record FieldValidationError(String field, String message) {
    }
}
