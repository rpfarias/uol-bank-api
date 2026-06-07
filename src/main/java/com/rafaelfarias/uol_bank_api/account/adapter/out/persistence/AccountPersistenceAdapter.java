package com.rafaelfarias.uol_bank_api.account.adapter.out.persistence;

import com.rafaelfarias.uol_bank_api.account.application.port.out.AccountRepositoryPort;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementação da porta de saída de contas usando Spring Data JPA.
 * Faz a tradução entre o domínio ({@link Account}) e a entidade JPA.
 */
@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Account save(Account account) {
        AccountJpaEntity saved = jpaRepository.save(toEntity(account));
        return toDomain(saved);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private AccountJpaEntity toEntity(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setId(account.getId());
        entity.setName(account.getName());
        entity.setBalance(account.getBalance());
        return entity;
    }

    private Account toDomain(AccountJpaEntity entity) {
        return Account.restore(entity.getId(), entity.getName(), entity.getBalance());
    }
}
