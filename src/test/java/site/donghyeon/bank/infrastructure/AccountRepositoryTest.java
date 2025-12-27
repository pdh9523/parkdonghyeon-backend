package site.donghyeon.bank.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.infrastructure.jpa.account.adapter.AccountRepositoryAdapter;

@DataJpaTest
@Import(AccountRepositoryAdapter.class)
class AccountRepositoryTest {

    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Test
    void 저장_후_조회_테스트() {
        accountRepositoryAdapter.save(new Account(TEST_ACCOUNT_ID, TEST_USER_ID, Money.zero(), AccountStatus.OPEN));

        Optional<Account> found = accountRepositoryAdapter.findById(TEST_ACCOUNT_ID);

        assertThat(found).isPresent();

        Account account = found.get();
        assertThat(account.getAccountId()).isEqualTo(TEST_ACCOUNT_ID);
        assertThat(account.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(account.getBalance().amount()).isZero();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.OPEN);
    }

    @Test
    void 조회_불가_시_에러_반환() {
        Optional<Account> found = accountRepositoryAdapter.findById(TEST_ACCOUNT_ID);
        assertThat(found).isNotPresent();
    }
}
