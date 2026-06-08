package com.rafaelfarias.uol_bank_api.movement.adapter.in.web;

import com.rafaelfarias.uol_bank_api.movement.adapter.in.web.dto.MovementResponse;
import com.rafaelfarias.uol_bank_api.movement.application.port.in.GetAccountStatementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountId}/movements")
@RequiredArgsConstructor
@Tag(name = "Movimentações", description = "Consulta de movimentações financeiras (extrato) de uma conta")
public class MovementController {

    private final GetAccountStatementUseCase getAccountStatementUseCase;

    @GetMapping
    @Operation(summary = "Lista as movimentações (extrato) de uma conta, mais recentes primeiro")
    public List<MovementResponse> getStatement(@PathVariable Long accountId) {
        return getAccountStatementUseCase.getStatement(accountId).stream()
                .map(MovementResponse::from)
                .toList();
    }
}
