package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.result.DepositResult;

import java.util.UUID;

@Schema(description = "계좌 입금 응답")
public record DepositResponse(
        @Schema(description = "거래 내역 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID eventId
) {
    public static DepositResponse from(DepositResult result) {
        return new DepositResponse(
                result.eventId()
        );
    }
}
