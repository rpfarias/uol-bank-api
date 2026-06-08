package com.rafaelfarias.uol_bank_api.notification.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.notification.application.port.in.SendTransferNotificationUseCase;
import com.rafaelfarias.uol_bank_api.notification.application.port.out.NotificationPort;
import com.rafaelfarias.uol_bank_api.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Compõe e despacha as notificações dos clientes envolvidos em uma transferência.
 * Usa a porta de entrada de contas para enriquecer a mensagem com os nomes.
 */
@Service
@RequiredArgsConstructor
public class NotificationService implements SendTransferNotificationUseCase {

    private final NotificationPort notificationPort;
    private final GetAccountUseCase getAccountUseCase;

    @Override
    public void notifyTransferCompleted(TransferNotificationCommand command) {
        Account source = getAccountUseCase.getById(command.sourceAccountId());
        Account target = getAccountUseCase.getById(command.targetAccountId());

        notificationPort.send(new Notification(
                source.getId(),
                source.getName(),
                "Voce transferiu R$ %s para %s.".formatted(command.amount(), target.getName())));

        notificationPort.send(new Notification(
                target.getId(),
                target.getName(),
                "Voce recebeu R$ %s de %s.".formatted(command.amount(), source.getName())));
    }
}
