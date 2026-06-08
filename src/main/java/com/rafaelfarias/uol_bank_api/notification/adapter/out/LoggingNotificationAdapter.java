package com.rafaelfarias.uol_bank_api.notification.adapter.out;

import com.rafaelfarias.uol_bank_api.notification.application.port.out.NotificationPort;
import com.rafaelfarias.uol_bank_api.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementação mock da porta de notificação: registra a notificação em log,
 * simulando o envio para o cliente (e-mail/SMS/push).
 */
@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void send(Notification notification) {
        log.info("[NOTIFICACAO] Conta {} ({}): {}",
                notification.recipientAccountId(),
                notification.recipientName(),
                notification.message());
    }
}
