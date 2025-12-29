package site.donghyeon.bank.presentation.account.request;

import site.donghyeon.bank.application.account.transaction.query.AccountLimitQuery;
import site.donghyeon.bank.domain.accountTransaction.enums.LimitType;

import java.util.UUID;

public record AccountLimitRequest(
        UUID userId,
        UUID accountId,
        LimitType type
) {
    public static AccountLimitRequest of(UUID userId, UUID accountId, LimitType type) {
        return new AccountLimitRequest(userId, accountId, type);
    }

    public AccountLimitQuery toQuery() {
        return new AccountLimitQuery(
                this.userId,
                this.accountId,
                this.type
        );
    }
}
