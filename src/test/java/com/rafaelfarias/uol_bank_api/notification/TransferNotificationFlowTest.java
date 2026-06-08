package com.rafaelfarias.uol_bank_api.notification;

import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.notification.application.port.out.NotificationPort;
import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.TransferFundsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Verifica, de ponta a ponta, que a notificação dispara APÓS o commit de uma
 * transferência bem-sucedida (uma para cada cliente) e NÃO dispara quando a
 * transferência falha e sofre rollback.
 */
@SpringBootTest
class TransferNotificationFlowTest {

    @Autowired
    private CreateAccountUseCase createAccountUseCase;

    @Autowired
    private TransferFundsUseCase transferFundsUseCase;

    @MockitoBean
    private NotificationPort notificationPort;

    @Test
    void deveNotificarAmbosClientesAposTransferenciaConcluida() {
        Account source = createAccountUseCase.create("NotifOrigem", new BigDecimal("200.00"));
        Account target = createAccountUseCase.create("NotifDestino", new BigDecimal("0.00"));

        transferFundsUseCase.transfer(source.getId(), target.getId(), new BigDecimal("50.00"));

        verify(notificationPort, times(2)).send(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void naoDeveNotificarQuandoTransferenciaFalha() {
        Account source = createAccountUseCase.create("SemSaldo", new BigDecimal("10.00"));
        Account target = createAccountUseCase.create("Destino2", new BigDecimal("0.00"));

        assertThatThrownBy(() ->
                transferFundsUseCase.transfer(source.getId(), target.getId(), new BigDecimal("100.00")))
                .isInstanceOf(BusinessRuleException.class);

        verify(notificationPort, never()).send(org.mockito.ArgumentMatchers.any());
    }
}
