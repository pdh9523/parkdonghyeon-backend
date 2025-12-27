package site.donghyeon.bank.application.account.executor;

import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.cache.TransferLimitCache;
import site.donghyeon.bank.application.account.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.repository.AccountRepository;
import site.donghyeon.bank.application.account.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.task.TransferTask;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

@Component
public class TransferExecutor {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final TransferLimitCache transferLimitCache;

    public TransferExecutor(
            AccountRepository accountRepository,
            AccountTransactionRepository accountTransactionRepository,
            TransferLimitCache transferLimitCache) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
        this.transferLimitCache = transferLimitCache;
    }

    public void execute(TransferTask task) {
        System.out.println(11);
        // 1. 멱등성 처리
        if (accountTransactionRepository.existsByEventId(task.eventId())) return;

        // 2. 계좌 확인 ( from -> to )
        Account fromAccount = accountRepository.findById(task.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(task.fromAccountId()));

        Account toAccount = accountRepository.findById(task.toAccountId())
                .orElseThrow(() -> new AccountNotFoundException(task.toAccountId()));

        AccountTransaction txFrom = AccountTransaction.transferFrom(
                task.eventId(),
                task.fromAccountId(),
                task.amount()
        );

        AccountTransaction txTo = AccountTransaction.transferTo(
                task.eventId(),
                task.toAccountId(),
                task.amount()
        );

        AccountTransaction txFee = AccountTransaction.fee(
                task.eventId(),
                task.fromAccountId(),
                task.amount().getFee()
        );

        try {
            // 3. from 계좌에서 출금 (수수료 포함)
            fromAccount.withdraw(task.amount().withFee());
            // 4. to 계좌에서 입금
            toAccount.deposit(task.amount());

            // 6. 이체 내역 저장
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            accountTransactionRepository.save(txFrom);
            accountTransactionRepository.save(txTo);
            accountTransactionRepository.save(txFee);

            // 7. 이체 한도 수정
            transferLimitCache.increase(task.fromAccountId(), task.amount());
        } catch (InsufficientBalanceException e) {
            // 3-1 출금 실패 시 실패 내역 저장 (보내는 사람에게만)
            txFrom.markFailed();
            accountTransactionRepository.save(txFrom);
        }
    }
}
