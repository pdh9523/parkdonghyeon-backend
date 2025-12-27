package site.donghyeon.bank.application.account.repository;

import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

import java.util.UUID;

public interface AccountTransactionRepository {
    AccountTransaction save(AccountTransaction tx);
    boolean existsByEventId(UUID eventId);
}
