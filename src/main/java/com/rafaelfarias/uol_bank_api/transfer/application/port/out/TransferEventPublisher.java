package com.rafaelfarias.uol_bank_api.transfer.application.port.out;

import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;

/**
 * Porta de saída para publicar eventos de transferência concluída.
 * Mantém o {@code TransferService} desacoplado do mecanismo de eventos do Spring.
 */
public interface TransferEventPublisher {

    void publishCompleted(Transfer transfer);
}
