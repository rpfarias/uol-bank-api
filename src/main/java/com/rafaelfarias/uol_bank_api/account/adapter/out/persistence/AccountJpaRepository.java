package com.rafaelfarias.uol_bank_api.account.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositório Spring Data para {@link AccountJpaEntity}.
 */
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    /**
     * Busca a conta aplicando lock pessimista de escrita — serializa débito/crédito
     * concorrentes sobre a mesma conta, garantindo consistência do saldo.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountJpaEntity a where a.id = :id")
    Optional<AccountJpaEntity> findByIdForUpdate(@Param("id") Long id);
}
