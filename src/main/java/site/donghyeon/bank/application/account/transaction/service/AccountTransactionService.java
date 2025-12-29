package site.donghyeon.bank.application.account.transaction.service;

import org.springframework.stereotype.Service;
import site.donghyeon.bank.application.account.limit.AccountLimitReader;
import site.donghyeon.bank.application.account.transaction.AccountTransactionUseCase;
import site.donghyeon.bank.application.account.transaction.query.AccountLimitQuery;
import site.donghyeon.bank.application.account.transaction.query.TransactionsQuery;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.transaction.result.AccountLimitResult;
import site.donghyeon.bank.application.account.transaction.result.TransactionsResult;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.exception.AccountAccessDeniedException;
import site.donghyeon.bank.domain.accountTransaction.enums.LimitType;

@Service
public class AccountTransactionService implements AccountTransactionUseCase {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final AccountLimitReader accountLimitReader;

    public AccountTransactionService(
            AccountRepository accountRepository,
            AccountTransactionRepository accountTransactionRepository,
            AccountLimitReader accountLimitReader
    ) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountLimitReader = accountLimitReader;
    }

    @Override
    public TransactionsResult getTransactions(TransactionsQuery query) {
        if (!accountRepository.existsByUserIdAndAccountId(query.userId(), query.accountId())) {
            throw new AccountAccessDeniedException();
        }
        return accountTransactionRepository.findByAccountId(query);
    }

    @Override
    public AccountLimitResult getAccountLimit(AccountLimitQuery query) {
        if (!accountRepository.existsByUserIdAndAccountId(query.userId(), query.accountId())) {
            throw new AccountAccessDeniedException();
        }

        return AccountLimitResult.of(
                accountLimitReader.checkLimit(query.accountId(), query.type())
        );
    }
}
