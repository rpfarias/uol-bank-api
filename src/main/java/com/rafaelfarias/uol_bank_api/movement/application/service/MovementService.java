package com.rafaelfarias.uol_bank_api.movement.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.movement.application.port.in.GetAccountStatementUseCase;
import com.rafaelfarias.uol_bank_api.movement.domain.Movement;
import com.rafaelfarias.uol_bank_api.movement.domain.MovementType;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.FindTransfersUseCase;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Monta o extrato de uma conta projetando as transferências como movimentações
 * (DEBIT quando a conta é origem, CREDIT quando é destino).
 */
@Service
@RequiredArgsConstructor
public class MovementService implements GetAccountStatementUseCase {

    private final GetAccountUseCase getAccountUseCase;
    private final FindTransfersUseCase findTransfersUseCase;

    @Override
    public List<Movement> getStatement(Long accountId) {
        // Garante 404 caso a conta não exista.
        getAccountUseCase.getById(accountId);

        return findTransfersUseCase.findByAccount(accountId).stream()
                .map(transfer -> toMovement(transfer, accountId))
                .toList();
    }

    private Movement toMovement(Transfer transfer, Long accountId) {
        boolean isSource = transfer.getSourceAccountId().equals(accountId);
        MovementType type = isSource ? MovementType.DEBIT : MovementType.CREDIT;
        Long counterparty = isSource ? transfer.getTargetAccountId() : transfer.getSourceAccountId();
        return new Movement(transfer.getId(), type, counterparty, transfer.getAmount(), transfer.getCreatedAt());
    }
}
