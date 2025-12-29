package site.donghyeon.bank.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.donghyeon.bank.application.account.transaction.query.TransactionsQuery;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;
import site.donghyeon.bank.application.account.support.repository.AccountTransactionRepository;
import site.donghyeon.bank.application.account.transaction.result.TransactionsResult;
import site.donghyeon.bank.application.account.transaction.service.AccountTransactionService;
import site.donghyeon.bank.application.account.transaction.view.TransactionsView;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.exception.AccountAccessDeniedException;
import site.donghyeon.bank.domain.accountTransaction.enums.TransactionType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class AccountTransactionUseCaseTest {

    private static final UUID TEST_EVENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");


    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountTransactionRepository accountTransactionRepository;

    @InjectMocks
    AccountTransactionService accountTransactionService;

    @Test
    void 거래_내역_조회_테스트() {
        TransactionsQuery query = new TransactionsQuery(
                TEST_USER_ID,
                TEST_ACCOUNT_ID,
                0,
                20
        );

        TransactionsResult expected = new TransactionsResult(
                List.of(
                        new TransactionsView(
                                TEST_EVENT_ID,
                                Instant.parse("2025-01-01T00:00:00Z"),
                                TransactionType.DEPOSIT,
                                new Money(1000),
                                new Money(1000)
                        )
                ),
                0,
                20,
                1
        );

        given(accountRepository.existsByUserIdAndAccountId(
                TEST_USER_ID, TEST_ACCOUNT_ID)
        ).willReturn(true);

        given(accountTransactionRepository.findByAccountId(query))
                .willReturn(expected);

        TransactionsResult result =
                accountTransactionService.getTransactions(query);

        assertThat(result).isEqualTo(expected);

        then(accountRepository)
                .should()
                .existsByUserIdAndAccountId(TEST_USER_ID, TEST_ACCOUNT_ID);

        then(accountTransactionRepository)
                .should()
                .findByAccountId(query);
    }

    @Test
    void 유저와_계좌가_일치하지_않는_경우() {
        TransactionsQuery query = new TransactionsQuery(
                TEST_USER_ID,
                TEST_ACCOUNT_ID,
                0,
                20
        );

        given(accountRepository.existsByUserIdAndAccountId(
                TEST_USER_ID, TEST_ACCOUNT_ID)
        ).willReturn(false);

        assertThatThrownBy(() ->
                accountTransactionService.getTransactions(query)
        ).isInstanceOf(AccountAccessDeniedException.class);

        then(accountRepository)
                .should()
                .existsByUserIdAndAccountId(TEST_USER_ID, TEST_ACCOUNT_ID);

        then(accountTransactionRepository)
                .should(never())
                .findByAccountId(query);
    }
}
