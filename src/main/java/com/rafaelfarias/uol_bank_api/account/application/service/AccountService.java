package com.rafaelfarias.uol_bank_api.account.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.in.UpdateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.out.AccountRepositoryPort;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementa os casos de uso de conta orquestrando o domínio e a porta de persistência.
 */
@Service
@RequiredArgsConstructor
public class AccountService implements CreateAccountUseCase, GetAccountUseCase, UpdateAccountUseCase {

    private final AccountRepositoryPort accountRepository;

    @Override
    @Transactional
    public Account create(String name, BigDecimal initialBalance) {
        Account account = Account.create(name, initialBalance);
        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta " + id + " nao encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account loadForUpdate(Long id) {
        return accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta " + id + " nao encontrada"));
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
