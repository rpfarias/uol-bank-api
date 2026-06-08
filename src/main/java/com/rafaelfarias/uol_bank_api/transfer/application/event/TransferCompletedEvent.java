package com.rafaelfarias.uol_bank_api.transfer.application.event;

import java.math.BigDecimal;

/**
 * Evento de contrato publicado quando uma transferência é concluída com sucesso.
 * Consumido por outros módulos (ex.: notificação) após o commit da transação.
 */
public record TransferCompletedEvent(
        Long transferId,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount
) {
}
