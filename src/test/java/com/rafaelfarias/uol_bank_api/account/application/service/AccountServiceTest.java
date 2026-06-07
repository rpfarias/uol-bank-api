package com.rafaelfarias.uol_bank_api.account.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.out.AccountRepositoryPort;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void deveCriarEPersistirConta() {
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account a = invocation.getArgument(0);
                    return Account.restore(1L, a.getName(), a.getBalance());
                });

        Account result = accountService.create("Alice", new BigDecimal("100.00"));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        org.mockito.Mockito.verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Alice");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveBuscarContaPorId() {
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(Account.restore(1L, "Alice", new BigDecimal("100.00"))));

        Account result = accountService.getById(1L);

        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void deveLancarNotFoundQuandoContaNaoExiste() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
