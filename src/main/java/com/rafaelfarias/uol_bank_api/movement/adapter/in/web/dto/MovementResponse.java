package com.rafaelfarias.uol_bank_api.movement.adapter.in.web.dto;

import com.rafaelfarias.uol_bank_api.movement.domain.Movement;
import com.rafaelfarias.uol_bank_api.movement.domain.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Item do extrato de movimentações de uma conta.
 */
public record MovementResponse(

        @Schema(description = "Id da transferência que originou a movimentação", example = "10")
        Long transferId,

        @Schema(description = "Tipo da movimentação (DEBIT=saída, CREDIT=entrada)", example = "DEBIT")
        MovementType type,

        @Schema(description = "Conta da contraparte (destino se DEBIT, origem se CREDIT)", example = "2")
        Long counterpartyAccountId,

        @Schema(description = "Valor movimentado", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Momento da movimentação")
        Instant createdAt
) {

    public static MovementResponse from(Movement movement) {
        return new MovementResponse(movement.transferId(), movement.type(),
                movement.counterpartyAccountId(), movement.amount(), movement.createdAt());
    }
}
