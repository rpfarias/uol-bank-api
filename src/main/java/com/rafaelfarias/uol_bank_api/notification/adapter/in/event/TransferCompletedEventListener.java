package com.rafaelfarias.uol_bank_api.notification.adapter.in.event;

import com.rafaelfarias.uol_bank_api.notification.application.port.in.SendTransferNotificationUseCase;
import com.rafaelfarias.uol_bank_api.notification.application.port.in.SendTransferNotificationUseCase.TransferNotificationCommand;
import com.rafaelfarias.uol_bank_api.transfer.application.event.TransferCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Adapter de entrada (event-driven): reage ao evento de transferência concluída
 * SOMENTE após o commit da transação, garantindo que a notificação só é enviada
 * quando a transferência foi de fato persistida.
 */
@Component
@RequiredArgsConstructor
public class TransferCompletedEventListener {

    private final SendTransferNotificationUseCase sendTransferNotificationUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        sendTransferNotificationUseCase.notifyTransferCompleted(new TransferNotificationCommand(
                event.transferId(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.amount()));
    }
}
