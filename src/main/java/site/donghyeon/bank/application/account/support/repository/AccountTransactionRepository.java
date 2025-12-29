package site.donghyeon.bank.application.account.support.repository;

import site.donghyeon.bank.application.account.transaction.query.TransactionsQuery;
import site.donghyeon.bank.application.account.transaction.result.TransactionsResult;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

import java.util.UUID;

public interface AccountTransactionRepository {
    TransactionsResult findByAccountId(TransactionsQuery query);
    AccountTransaction save(AccountTransaction tx);
    boolean existsByEventId(UUID eventId);
}
