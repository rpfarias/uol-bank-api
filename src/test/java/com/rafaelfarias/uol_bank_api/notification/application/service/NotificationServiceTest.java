package com.rafaelfarias.uol_bank_api.notification.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.notification.application.port.in.SendTransferNotificationUseCase.TransferNotificationCommand;
import com.rafaelfarias.uol_bank_api.notification.application.port.out.NotificationPort;
import com.rafaelfarias.uol_bank_api.notification.domain.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationPort notificationPort;

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void deveNotificarOrigemEDestinoComMensagensCorretas() {
        when(getAccountUseCase.getById(1L)).thenReturn(Account.restore(1L, "Alice", new BigDecimal("100.00")));
        when(getAccountUseCase.getById(2L)).thenReturn(Account.restore(2L, "Bob", new BigDecimal("50.00")));

        notificationService.notifyTransferCompleted(
                new TransferNotificationCommand(10L, 1L, 2L, new BigDecimal("30.00")));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort, times(2)).send(captor.capture());

        Notification toSource = captor.getAllValues().get(0);
        Notification toTarget = captor.getAllValues().get(1);

        assertThat(toSource.recipientAccountId()).isEqualTo(1L);
        assertThat(toSource.message()).contains("transferiu", "30.00", "Bob");

        assertThat(toTarget.recipientAccountId()).isEqualTo(2L);
        assertThat(toTarget.message()).contains("recebeu", "30.00", "Alice");
    }
}
