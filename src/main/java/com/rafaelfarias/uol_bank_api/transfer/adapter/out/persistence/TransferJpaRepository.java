package com.rafaelfarias.uol_bank_api.transfer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data para {@link TransferJpaEntity}.
 */
public interface TransferJpaRepository extends JpaRepository<TransferJpaEntity, Long> {
}
