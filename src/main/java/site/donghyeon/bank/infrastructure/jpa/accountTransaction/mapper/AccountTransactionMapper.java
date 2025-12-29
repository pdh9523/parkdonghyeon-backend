package site.donghyeon.bank.infrastructure.jpa.accountTransaction.mapper;

import site.donghyeon.bank.application.account.transaction.view.TransactionsView;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;
import site.donghyeon.bank.infrastructure.jpa.accountTransaction.entity.AccountTransactionJpaEntity;

public final class AccountTransactionMapper {

    public static AccountTransaction toDomain(AccountTransactionJpaEntity entity) {
        return new AccountTransaction(
                entity.getId(),
                entity.getAccountId(),
                entity.getEventId(),
                new Money(entity.getAmount()),
                new Money(entity.getBalance()),
                entity.getTransactionType()
        );
    }

    public static AccountTransactionJpaEntity toEntity(AccountTransaction tx) {
        return new AccountTransactionJpaEntity(
                tx.getTxId(),
                tx.getAccountId(),
                tx.getEventId(),
                tx.getAmount().amount(),
                tx.getBalance().amount(),
                tx.getTransactionType()
        );
    }

    public static TransactionsView toTransactionsView(AccountTransactionJpaEntity entity) {
        return new TransactionsView(
                entity.getEventId(),
                entity.getCreatedAt(),
                entity.getTransactionType(),
                new Money(entity.getAmount()),
                new Money(entity.getBalance())
        );
    }
}
