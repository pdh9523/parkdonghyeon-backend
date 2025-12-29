package site.donghyeon.bank.application.account.operation.executor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.donghyeon.bank.application.account.support.cache.WithdrawalLimitCache;
import site.donghyeon.bank.application.account.support.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.operation.task.WithdrawalTask;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

import static site.donghyeon.bank.application.account.operation.service.AccountOperationService.WITHDRAWAL_LIMIT;

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
                task.amount(),
                account.getBalance()
        );

        if (!withdrawalLimitCache.tryConsume(task.accountId(), task.amount(), WITHDRAWAL_LIMIT)) {
            //TODO: 한도 초과로 인한 실패 별도 구분자로 표시
            tx.markFailed();
        } else {
            try {
                // 3. 출금 시도
                account.withdraw(task.amount());
                tx = AccountTransaction.withdrawal(
                        task.eventId(),
                        task.accountId(),
                        task.amount(),
                        account.getBalance()
                );
                // 4. 출금 내역 저장
                accountRepository.save(account);
            } catch (InsufficientBalanceException e) {
                // 3-1. 출금 시도 실패 시 실패 내역 저장
                tx.markFailed();
                withdrawalLimitCache.rollback(task.accountId(), task.amount());
            }
        }
        accountTransactionRepository.save(tx);
    }
}
