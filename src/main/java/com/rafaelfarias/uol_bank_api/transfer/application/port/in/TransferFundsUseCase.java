package com.rafaelfarias.uol_bank_api.transfer.application.port.in;

import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;

import java.math.BigDecimal;

/**
 * Porta de entrada: transferência de fundos entre duas contas.
 */
public interface TransferFundsUseCase {

    Transfer transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount);
}
