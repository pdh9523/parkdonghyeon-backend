package site.donghyeon.bank.application.account.management.result;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;

import java.util.UUID;

public record OpenAccountResult(
        UUID accountId,
        Money balance
) {
    public static OpenAccountResult from(Account account) {
        return new OpenAccountResult(
                account.getAccountId(),
                account.getBalance()
        );
    }
}
