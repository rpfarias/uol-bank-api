package com.rafaelfarias.uol_bank_api.transfer.adapter.out.persistence;

import com.rafaelfarias.uol_bank_api.transfer.application.port.out.TransferRepositoryPort;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementação da porta de saída de transferências com Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class TransferPersistenceAdapter implements TransferRepositoryPort {

    private final TransferJpaRepository jpaRepository;

    @Override
    public Transfer save(Transfer transfer) {
        return toDomain(jpaRepository.save(toEntity(transfer)));
    }

    @Override
    public List<Transfer> findByAccountId(Long accountId) {
        return jpaRepository.findByAccount(accountId).stream().map(this::toDomain).toList();
    }

    private TransferJpaEntity toEntity(Transfer transfer) {
        TransferJpaEntity entity = new TransferJpaEntity();
        entity.setId(transfer.getId());
        entity.setSourceAccountId(transfer.getSourceAccountId());
        entity.setTargetAccountId(transfer.getTargetAccountId());
        entity.setAmount(transfer.getAmount());
        entity.setCreatedAt(transfer.getCreatedAt());
        return entity;
    }

    private Transfer toDomain(TransferJpaEntity entity) {
        return Transfer.restore(entity.getId(), entity.getSourceAccountId(),
                entity.getTargetAccountId(), entity.getAmount(), entity.getCreatedAt());
    }
}
