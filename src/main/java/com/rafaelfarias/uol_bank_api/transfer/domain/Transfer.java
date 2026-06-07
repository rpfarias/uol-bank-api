package com.rafaelfarias.uol_bank_api.transfer.domain;

import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Registro de uma transferência de fundos entre duas contas.
 * Valida as invariantes da operação no momento da criação.
 */
@Getter
public class Transfer {

    private final Long id;
    private final Long sourceAccountId;
    private final Long targetAccountId;
    private final BigDecimal amount;
    private final Instant createdAt;

    private Transfer(Long id, Long sourceAccountId, Long targetAccountId, BigDecimal amount, Instant createdAt) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    /** Cria uma nova transferência validando contas e valor. */
    public static Transfer create(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        if (sourceAccountId == null || targetAccountId == null) {
            throw new BusinessRuleException("Conta de origem e destino sao obrigatorias");
        }
        if (sourceAccountId.equals(targetAccountId)) {
            throw new BusinessRuleException("Conta de origem e destino devem ser diferentes");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("Valor da transferencia deve ser positivo");
        }
        return new Transfer(null, sourceAccountId, targetAccountId, amount, Instant.now());
    }

    /** Reconstrói uma transferência já persistida. */
    public static Transfer restore(Long id, Long sourceAccountId, Long targetAccountId,
                                   BigDecimal amount, Instant createdAt) {
        return new Transfer(id, sourceAccountId, targetAccountId, amount, createdAt);
    }
}
