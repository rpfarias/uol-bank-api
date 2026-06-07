package com.rafaelfarias.uol_bank_api.account.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data para {@link AccountJpaEntity}.
 */
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {
}
