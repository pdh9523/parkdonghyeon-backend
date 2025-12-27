package site.donghyeon.bank.application.account.executor;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.repository.AccountRepository;
import site.donghyeon.bank.application.account.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.task.DepositTask;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

@Component
@Transactional
public class DepositExecutor {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    public DepositExecutor(
            AccountRepository accountRepository,
            AccountTransactionRepository accountTransactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    public void execute(DepositTask task) {
        // 1. 멱등성 처리
        if (accountTransactionRepository.existsByEventId(task.eventId())) return;

        // 2. 입금
        Account account = accountRepository.findById(task.accountId())
                .orElseThrow(() -> new AccountNotFoundException(task.accountId()));
        account.deposit(task.amount());

        // 3. 거래 내역 생성
        AccountTransaction tx = AccountTransaction.deposit(task.eventId(), task.accountId(), task.amount());

        // 4. DB 저장
        accountRepository.save(account);
        accountTransactionRepository.save(tx);
    }
}
