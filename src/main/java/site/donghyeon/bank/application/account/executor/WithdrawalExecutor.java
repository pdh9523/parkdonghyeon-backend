package site.donghyeon.bank.application.account.executor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.donghyeon.bank.application.account.cache.WithdrawalLimitCache;
import site.donghyeon.bank.application.account.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.repository.AccountRepository;
import site.donghyeon.bank.application.account.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.task.WithdrawalTask;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

@Component
@Transactional
public class WithdrawalExecutor {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final WithdrawalLimitCache withdrawalLimitCache;

    public WithdrawalExecutor(
            AccountRepository accountRepository,
            AccountTransactionRepository accountTransactionRepository,
            WithdrawalLimitCache withdrawalLimitCache) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
        this.withdrawalLimitCache = withdrawalLimitCache;
    }

    public void execute(WithdrawalTask task) {
        // 1. 멱등성 처리
        if (accountTransactionRepository.existsByEventId(task.eventId())) return;

        // 2. 계좌 확인
        Account account = accountRepository.findById(task.accountId())
                .orElseThrow(() -> new AccountNotFoundException(task.accountId()));

        AccountTransaction tx = AccountTransaction.withdrawal(
                task.eventId(),
                task.accountId(),
                task.amount()
        );

        try {
            // 3. 출금 시도
            account.withdraw(task.amount());
            // 4. 출금 내역 저장
            accountRepository.save(account);
            accountTransactionRepository.save(tx);
            // 5. 출금 한도 수정
            withdrawalLimitCache.increase(task.accountId(), task.amount());
        } catch (InsufficientBalanceException e) {
            // 3-1. 출금 시도 실패 시 실패 내역 저장
            tx.markFailed();
            accountTransactionRepository.save(tx);
        }
        //TODO: redis incrby 실패 시 동작 구현
    }
}
