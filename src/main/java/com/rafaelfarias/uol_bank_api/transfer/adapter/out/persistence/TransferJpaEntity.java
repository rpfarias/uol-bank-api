package com.rafaelfarias.uol_bank_api.transfer.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entidade JPA de uma transferência. Indexada por conta para suportar a consulta
 * de movimentações (extrato).
 */
@Entity
@Table(name = "transfers", indexes = {
        @Index(name = "idx_transfer_source", columnList = "source_account_id"),
        @Index(name = "idx_transfer_target", columnList = "target_account_id")
})
@Getter
@Setter
@NoArgsConstructor
public class TransferJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @Column(name = "target_account_id", nullable = false)
    private Long targetAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
