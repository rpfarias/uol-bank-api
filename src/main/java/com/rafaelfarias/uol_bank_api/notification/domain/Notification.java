package com.rafaelfarias.uol_bank_api.notification.domain;

/**
 * Notificação destinada a um cliente.
 */
public record Notification(
        Long recipientAccountId,
        String recipientName,
        String message
) {
}
