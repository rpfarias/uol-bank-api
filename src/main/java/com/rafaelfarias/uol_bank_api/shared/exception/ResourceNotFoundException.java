package com.rafaelfarias.uol_bank_api.shared.exception;

/**
 * Lançada quando um recurso solicitado não existe (ex.: conta inexistente).
 * Mapeada para HTTP 404.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
