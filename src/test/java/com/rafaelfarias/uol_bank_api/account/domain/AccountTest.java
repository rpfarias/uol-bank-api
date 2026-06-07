package com.rafaelfarias.uol_bank_api.account.domain;

import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void deveCriarContaComDadosValidos() {
        Account account = Account.create("Alice", new BigDecimal("100.00"));

        assertThat(account.getId()).isNull();
        assertThat(account.getName()).isEqualTo("Alice");
        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void naoDeveCriarContaComNomeVazio() {
        assertThatThrownBy(() -> Account.create("  ", new BigDecimal("100.00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Nome");
    }

    @Test
    void naoDeveCriarContaComSaldoInicialNegativo() {
        assertThatThrownBy(() -> Account.create("Alice", new BigDecimal("-1")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("negativo");
    }

    @Test
    void deveDebitarQuandoSaldoSuficiente() {
        Account account = Account.create("Alice", new BigDecimal("100.00"));

        account.debit(new BigDecimal("30.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    void naoDeveDebitarComSaldoInsuficiente() {
        Account account = Account.create("Alice", new BigDecimal("10.00"));

        assertThatThrownBy(() -> account.debit(new BigDecimal("30.00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    void deveCreditarValor() {
        Account account = Account.create("Alice", new BigDecimal("100.00"));

        account.credit(new BigDecimal("50.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void naoDeveAceitarValorNaoPositivoEmDebitoOuCredito() {
        Account account = Account.create("Alice", new BigDecimal("100.00"));

        assertThatThrownBy(() -> account.debit(BigDecimal.ZERO))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> account.credit(new BigDecimal("-5")))
                .isInstanceOf(BusinessRuleException.class);
    }
}
