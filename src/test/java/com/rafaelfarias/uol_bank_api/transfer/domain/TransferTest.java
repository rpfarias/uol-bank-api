package com.rafaelfarias.uol_bank_api.transfer.domain;

import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferTest {

    @Test
    void deveCriarTransferenciaValida() {
        Transfer transfer = Transfer.create(1L, 2L, new BigDecimal("100.00"));

        assertThat(transfer.getSourceAccountId()).isEqualTo(1L);
        assertThat(transfer.getTargetAccountId()).isEqualTo(2L);
        assertThat(transfer.getAmount()).isEqualByComparingTo("100.00");
        assertThat(transfer.getCreatedAt()).isNotNull();
    }

    @Test
    void naoDevePermitirMesmaContaOrigemEDestino() {
        assertThatThrownBy(() -> Transfer.create(1L, 1L, new BigDecimal("100.00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("diferentes");
    }

    @Test
    void naoDevePermitirValorNaoPositivo() {
        assertThatThrownBy(() -> Transfer.create(1L, 2L, BigDecimal.ZERO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("positivo");
    }

    @Test
    void naoDevePermitirContasNulas() {
        assertThatThrownBy(() -> Transfer.create(null, 2L, new BigDecimal("10.00")))
                .isInstanceOf(BusinessRuleException.class);
    }
}
