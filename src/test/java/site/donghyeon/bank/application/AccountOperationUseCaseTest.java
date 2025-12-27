package site.donghyeon.bank.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.donghyeon.bank.application.account.cache.TransferLimitCache;
import site.donghyeon.bank.application.account.cache.WithdrawalLimitCache;
import site.donghyeon.bank.application.account.command.DepositCommand;
import site.donghyeon.bank.application.account.command.TransferCommand;
import site.donghyeon.bank.application.account.command.WithdrawalCommand;
import site.donghyeon.bank.application.account.exception.TransferLimitExceededException;
import site.donghyeon.bank.application.account.exception.WithdrawalLimitExceededException;
import site.donghyeon.bank.application.account.repository.AccountRepository;
import site.donghyeon.bank.application.account.service.AccountOperationService;
import site.donghyeon.bank.application.account.task.DepositTask;
import site.donghyeon.bank.application.account.task.TransferTask;
import site.donghyeon.bank.application.account.task.WithdrawalTask;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class AccountOperationUseCaseTest {

    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final Account TEST_ACCOUNT = new Account(
            TEST_ACCOUNT_ID,
            TEST_USER_ID,
            new Money(10_000),
            AccountStatus.OPEN
    );

    @Mock
    WithdrawalPublisher withdrawalPublisher;

    @Mock
    DepositPublisher depositPublisher;

    @Mock
    TransferPublisher transferPublisher;

    @Mock
    AccountRepository accountRepository;

    @Mock
    WithdrawalLimitCache withdrawalLimitCache;

    @Mock
    TransferLimitCache transferLimitCache;

    @InjectMocks
    AccountOperationService accountOperationService;

    @Test
    void 입금_요청_테스트() {
        DepositCommand command = new DepositCommand(TEST_ACCOUNT_ID, 10_000);

        given(accountRepository.existsById(TEST_ACCOUNT_ID)).willReturn(true);

        accountOperationService.deposit(command);
        then(depositPublisher).should().publish(any(DepositTask.class));
    }

    @Test
    void 출금_요청_테스트() {
        WithdrawalCommand command = new WithdrawalCommand(TEST_ACCOUNT_ID, 10_000);

        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));
        given(withdrawalLimitCache.checkWithdrawalLimit(TEST_ACCOUNT_ID))
                .willReturn(Money.zero());

        accountOperationService.withdrawal(command);

        then(withdrawalPublisher).should().publish(any(WithdrawalTask.class));
    }

    @Test
    void 이체_요청_테스트() {
        TransferCommand command = new TransferCommand(TEST_ACCOUNT_ID, OTHER_ACCOUNT_ID, 1_000);

        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));
        given(accountRepository.existsById(OTHER_ACCOUNT_ID)).willReturn(true);
        given(transferLimitCache.checkTransferLimit(TEST_ACCOUNT_ID))
                .willReturn(Money.zero());

        accountOperationService.transfer(command);

        then(transferPublisher).should().publish(any(TransferTask.class));
    }

    @Test
    void 출금_한도_초과_테스트() {
        WithdrawalCommand command = new WithdrawalCommand(TEST_ACCOUNT_ID, 10_000);

        given(withdrawalLimitCache.checkWithdrawalLimit(TEST_ACCOUNT_ID))
                .willReturn(new Money(1_000_000));

        assertThatThrownBy(() -> accountOperationService.withdrawal(command))
                .isInstanceOf(WithdrawalLimitExceededException.class)
                .hasMessageContaining("(spent: 1000000, requested: 10000)");

        then(withdrawalPublisher).shouldHaveNoMoreInteractions();
    }

    @Test
    void 이체_한도_초과_테스트() {
        TransferCommand command = new TransferCommand(TEST_ACCOUNT_ID, OTHER_ACCOUNT_ID, 10_000);

        given(transferLimitCache.checkTransferLimit(TEST_ACCOUNT_ID))
                .willReturn(new Money(3_000_000));

        assertThatThrownBy(() -> accountOperationService.transfer(command))
                .isInstanceOf(TransferLimitExceededException.class)
                .hasMessageContaining("(spent: 3000000, requested: 10000)");

        then(transferPublisher).shouldHaveNoMoreInteractions();
    }

    @Test
    void 잔고_초과_출금_테스트() {
        WithdrawalCommand command = new WithdrawalCommand(TEST_ACCOUNT_ID, 100_000);

        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));
        given(withdrawalLimitCache.checkWithdrawalLimit(TEST_ACCOUNT_ID))
                .willReturn(Money.zero());

        assertThatThrownBy(() -> accountOperationService.withdrawal(command))
                .isInstanceOf(InsufficientBalanceException.class);

        then(withdrawalPublisher).shouldHaveNoMoreInteractions();
    }

    @Test
    void 잔고_초과_이체_테스트() {
        TransferCommand command = new TransferCommand(TEST_ACCOUNT_ID, OTHER_ACCOUNT_ID, 10_000);

        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));
        given(accountRepository.existsById(OTHER_ACCOUNT_ID)).willReturn(true);
        given(transferLimitCache.checkTransferLimit(TEST_ACCOUNT_ID))
                .willReturn(Money.zero());

        assertThatThrownBy(() -> accountOperationService.transfer(command))
                .isInstanceOf(InsufficientBalanceException.class);

        then(transferPublisher).shouldHaveNoMoreInteractions();
    }
}
