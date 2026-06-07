package com.rafaelfarias.uol_bank_api.account.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade JPA da conta. É um detalhe do adapter de persistência e não vaza
 * para fora do módulo — o domínio trabalha com {@code Account}.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class AccountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
}
