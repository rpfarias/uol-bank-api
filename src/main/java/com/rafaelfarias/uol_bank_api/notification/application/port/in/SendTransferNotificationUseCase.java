package com.rafaelfarias.uol_bank_api.notification.application.port.in;

import java.math.BigDecimal;

/**
 * Porta de entrada: notificar os clientes envolvidos em uma transferência concluída.
 */
public interface SendTransferNotificationUseCase {

    void notifyTransferCompleted(TransferNotificationCommand command);

    /**
     * Dados necessários para compor as notificações de uma transferência.
     */
    record TransferNotificationCommand(
            Long transferId,
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal amount
    ) {
    }
}
