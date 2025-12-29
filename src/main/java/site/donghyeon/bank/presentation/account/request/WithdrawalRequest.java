package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.donghyeon.bank.application.account.operation.command.WithdrawalCommand;
import site.donghyeon.bank.common.exception.BadRequestException;

import java.util.UUID;

@Schema(description = "계좌 출금 요청")
public record WithdrawalRequest(
        @Schema(description = "금액", example = "0")
        @NotBlank(message = "출금할 금액은 필수입니다.")
        long amount
) {
    public WithdrawalCommand toCommand(UUID userId, UUID accountId) {
        if (accountId == null) {
            throw new BadRequestException("AccountId is required");
        }
        return new WithdrawalCommand(
                userId,
                accountId,
                this.amount
        );
    }
}
