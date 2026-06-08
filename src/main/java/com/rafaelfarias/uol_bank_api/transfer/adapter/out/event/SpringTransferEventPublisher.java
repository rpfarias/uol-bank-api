package com.rafaelfarias.uol_bank_api.transfer.adapter.out.event;

import com.rafaelfarias.uol_bank_api.transfer.application.event.TransferCompletedEvent;
import com.rafaelfarias.uol_bank_api.transfer.application.port.out.TransferEventPublisher;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter que publica o evento de transferência via {@link ApplicationEventPublisher}.
 * O evento é emitido dentro da transação; os listeners AFTER_COMMIT só disparam
 * quando a transferência é efetivamente persistida.
 */
@Component
@RequiredArgsConstructor
public class SpringTransferEventPublisher implements TransferEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishCompleted(Transfer transfer) {
        applicationEventPublisher.publishEvent(new TransferCompletedEvent(
                transfer.getId(),
                transfer.getSourceAccountId(),
                transfer.getTargetAccountId(),
                transfer.getAmount()));
    }
}
