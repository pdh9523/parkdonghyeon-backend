package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.operation.result.TransferResult;

import java.util.UUID;

@Schema(description = "계좌 이체 응답")
public record TransferResponse(
        @Schema(description = "거래 내역 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID eventId
) {
    public static TransferResponse from(TransferResult result) {
        return new TransferResponse(
                result.eventId()
        );
    }
}
