package com.rafaelfarias.uol_bank_api.notification.application.port.out;

import com.rafaelfarias.uol_bank_api.notification.domain.Notification;

/**
 * Porta de saída para envio de notificações. A implementação concreta (e-mail, SMS,
 * push, etc.) é um detalhe de adapter — aqui usamos um adapter de log (mock).
 */
public interface NotificationPort {

    void send(Notification notification);
}
