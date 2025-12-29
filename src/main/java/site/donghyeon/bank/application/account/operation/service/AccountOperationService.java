package site.donghyeon.bank.application.account.operation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.donghyeon.bank.application.account.limit.AccountLimitReader;
import site.donghyeon.bank.application.account.operation.AccountOperationUseCase;
import site.donghyeon.bank.application.account.operation.command.DepositCommand;
import site.donghyeon.bank.application.account.operation.command.TransferCommand;
import site.donghyeon.bank.application.account.operation.command.WithdrawalCommand;
import site.donghyeon.bank.application.account.support.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.support.exception.TransferLimitExceededException;
import site.donghyeon.bank.application.account.support.exception.WithdrawalLimitExceededException;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.operation.result.DepositResult;
import site.donghyeon.bank.application.account.operation.result.TransferResult;
import site.donghyeon.bank.application.account.operation.result.WithdrawalResult;
import site.donghyeon.bank.application.account.operation.task.DepositTask;
import site.donghyeon.bank.application.account.operation.task.TransferTask;
import site.donghyeon.bank.application.account.operation.task.WithdrawalTask;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalPublisher;

import java.util.UUID;

@Service
public class AccountOperationService implements AccountOperationUseCase {

    public final static Money WITHDRAWAL_LIMIT = new Money(1_000_000);
    public final static Money TRANSFER_LIMIT = new Money(3_000_000);

    private final AccountRepository accountRepository;
    private final AccountLimitReader accountLimitReader;
    private final DepositPublisher depositPublisher;
    private final WithdrawalPublisher withdrawalPublisher;
    private final TransferPublisher transferPublisher;

    public AccountOperationService(
            AccountRepository accountRepository,
            AccountLimitReader accountLimitReader,
            DepositPublisher depositPublisher,
            WithdrawalPublisher withdrawalPublisher,
            TransferPublisher transferPublisher) {
        this.accountRepository = accountRepository;
        this.accountLimitReader = accountLimitReader;
        this.depositPublisher = depositPublisher;
        this.withdrawalPublisher = withdrawalPublisher;
        this.transferPublisher = transferPublisher;
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
        // 1. 계좌 조회
        Account account = accountRepository.findById(command.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(command.fromAccountId()));

        account.verifyOwner(command.userId());

        // 2. 잔고 확인
        Money withdrawalAmount = new Money(command.amount());

        if (withdrawalAmount.exceeded(account.getBalance())) {
            throw new InsufficientBalanceException(account.getBalance(), withdrawalAmount);
        }

        // 3. 한도 조회
        Money spentLimit = accountLimitReader.checkWithdrawalLimit(command.fromAccountId());
        Money expectedLimit = spentLimit.add(withdrawalAmount);

        if (expectedLimit.exceeded(WITHDRAWAL_LIMIT)) {
            throw new WithdrawalLimitExceededException(spentLimit, withdrawalAmount);
        }

        // 4. 출금 요청 (pub)
        WithdrawalTask task = new WithdrawalTask(
                UUID.randomUUID(),
                command.fromAccountId(),
                withdrawalAmount
        );
        withdrawalPublisher.publish(task);

        return new WithdrawalResult(task.eventId());
    }

    @Override
    public TransferResult transfer(TransferCommand command) {
        // 1. 계좌 조회
        Account account = accountRepository.findById(command.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(command.fromAccountId()));

        account.verifyOwner(command.userId());

        // 2. 잔고 조회
        Money transferAmount = new Money(command.amount());

        if (transferAmount.withFee().exceeded(account.getBalance())) {
            throw new InsufficientBalanceException(account.getBalance(), transferAmount);
        }

        // 3. 한도 조회
        Money spentLimit = accountLimitReader.checkTransferLimit(command.fromAccountId());
        Money expectedLimit = spentLimit.add(transferAmount);

        if (expectedLimit.exceeded(TRANSFER_LIMIT)) {
            throw new TransferLimitExceededException(spentLimit, transferAmount);
        }

        // 4. 상대방 계좌 확인
        if (!accountRepository.existsById(command.toAccountId())) {
            throw new AccountNotFoundException(command.toAccountId());
        }

        // 5. 이체 요청 (pub)
        TransferTask task = new TransferTask(
                UUID.randomUUID(),
                command.fromAccountId(),
                command.toAccountId(),
                transferAmount
        );
        transferPublisher.publish(task);

        return new TransferResult(task.eventId());
    }
}
