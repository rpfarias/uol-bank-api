package com.rafaelfarias.uol_bank_api.account.config;

import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Pré-carrega uma base de clientes na inicialização (apenas se ainda não houver contas),
 * atendendo ao requisito de base pré-carregada de clientes.
 */
@Component
@RequiredArgsConstructor
public class AccountDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AccountDataLoader.class);

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;

    @Override
    public void run(String... args) {
        if (!getAccountUseCase.getAll().isEmpty()) {
            return;
        }
        createAccountUseCase.create("Alice", new BigDecimal("1000.00"));
        createAccountUseCase.create("Bob", new BigDecimal("500.00"));
        createAccountUseCase.create("Carol", new BigDecimal("0.00"));
        log.info("Base de contas pre-carregada com {} clientes", getAccountUseCase.getAll().size());
    }
}
