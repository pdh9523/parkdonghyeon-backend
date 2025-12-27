package site.donghyeon.bank.infrastructure.jpa.accountTransaction.adapter;

import org.springframework.stereotype.Repository;
import site.donghyeon.bank.application.account.repository.AccountTransactionRepository;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;
import site.donghyeon.bank.infrastructure.jpa.accountTransaction.entity.AccountTransactionJpaEntity;
import site.donghyeon.bank.infrastructure.jpa.accountTransaction.mapper.AccountTransactionMapper;

import java.util.UUID;

@Repository
public class AccountTransactionRepositoryAdapter implements AccountTransactionRepository {

    private final AccountTransactionJpaRepository accountTransactionJpaRepository;

    public AccountTransactionRepositoryAdapter(
            AccountTransactionJpaRepository accountTransactionJpaRepository
    ) {
        this.accountTransactionJpaRepository = accountTransactionJpaRepository;
    }

    @Override
    public AccountTransaction save(AccountTransaction tx) {
        AccountTransactionJpaEntity entity = AccountTransactionMapper.toEntity(tx);
        AccountTransactionJpaEntity saved = accountTransactionJpaRepository.save(entity);
        return AccountTransactionMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEventId(UUID eventId) {
        return accountTransactionJpaRepository.existsByEventId(eventId);
    }
}
