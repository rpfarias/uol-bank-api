package com.rafaelfarias.uol_bank_api.transfer.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Payload de solicitação de transferência.
 */
public record TransferRequest(

        @Schema(description = "Id da conta de origem", example = "1")
        @NotNull(message = "Conta de origem e obrigatoria")
        Long sourceAccountId,

        @Schema(description = "Id da conta de destino", example = "2")
        @NotNull(message = "Conta de destino e obrigatoria")
        Long targetAccountId,

        @Schema(description = "Valor a transferir", example = "150.00")
        @NotNull(message = "Valor e obrigatorio")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal amount
) {
}
