package com.rafaelfarias.uol_bank_api.account.application.port.in;

import com.rafaelfarias.uol_bank_api.account.domain.Account;

import java.math.BigDecimal;

/**
 * Porta de entrada: cadastro básico de conta (ID gerado, Nome, Saldo Inicial).
 */
public interface CreateAccountUseCase {

    Account create(String name, BigDecimal initialBalance);
}
