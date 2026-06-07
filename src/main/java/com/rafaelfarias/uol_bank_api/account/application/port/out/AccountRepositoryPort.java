package com.rafaelfarias.uol_bank_api.account.application.port.out;

import com.rafaelfarias.uol_bank_api.account.domain.Account;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de contas. Mantém o domínio/aplicação
 * desacoplados de JPA — implementada pelo adapter de persistência.
 */
public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findById(Long id);

    /**
     * Carrega a conta adquirindo lock pessimista de escrita (PESSIMISTIC_WRITE).
     * Deve ser chamado dentro de uma transação ativa (ex.: transferência).
     */
    Optional<Account> findByIdForUpdate(Long id);

    List<Account> findAll();
}
