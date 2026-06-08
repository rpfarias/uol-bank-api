package com.rafaelfarias.uol_bank_api.transfer.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.UpdateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
import com.rafaelfarias.uol_bank_api.transfer.application.port.out.TransferEventPublisher;
import com.rafaelfarias.uol_bank_api.transfer.application.port.out.TransferRepositoryPort;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private UpdateAccountUseCase accountPort;

    @Mock
    private TransferRepositoryPort transferRepository;

    @Mock
    private TransferEventPublisher eventPublisher;

    @InjectMocks
    private TransferService transferService;

    @Test
    void deveTransferirDebitandoOrigemECreditandoDestino() {
        Account source = Account.restore(1L, "Alice", new BigDecimal("100.00"));
        Account target = Account.restore(2L, "Bob", new BigDecimal("50.00"));
        when(accountPort.loadForUpdate(1L)).thenReturn(source);
        when(accountPort.loadForUpdate(2L)).thenReturn(target);
        when(transferRepository.save(org.mockito.ArgumentMatchers.any(Transfer.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        transferService.transfer(1L, 2L, new BigDecimal("30.00"));

        assertThat(source.getBalance()).isEqualByComparingTo("70.00");
        assertThat(target.getBalance()).isEqualByComparingTo("80.00");
        verify(accountPort).save(source);
        verify(accountPort).save(target);
        verify(transferRepository).save(org.mockito.ArgumentMatchers.any(Transfer.class));
        verify(eventPublisher).publishCompleted(org.mockito.ArgumentMatchers.any(Transfer.class));
    }

    @Test
    void naoDeveTransferirComSaldoInsuficiente() {
        Account source = Account.restore(1L, "Alice", new BigDecimal("10.00"));
        Account target = Account.restore(2L, "Bob", new BigDecimal("50.00"));
        when(accountPort.loadForUpdate(1L)).thenReturn(source);
        when(accountPort.loadForUpdate(2L)).thenReturn(target);

        assertThatThrownBy(() -> transferService.transfer(1L, 2L, new BigDecimal("30.00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("insuficiente");

        assertThat(target.getBalance()).isEqualByComparingTo("50.00");
        verify(transferRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(eventPublisher, never()).publishCompleted(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void devePropagarNotFoundQuandoContaOrigemNaoExiste() {
        when(accountPort.loadForUpdate(1L))
                .thenThrow(new ResourceNotFoundException("Conta 1 nao encontrada"));

        assertThatThrownBy(() -> transferService.transfer(1L, 2L, new BigDecimal("30.00")))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(transferRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveAdquirirLocksDaMenorParaMaiorIdMesmoComOrigemMaior() {
        Account source = Account.restore(5L, "Alice", new BigDecimal("100.00"));
        Account target = Account.restore(2L, "Bob", new BigDecimal("50.00"));
        when(accountPort.loadForUpdate(5L)).thenReturn(source);
        when(accountPort.loadForUpdate(2L)).thenReturn(target);
        when(transferRepository.save(org.mockito.ArgumentMatchers.any(Transfer.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        transferService.transfer(5L, 2L, new BigDecimal("10.00"));

        // a conta de menor id (2) deve ser travada antes da de maior id (5)
        ArgumentCaptor<Long> order = ArgumentCaptor.forClass(Long.class);
        verify(accountPort, org.mockito.Mockito.times(2)).loadForUpdate(order.capture());
        assertThat(order.getAllValues()).containsExactly(2L, 5L);
    }
}
