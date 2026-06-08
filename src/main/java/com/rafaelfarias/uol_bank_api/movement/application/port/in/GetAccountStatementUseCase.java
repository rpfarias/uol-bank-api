package com.rafaelfarias.uol_bank_api.movement.application.port.in;

import com.rafaelfarias.uol_bank_api.movement.domain.Movement;

import java.util.List;

/**
 * Porta de entrada: consulta do extrato (movimentações) de uma conta.
 */
public interface GetAccountStatementUseCase {

    List<Movement> getStatement(Long accountId);
}
