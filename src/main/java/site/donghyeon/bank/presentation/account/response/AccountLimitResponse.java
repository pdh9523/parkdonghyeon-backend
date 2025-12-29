package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.transaction.result.AccountLimitResult;

@Schema(description = "한도 조회 응답")
public record AccountLimitResponse(
        @Schema(description = "한도", example = "0")
        long limit
) {
    public static AccountLimitResponse from(AccountLimitResult result) {
        return new AccountLimitResponse(
                result.limit().amount()
        );
    }
}
