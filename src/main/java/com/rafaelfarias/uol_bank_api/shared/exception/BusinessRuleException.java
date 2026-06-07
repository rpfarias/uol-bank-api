package com.rafaelfarias.uol_bank_api.shared.exception;

/**
 * Lançada quando uma regra de negócio é violada (ex.: saldo insuficiente,
 * transferência para a mesma conta). Mapeada para HTTP 422.
 */
public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
