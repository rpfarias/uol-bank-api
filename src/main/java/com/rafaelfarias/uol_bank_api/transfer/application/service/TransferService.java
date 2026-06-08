package com.rafaelfarias.uol_bank_api.transfer.application.service;

import com.rafaelfarias.uol_bank_api.account.application.port.in.UpdateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.FindTransfersUseCase;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.TransferFundsUseCase;
import com.rafaelfarias.uol_bank_api.transfer.application.port.out.TransferEventPublisher;
import com.rafaelfarias.uol_bank_api.transfer.application.port.out.TransferRepositoryPort;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orquestra a transferência de fundos garantindo consistência sob concorrência.
 *
 * <p>Estratégia: dentro de uma única transação, as duas contas são carregadas com
 * lock pessimista de escrita. Para evitar deadlocks quando duas transferências
 * invertidas (A→B e B→A) ocorrem em paralelo, os locks são sempre adquiridos na
 * mesma ordem (menor id primeiro).</p>
 */
@Service
@RequiredArgsConstructor
public class TransferService implements TransferFundsUseCase, FindTransfersUseCase {

    private final UpdateAccountUseCase accountPort;
    private final TransferRepositoryPort transferRepository;
    private final TransferEventPublisher eventPublisher;

    @Override
    @Transactional
    public Transfer transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        Transfer transfer = Transfer.create(sourceAccountId, targetAccountId, amount);

        Account source;
        Account target;
        // Ordem de aquisição de lock consistente (menor id primeiro) -> previne deadlock.
        if (sourceAccountId < targetAccountId) {
            source = accountPort.loadForUpdate(sourceAccountId);
            target = accountPort.loadForUpdate(targetAccountId);
        } else {
            target = accountPort.loadForUpdate(targetAccountId);
            source = accountPort.loadForUpdate(sourceAccountId);
        }

        source.debit(amount);
        target.credit(amount);

        accountPort.save(source);
        accountPort.save(target);

        Transfer saved = transferRepository.save(transfer);
        // Publicado na transação; a notificação só dispara após o commit (AFTER_COMMIT).
        eventPublisher.publishCompleted(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> findByAccount(Long accountId) {
        return transferRepository.findByAccountId(accountId);
    }
}
