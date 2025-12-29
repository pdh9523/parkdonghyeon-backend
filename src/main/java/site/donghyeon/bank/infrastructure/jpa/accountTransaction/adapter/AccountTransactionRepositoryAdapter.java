package site.donghyeon.bank.infrastructure.jpa.accountTransaction.adapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import site.donghyeon.bank.application.account.transaction.query.TransactionsQuery;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.transaction.result.TransactionsResult;
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
    public TransactionsResult findByAccountId(TransactionsQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AccountTransactionJpaEntity> page =
                accountTransactionJpaRepository.findByAccountId(query.accountId(), pageable);

        return new TransactionsResult(
                page.getContent().stream()
                        .map(AccountTransactionMapper::toTransactionsView)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public boolean existsByEventId(UUID eventId) {
        return accountTransactionJpaRepository.existsByEventId(eventId);
    }
}
