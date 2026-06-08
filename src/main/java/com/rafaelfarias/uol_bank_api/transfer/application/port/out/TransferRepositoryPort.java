package com.rafaelfarias.uol_bank_api.transfer.application.port.out;

import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;

import java.util.List;

/**
 * Porta de saída para persistência das transferências realizadas.
 */
public interface TransferRepositoryPort {

    Transfer save(Transfer transfer);

    /** Transferências em que a conta participa (origem ou destino), mais recentes primeiro. */
    List<Transfer> findByAccountId(Long accountId);
}
