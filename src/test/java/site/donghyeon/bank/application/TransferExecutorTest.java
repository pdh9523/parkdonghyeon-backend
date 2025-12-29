package site.donghyeon.bank.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.donghyeon.bank.application.account.support.cache.TransferLimitCache;
import site.donghyeon.bank.application.account.support.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.operation.executor.TransferExecutor;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.operation.task.TransferTask;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.domain.accountTransaction.AccountTransaction;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TransferExecutorTest {

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_EVENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID OTHER_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final Money TRANSFER_AMOUNT = new Money(1000);

    private static final Account TEST_ACCOUNT = new Account(
            TEST_ACCOUNT_ID,
            TEST_USER_ID,
            new Money(10000),
            AccountStatus.OPEN
    );

    private static final Account OTHER_ACCOUNT = new Account(
            OTHER_ACCOUNT_ID,
            TEST_USER_ID,
            Money.zero(),
            AccountStatus.OPEN
    );

    private static final Account TEST_ACCOUNT_WITH_ZERO = new Account(
            TEST_ACCOUNT_ID,
            TEST_USER_ID,
            Money.zero(),
            AccountStatus.OPEN
    );

    private static final TransferTask TEST_TASK = new TransferTask(
            TEST_EVENT_ID,
            TEST_ACCOUNT_ID,
            OTHER_ACCOUNT_ID,
            TRANSFER_AMOUNT
    );

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountTransactionRepository accountTransactionRepository;

    @Mock
    TransferLimitCache transferLimitCache;

    @InjectMocks
    TransferExecutor transferExecutor;

    @Test
    void 정상_이체_처리() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));
        given(accountRepository.findById(OTHER_ACCOUNT_ID)).willReturn(Optional.of(OTHER_ACCOUNT));
        given(transferLimitCache.tryConsume(any(UUID.class), any(Money.class), any(Money.class))).willReturn(true);

        transferExecutor.execute(TEST_TASK);

        then(accountRepository).should(times(2)).save(any(Account.class));
        then(accountTransactionRepository).should(times(3)).save(any(AccountTransaction.class));
    }

    @Test
    void 이미_처리된_이벤트_처리() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(true);

        transferExecutor.execute(TEST_TASK);

        then(accountTransactionRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 보내는_계좌가_없으면_예외_발생() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> transferExecutor.execute(TEST_TASK))
                .isInstanceOf(AccountNotFoundException.class);

        then(accountTransactionRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 받는_계좌가_없으면_예외_발생() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));

        given(transferLimitCache.tryConsume(any(UUID.class), any(Money.class), any(Money.class))).willReturn(true);
        given(accountRepository.findById(OTHER_ACCOUNT_ID)).willReturn(Optional.empty());

        transferExecutor.execute(TEST_TASK);

        then(accountTransactionRepository).should().save(any(AccountTransaction.class));
    }

    @Test
    void 잔고_초과_시_예외_발생() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT_WITH_ZERO));
        given(accountRepository.findById(OTHER_ACCOUNT_ID)).willReturn(Optional.of(OTHER_ACCOUNT));
        given(transferLimitCache.tryConsume(any(UUID.class), any(Money.class), any(Money.class))).willReturn(true);

        transferExecutor.execute(TEST_TASK);

        then(accountRepository).shouldHaveNoMoreInteractions();
        then(accountTransactionRepository).should().save(any(AccountTransaction.class));
    }

    @Test
    void 이체_한도_초과_시_예외_발생() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT_WITH_ZERO));
        given(transferLimitCache.tryConsume(any(UUID.class), any(Money.class), any(Money.class))).willReturn(false);

        transferExecutor.execute(TEST_TASK);

        then(accountRepository).shouldHaveNoMoreInteractions();
        then(accountTransactionRepository).should().save(any(AccountTransaction.class));
    }
}
