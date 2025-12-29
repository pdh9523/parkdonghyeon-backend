package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.donghyeon.bank.application.account.operation.command.TransferCommand;
import site.donghyeon.bank.common.exception.BadRequestException;

import java.util.UUID;

public record TransferRequest(
        @Schema(description = "상대 계좌 ID", example = "00000000-0000-0000-0000-000000000000")
        @NotBlank(message = "ID는 필수입니다.")
        UUID toAccountId,
        @Schema(description = "금액", example = "0")
        @NotBlank(message = "이체할 금액은 필수입니다.")
        long amount
) {
    public TransferCommand toCommand(UUID userId, UUID fromAccountId) {
        if (fromAccountId == null) {
            throw new BadRequestException("accountID is required.");
        }
        return new TransferCommand(
                userId,
                fromAccountId,
                this.toAccountId,
                this.amount
        );
    }
}
