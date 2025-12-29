package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.operation.result.WithdrawalResult;

import java.util.UUID;

@Schema(description = "계좌 출금 응답")
public record WithdrawalResponse(
        @Schema(description = "거래 내역 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID eventId
) {
    public static WithdrawalResponse from(WithdrawalResult result) {
        return new WithdrawalResponse(
            result.eventId()
        );
    }
}
