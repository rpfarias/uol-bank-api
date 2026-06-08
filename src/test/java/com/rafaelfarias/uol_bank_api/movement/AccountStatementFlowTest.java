package com.rafaelfarias.uol_bank_api.movement;

import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.movement.application.port.in.GetAccountStatementUseCase;
import com.rafaelfarias.uol_bank_api.movement.domain.Movement;
import com.rafaelfarias.uol_bank_api.movement.domain.MovementType;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.TransferFundsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fluxo real: após transferências, o extrato de cada conta reflete as movimentações
 * com o tipo (DEBIT/CREDIT) e a contraparte corretos.
 */
@SpringBootTest
class AccountStatementFlowTest {

    @Autowired
    private CreateAccountUseCase createAccountUseCase;

    @Autowired
    private TransferFundsUseCase transferFundsUseCase;

    @Autowired
    private GetAccountStatementUseCase getAccountStatementUseCase;

    @Test
    void deveRefletirTransferenciaNoExtratoDeAmbasAsContas() {
        Account alice = createAccountUseCase.create("ExtratoAlice", new BigDecimal("100.00"));
        Account bob = createAccountUseCase.create("ExtratoBob", new BigDecimal("0.00"));

        transferFundsUseCase.transfer(alice.getId(), bob.getId(), new BigDecimal("40.00"));

        List<Movement> aliceStatement = getAccountStatementUseCase.getStatement(alice.getId());
        assertThat(aliceStatement).hasSize(1);
        assertThat(aliceStatement.get(0).type()).isEqualTo(MovementType.DEBIT);
        assertThat(aliceStatement.get(0).counterpartyAccountId()).isEqualTo(bob.getId());
        assertThat(aliceStatement.get(0).amount()).isEqualByComparingTo("40.00");

        List<Movement> bobStatement = getAccountStatementUseCase.getStatement(bob.getId());
        assertThat(bobStatement).hasSize(1);
        assertThat(bobStatement.get(0).type()).isEqualTo(MovementType.CREDIT);
        assertThat(bobStatement.get(0).counterpartyAccountId()).isEqualTo(alice.getId());
    }
}
