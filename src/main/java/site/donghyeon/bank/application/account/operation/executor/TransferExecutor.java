package site.donghyeon.bank.application.account.operation.executor;

import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.support.cache.TransferLimitCache;
import site.donghyeon.bank.application.account.support.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.operation.task.TransferTask;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

import static site.donghyeon.bank.application.account.operation.service.AccountOperationService.TRANSFER_LIMIT;

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
        // 1. 멱등성 처리
        if (accountTransactionRepository.existsByEventId(task.eventId())) return;

        // 2. 계좌 확인 ( from -> to )
        Account fromAccount = accountRepository.findById(task.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(task.fromAccountId()));

        AccountTransaction txFrom = AccountTransaction.transferFrom(
                task.eventId(),
                task.fromAccountId(),
                task.amount(),
                fromAccount.getBalance()
        );

        if (!transferLimitCache.tryConsume(task.fromAccountId(), task.amount(), TRANSFER_LIMIT)) {
            txFrom.markFailed();
            accountTransactionRepository.save(txFrom);
        } else {
            try {
                // 2-1. 받는 사람 계좌 확인
                Account toAccount = accountRepository.findById(task.toAccountId())
                        .orElseThrow(() -> new AccountNotFoundException(task.toAccountId()));

                // 3. from 계좌에서 출금
                fromAccount.withdraw(task.amount());
                txFrom = AccountTransaction.transferFrom(
                        task.eventId(),
                        task.fromAccountId(),
                        task.amount(),
                        fromAccount.getBalance()
                );

                // 4. from 계좌에서 수수료 출금
                fromAccount.withdraw(task.amount().getFee());
                AccountTransaction txFee = AccountTransaction.fee(
                        task.eventId(),
                        task.fromAccountId(),
                        task.amount().getFee(),
                        fromAccount.getBalance()
                );

                // 5. to 계좌에 입금
                toAccount.deposit(task.amount());
                AccountTransaction txTo = AccountTransaction.transferTo(
                        task.eventId(),
                        task.toAccountId(),
                        task.amount(),
                        toAccount.getBalance()
                );

                // 6. 이체 내역 저장
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);

                accountTransactionRepository.save(txFrom);
                accountTransactionRepository.save(txFee);
                accountTransactionRepository.save(txTo);
            } catch (InsufficientBalanceException e) {
                // 3-1 출금 실패 시 실패 내역 저장 (보내는 사람에게만)
                txFrom.markFailed();
                transferLimitCache.rollback(task.fromAccountId(), task.amount());
                accountTransactionRepository.save(txFrom);
            } catch (AccountNotFoundException e) {
                // 2-2. 받는 사람 계좌 없을 시 실패 처리
                txFrom.markFailed();
                transferLimitCache.rollback(task.fromAccountId(), task.amount());
                accountTransactionRepository.save(txFrom);
            }
        }
    }
}
