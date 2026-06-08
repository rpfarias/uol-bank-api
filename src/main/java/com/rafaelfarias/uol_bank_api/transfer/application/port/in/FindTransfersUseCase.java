package com.rafaelfarias.uol_bank_api.transfer.application.port.in;

import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;

import java.util.List;

/**
 * Porta de entrada: consulta de transferências de uma conta. Exposta a outros
 * módulos (ex.: movimentações) sem vazar a entidade JPA.
 */
public interface FindTransfersUseCase {

    List<Transfer> findByAccount(Long accountId);
}
