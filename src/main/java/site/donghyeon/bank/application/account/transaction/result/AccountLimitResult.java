package site.donghyeon.bank.application.account.transaction.result;

import site.donghyeon.bank.common.domain.Money;

public record AccountLimitResult(
        Money limit
) {
    public static AccountLimitResult of(Money limit) {
        return new AccountLimitResult(limit);
    }
}
