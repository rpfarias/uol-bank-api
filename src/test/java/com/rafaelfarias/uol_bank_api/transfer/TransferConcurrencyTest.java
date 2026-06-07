package com.rafaelfarias.uol_bank_api.transfer;

import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.TransferFundsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prova de consistência sob concorrência: múltiplas threads transferindo da mesma
 * conta em paralelo. O lock pessimista deve serializar os débitos, sem perda de
 * atualização (lost update) e sem saldo negativo. O total do sistema é conservado.
 */
@SpringBootTest
class TransferConcurrencyTest {

    @Autowired
    private CreateAccountUseCase createAccountUseCase;

    @Autowired
    private GetAccountUseCase getAccountUseCase;

    @Autowired
    private TransferFundsUseCase transferFundsUseCase;

    @Test
    void deveManterConsistenciaSobTransferenciasConcorrentes() throws Exception {
        Account source = createAccountUseCase.create("Origem", new BigDecimal("1000.00"));
        Account target = createAccountUseCase.create("Destino", new BigDecimal("0.00"));

        int transferencias = 200;
        BigDecimal valor = new BigDecimal("5.00");

        ExecutorService pool = Executors.newFixedThreadPool(16);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < transferencias; i++) {
            tasks.add(() -> {
                transferFundsUseCase.transfer(source.getId(), target.getId(), valor);
                return null;
            });
        }

        List<Future<Void>> futures = pool.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }
        pool.shutdown();

        BigDecimal totalMovido = valor.multiply(BigDecimal.valueOf(transferencias));
        BigDecimal saldoOrigem = getAccountUseCase.getById(source.getId()).getBalance();
        BigDecimal saldoDestino = getAccountUseCase.getById(target.getId()).getBalance();

        assertThat(saldoOrigem).isEqualByComparingTo(new BigDecimal("1000.00").subtract(totalMovido));
        assertThat(saldoDestino).isEqualByComparingTo(totalMovido);
        // total conservado no sistema
        assertThat(saldoOrigem.add(saldoDestino)).isEqualByComparingTo("1000.00");
    }
}
