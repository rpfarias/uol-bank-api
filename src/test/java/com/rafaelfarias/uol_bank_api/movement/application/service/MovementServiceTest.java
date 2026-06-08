package com.rafaelfarias.uol_bank_api.movement.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.movement.domain.Movement;
import com.rafaelfarias.uol_bank_api.movement.domain.MovementType;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.FindTransfersUseCase;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @Mock
    private FindTransfersUseCase findTransfersUseCase;

    @InjectMocks
    private MovementService movementService;

    @Test
    void deveProjetarTransferenciasComoDebitoECredito() {
        Long accountId = 1L;
        when(getAccountUseCase.getById(accountId))
                .thenReturn(Account.restore(accountId, "Alice", new BigDecimal("100.00")));
        // 1->2 (saída/DEBIT) e 3->1 (entrada/CREDIT) na perspectiva da conta 1
        when(findTransfersUseCase.findByAccount(accountId)).thenReturn(List.of(
                Transfer.restore(10L, 1L, 2L, new BigDecimal("30.00"), Instant.now()),
                Transfer.restore(11L, 3L, 1L, new BigDecimal("50.00"), Instant.now())
        ));

        List<Movement> statement = movementService.getStatement(accountId);

        assertThat(statement).hasSize(2);

        Movement saida = statement.get(0);
        assertThat(saida.type()).isEqualTo(MovementType.DEBIT);
        assertThat(saida.counterpartyAccountId()).isEqualTo(2L);
        assertThat(saida.amount()).isEqualByComparingTo("30.00");

        Movement entrada = statement.get(1);
        assertThat(entrada.type()).isEqualTo(MovementType.CREDIT);
        assertThat(entrada.counterpartyAccountId()).isEqualTo(3L);
        assertThat(entrada.amount()).isEqualByComparingTo("50.00");
    }

    @Test
    void deveLancarNotFoundQuandoContaNaoExiste() {
        when(getAccountUseCase.getById(99L))
                .thenThrow(new ResourceNotFoundException("Conta 99 nao encontrada"));

        assertThatThrownBy(() -> movementService.getStatement(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
