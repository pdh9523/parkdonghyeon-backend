package site.donghyeon.bank.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.infrastructure.jpa.account.adapter.AccountRepositoryAdapter;
import site.donghyeon.bank.infrastructure.jpa.account.exception.AccountNotFoundException;

@DataJpaTest
@Import(AccountRepositoryAdapter.class)
class AccountRepositoryTest {

    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Test
    void 저장_후_조회_테스트() {
        Account saved = accountRepositoryAdapter.save(new Account(TEST_ACCOUNT_ID, TEST_USER_ID, Money.zero(), AccountStatus.OPEN));

        Account found = accountRepositoryAdapter.findById(saved.getAccountId());

        assertThat(found.getAccountId()).isEqualTo(TEST_ACCOUNT_ID);
        assertThat(found.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(found.getBalance().amount()).isZero();
        assertThat(found.getStatus()).isEqualTo(AccountStatus.OPEN);
    }

    @Test
    void 조회_불가_시_에러_반환() {
        assertThatThrownBy(() -> accountRepositoryAdapter.findById(TEST_ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
