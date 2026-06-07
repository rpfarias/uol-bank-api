package com.rafaelfarias.uol_bank_api.transfer.application.port.out;

import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;

/**
 * Porta de saída para persistência das transferências realizadas.
 */
public interface TransferRepositoryPort {

    Transfer save(Transfer transfer);
}
