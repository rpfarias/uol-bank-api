package com.rafaelfarias.uol_bank_api.account.application.port.in;

import com.rafaelfarias.uol_bank_api.account.domain.Account;

import java.util.List;

/**
 * Porta de entrada: consulta de contas.
 */
public interface GetAccountUseCase {

    Account getById(Long id);

    List<Account> getAll();
}
