package site.donghyeon.bank.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.donghyeon.bank.application.account.exception.RemainingBalanceException;
import site.donghyeon.bank.application.account.query.MyAccountsQuery;
import site.donghyeon.bank.application.account.result.MyAccountsResult;
import site.donghyeon.bank.application.account.service.AccountService;
import site.donghyeon.bank.application.account.command.CloseAccountCommand;
import site.donghyeon.bank.application.account.command.OpenAccountCommand;
import site.donghyeon.bank.application.account.result.OpenAccountResult;
import site.donghyeon.bank.application.account.view.AccountView;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.application.account.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountUseCaseTest {

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void 계좌_개설_테스트() {
        Account saved = new Account(TEST_ACCOUNT_ID, TEST_USER_ID, Money.zero(), AccountStatus.OPEN);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        OpenAccountResult result = accountService.openAccount(new OpenAccountCommand(TEST_USER_ID));

        assertThat(result.accountId()).isEqualTo(TEST_ACCOUNT_ID);
        assertThat(result.balance().amount()).isZero();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void 계좌_해지_테스트() {
        Account existing = new Account(TEST_ACCOUNT_ID, TEST_USER_ID, Money.zero(), AccountStatus.OPEN);
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(existing));
        when(accountRepository.save(existing)).thenReturn(existing);

        accountService.closeAccount(new CloseAccountCommand(TEST_USER_ID, TEST_ACCOUNT_ID));

        assertThat(existing.getStatus()).isEqualTo(AccountStatus.CLOSED);
        verify(accountRepository).findById(TEST_ACCOUNT_ID);
        verify(accountRepository).save(existing);
    }

    @Test
    void 잔액_있는_계좌_해지_불가() {
        Account existing = new Account(TEST_ACCOUNT_ID, TEST_USER_ID, new Money(1000), AccountStatus.OPEN);

        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> accountService.closeAccount(new CloseAccountCommand(TEST_USER_ID, TEST_ACCOUNT_ID)))
                .isInstanceOf(RemainingBalanceException.class);

        then(accountRepository).should(never()).save(any());
    }

    @Test
    void 내_계좌_조회_테스트() {
        // given
        AccountView view = new AccountView(
                TEST_ACCOUNT_ID,
                new Money(1000),
                Instant.parse("2025-01-01T00:00:00Z")
        );

        MyAccountsQuery query = new MyAccountsQuery(TEST_USER_ID);

        when(accountRepository.findAllByUserId(TEST_USER_ID))
                .thenReturn(List.of(view));

        MyAccountsResult result = accountService.getMyAccounts(query);

        assertThat(result.accounts()).hasSize(1);
        assertThat(result.accounts().get(0)).isEqualTo(view);

        then(accountRepository)
                .should()
                .findAllByUserId(TEST_USER_ID);
    }

    @Test
    void 보유_계좌_없을_시_빈_리스트_반환() {
        MyAccountsQuery query = new MyAccountsQuery(TEST_USER_ID);

        when(accountRepository.findAllByUserId(TEST_USER_ID))
                .thenReturn(List.of());

        MyAccountsResult result = accountService.getMyAccounts(query);

        assertThat(result.accounts()).hasSize(0);

        then(accountRepository)
                .should()
                .findAllByUserId(TEST_USER_ID);
    }
}
