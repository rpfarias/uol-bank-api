package com.rafaelfarias.uol_bank_api.transfer.adapter.in.web;

import com.rafaelfarias.uol_bank_api.transfer.adapter.in.web.dto.TransferRequest;
import com.rafaelfarias.uol_bank_api.transfer.adapter.in.web.dto.TransferResponse;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.TransferFundsUseCase;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transferências", description = "Transferência de fundos entre contas")
public class TransferController {

    private final TransferFundsUseCase transferFundsUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realiza uma transferência entre duas contas")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        Transfer transfer = transferFundsUseCase.transfer(
                request.sourceAccountId(), request.targetAccountId(), request.amount());
        return TransferResponse.from(transfer);
    }
}
