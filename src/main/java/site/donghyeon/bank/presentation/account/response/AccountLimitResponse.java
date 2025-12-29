package site.donghyeon.bank.presentation.account.response;

import site.donghyeon.bank.application.account.transaction.result.AccountLimitResult;

public record AccountLimitResponse(
    long limit
) {
    public static AccountLimitResponse from(AccountLimitResult result) {
        return new AccountLimitResponse(
                result.limit().amount()
        );
    }
}
