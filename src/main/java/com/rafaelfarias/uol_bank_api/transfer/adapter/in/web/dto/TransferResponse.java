package com.rafaelfarias.uol_bank_api.transfer.adapter.in.web.dto;

import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representação de saída de uma transferência concluída.
 */
public record TransferResponse(

        @Schema(description = "Id da transferência", example = "1")
        Long id,

        @Schema(description = "Id da conta de origem", example = "1")
        Long sourceAccountId,

        @Schema(description = "Id da conta de destino", example = "2")
        Long targetAccountId,

        @Schema(description = "Valor transferido", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Momento da transferência")
        Instant createdAt
) {

    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(transfer.getId(), transfer.getSourceAccountId(),
                transfer.getTargetAccountId(), transfer.getAmount(), transfer.getCreatedAt());
    }
}
