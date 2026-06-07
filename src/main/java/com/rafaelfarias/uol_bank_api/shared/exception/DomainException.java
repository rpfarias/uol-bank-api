package com.rafaelfarias.uol_bank_api.shared.exception;

/**
 * Base para exceções de regra de negócio/domínio.
 * Permite que o {@code GlobalExceptionHandler} traduza erros de domínio
 * em respostas HTTP consistentes, sem que os módulos conheçam a camada web.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
