package site.donghyeon.bank.infrastructure.jpa.account.mapper;

import site.donghyeon.bank.application.account.management.view.AccountView;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.infrastructure.jpa.account.entity.AccountJpaEntity;

public final class AccountMapper {

    public static Account toDomain(AccountJpaEntity entity) {
        return new Account(
                entity.getId(),
                entity.getUserId(),
                new Money(entity.getBalance()),
                entity.getStatus()
        );
    }

    public static AccountJpaEntity toEntity(Account account) {
        return new AccountJpaEntity(
                account.getAccountId(),
                account.getUserId(),
                account.getBalance().amount(),
                account.getStatus()
        );
    }

    public static AccountView toAccountView(AccountJpaEntity entity) {
        return new AccountView(
                entity.getId(),
                new Money(entity.getBalance()),
                entity.getCreatedAt()
        );
    }
}
