package com.rafaelfarias.uol_bank_api.account.adapter.in.web.dto;

import com.rafaelfarias.uol_bank_api.account.domain.Account;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Representação de saída de uma conta.
 */
public record AccountResponse(

        @Schema(description = "Identificador da conta", example = "1")
        Long id,

        @Schema(description = "Nome do cliente", example = "Alice")
        String name,

        @Schema(description = "Saldo atual da conta", example = "1000.00")
        BigDecimal balance
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getName(), account.getBalance());
    }
}
