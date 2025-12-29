package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.transaction.query.AccountLimitQuery;
import site.donghyeon.bank.domain.accountTransaction.enums.LimitType;

import java.util.UUID;

@Schema(description = "한도 조회 요청")
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
