package com.rafaelfarias.uol_bank_api.transfer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositório Spring Data para {@link TransferJpaEntity}.
 */
public interface TransferJpaRepository extends JpaRepository<TransferJpaEntity, Long> {

    @Query("select t from TransferJpaEntity t "
            + "where t.sourceAccountId = :accountId or t.targetAccountId = :accountId "
            + "order by t.createdAt desc, t.id desc")
    List<TransferJpaEntity> findByAccount(@Param("accountId") Long accountId);
}
