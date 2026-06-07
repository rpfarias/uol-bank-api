package com.rafaelfarias.uol_bank_api.account.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Payload de criação de conta.
 */
public record CreateAccountRequest(

        @Schema(description = "Nome do cliente", example = "Alice")
        @NotBlank(message = "Nome e obrigatorio")
        String name,

        @Schema(description = "Saldo inicial da conta", example = "1000.00")
        @NotNull(message = "Saldo inicial e obrigatorio")
        @PositiveOrZero(message = "Saldo inicial nao pode ser negativo")
        BigDecimal initialBalance
) {
}
