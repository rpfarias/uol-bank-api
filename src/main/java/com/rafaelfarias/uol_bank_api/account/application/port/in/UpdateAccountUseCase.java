package com.rafaelfarias.uol_bank_api.account.application.port.in;

import com.rafaelfarias.uol_bank_api.account.domain.Account;

/**
 * Porta de entrada usada por outros módulos (ex.: transferência) para alterar saldo
 * de contas de forma consistente. Expõe a carga com lock pessimista e a persistência,
 * sem vazar a entidade JPA do módulo de contas.
 */
public interface UpdateAccountUseCase {

    /**
     * Carrega a conta com lock pessimista de escrita. Deve ser invocado dentro da
     * transação do chamador para que o lock seja mantido até o commit.
     *
     * @throws com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException se a conta não existir
     */
    Account loadForUpdate(Long id);

    /** Persiste as alterações de saldo de uma conta. */
    Account save(Account account);
}
