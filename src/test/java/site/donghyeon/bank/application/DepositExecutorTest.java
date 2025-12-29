package site.donghyeon.bank.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.donghyeon.bank.application.account.support.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.operation.executor.DepositExecutor;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.operation.task.DepositTask;
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

@ExtendWith(MockitoExtension.class)
public class DepositExecutorTest {

    private static final UUID TEST_EVENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final Money DEPOSIT_AMOUNT = new Money(1000);

    private static final Account TEST_ACCOUNT = new Account(
            TEST_ACCOUNT_ID,
            TEST_USER_ID,
            Money.zero(),
            AccountStatus.OPEN
    );

    private static final DepositTask TEST_TASK = new DepositTask(
            TEST_EVENT_ID,
            TEST_ACCOUNT_ID,
            DEPOSIT_AMOUNT
    );

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountTransactionRepository accountTransactionRepository;

    @InjectMocks
    DepositExecutor depositExecutor;

    @Test
    void 정상_입금_처리() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(TEST_ACCOUNT));

        depositExecutor.execute(TEST_TASK);

        then(accountRepository).should().save(TEST_ACCOUNT);
        then(accountTransactionRepository).should().save(any(AccountTransaction.class));
    }

    @Test
    void 이미_처리된_이벤트_처리() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(true);

        depositExecutor.execute(TEST_TASK);

        then(accountTransactionRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 계좌가_없으면_예외_발생() {
        given(accountTransactionRepository.existsByEventId(TEST_EVENT_ID)).willReturn(false);
        given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> depositExecutor.execute(TEST_TASK))
                .isInstanceOf(AccountNotFoundException.class);

        then(accountTransactionRepository).shouldHaveNoMoreInteractions();
    }
}
