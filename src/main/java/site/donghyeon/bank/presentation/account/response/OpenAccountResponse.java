package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.management.result.OpenAccountResult;

import java.util.UUID;

@Schema(description = "계좌 등록 응답")
public record OpenAccountResponse(
        @Schema(description = "등록된 계좌 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID accountId,
        @Schema(description = "계좌 잔고", example = "0")
        long balance
) {
    public static OpenAccountResponse from(OpenAccountResult result) {
        return new OpenAccountResponse(
                result.accountId(),
                result.balance().amount()
        );
    }
}
