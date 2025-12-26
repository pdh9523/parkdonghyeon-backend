package site.donghyeon.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.exception.InsufficientBalanceException;

class AccountDomainTest {

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Test
    void 계좌_개설_테스트() {
        Account account = Account.open(TEST_ACCOUNT_ID, TEST_USER_ID);

        assertThat(account.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(account.getBalance().amount()).isZero();
    }

    @Test
    void 입금_테스트() {
        Account account = Account.open(TEST_ACCOUNT_ID, TEST_USER_ID);

        account.deposit(new Money(100));

        assertThat(account.getBalance().amount()).isEqualTo(100L);
    }

    @Test
    void 출금_테스트() {
        Account account = Account.open(TEST_ACCOUNT_ID, TEST_USER_ID);
        account.deposit(new Money(1000));
        account.withdraw(new Money(100));

        assertThat(account.getBalance().amount()).isEqualTo(900L);
    }

    @Test
    void 잔고는_0미만이_될_수_없다() {
        Account account = Account.open(TEST_ACCOUNT_ID, TEST_USER_ID);

        assertThatThrownBy(() -> account.withdraw(new Money(1L)))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
