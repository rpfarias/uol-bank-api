package com.rafaelfarias.uol_bank_api.movement.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Uma movimentação financeira do extrato de uma conta, derivada de uma transferência.
 */
public record Movement(
        Long transferId,
        MovementType type,
        Long counterpartyAccountId,
        BigDecimal amount,
        Instant createdAt
) {
}
