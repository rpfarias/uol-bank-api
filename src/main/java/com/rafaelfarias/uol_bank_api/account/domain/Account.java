package com.rafaelfarias.uol_bank_api.account.domain;

import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Modelo de domínio (aggregate root) de uma conta de cliente.
 * Concentra as regras de saldo (débito/crédito); é independente de JPA/web.
 */
@Getter
public class Account {

    private final Long id;
    private final String name;
    private BigDecimal balance;

    private Account(Long id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    /** Cria uma nova conta (ainda sem id), validando os dados de entrada. */
    public static Account create(String name, BigDecimal initialBalance) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Nome do cliente e obrigatorio");
        }
        if (initialBalance == null || initialBalance.signum() < 0) {
            throw new BusinessRuleException("Saldo inicial nao pode ser negativo");
        }
        return new Account(null, name.trim(), initialBalance);
    }

    /** Reconstrói uma conta já persistida (vindo do adapter de persistência). */
    public static Account restore(Long id, String name, BigDecimal balance) {
        return new Account(id, name, balance);
    }

    /** Debita um valor do saldo. Lança {@link BusinessRuleException} se insuficiente. */
    public void debit(BigDecimal amount) {
        validatePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new BusinessRuleException("Saldo insuficiente");
        }
        this.balance = this.balance.subtract(amount);
    }

    /** Credita um valor no saldo. */
    public void credit(BigDecimal amount) {
        validatePositive(amount);
        this.balance = this.balance.add(amount);
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("Valor da operacao deve ser positivo");
        }
    }
}
