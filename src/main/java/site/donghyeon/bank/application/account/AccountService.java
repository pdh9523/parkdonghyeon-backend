package site.donghyeon.bank.application.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.donghyeon.bank.application.account.cache.WithdrawalLimitCache;
import site.donghyeon.bank.application.account.command.CloseAccountCommand;
import site.donghyeon.bank.application.account.command.DepositCommand;
import site.donghyeon.bank.application.account.command.OpenAccountCommand;
import site.donghyeon.bank.application.account.command.WithdrawalCommand;
import site.donghyeon.bank.application.account.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.exception.WithdrawalLimitExceededException;
import site.donghyeon.bank.application.account.result.DepositResult;
import site.donghyeon.bank.application.account.result.OpenAccountResult;
import site.donghyeon.bank.application.account.result.WithdrawalResult;
import site.donghyeon.bank.application.account.task.DepositTask;
import site.donghyeon.bank.application.account.task.WithdrawalTask;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.application.account.repository.AccountRepository;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalPublisher;

import java.util.UUID;

@Service
public class AccountService implements AccountUseCase {

    private final AccountRepository accountRepository;
    private final DepositPublisher depositPublisher;
    private final WithdrawalLimitCache withdrawalLimitCache;
    private final WithdrawalPublisher withdrawalPublisher;

    private final static Money WITHDRAWAL_LIMIT = new Money(1_000_000);

    public AccountService(
            AccountRepository accountRepository,
            DepositPublisher depositPublisher,
            WithdrawalLimitCache withdrawalLimitCache,
            WithdrawalPublisher withdrawalPublisher
    ) {
        this.accountRepository = accountRepository;
        this.depositPublisher = depositPublisher;
        this.withdrawalLimitCache = withdrawalLimitCache;
        this.withdrawalPublisher = withdrawalPublisher;
    }

    @Override
    @Transactional
    public OpenAccountResult openAccount(OpenAccountCommand command) {
        return OpenAccountResult.from(
                accountRepository.save(
                        Account.open(UUID.randomUUID(), command.userId())
                )
        );
    }

    @Override
    public void closeAccount(CloseAccountCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        
        //TODO: 잔액 검증(선택)
        account.close(command.userId());

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public DepositResult deposit(DepositCommand command) {
        DepositTask task = new DepositTask(
                UUID.randomUUID(),
                command.toAccountId(),
                new Money(command.amount())
        );

        if (!accountRepository.existsById(command.toAccountId())) {
            throw new AccountNotFoundException(command.toAccountId());
        }

        depositPublisher.publish(task);
        return DepositResult.from(task);
    }

    @Override
    public WithdrawalResult withdrawal(WithdrawalCommand command) {
        Money withdrawalAmount = new Money(command.amount());
        Money spentLimit = withdrawalLimitCache.checkWithdrawalLimit(command.fromAccountId());
        Money expectedLimit = spentLimit.add(withdrawalAmount);

        // 1. 한도 조회
        if (expectedLimit.exceeded(WITHDRAWAL_LIMIT)) {
            throw new WithdrawalLimitExceededException(spentLimit, withdrawalAmount);
        }

        // 2. 잔고 조회
        Account account = accountRepository.findById(command.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(command.fromAccountId()));

        if (withdrawalAmount.exceeded(account.getBalance())) {
            throw new InsufficientBalanceException(account.getBalance(), withdrawalAmount);
        }

        // 3. 출금 요청 (pub)
        WithdrawalTask task = new WithdrawalTask(
                UUID.randomUUID(),
                command.fromAccountId(),
                withdrawalAmount
        );
        withdrawalPublisher.publish(task);

        return new WithdrawalResult(task.txId());
    }
}
