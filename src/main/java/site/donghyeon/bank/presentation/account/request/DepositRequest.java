package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.donghyeon.bank.application.account.operation.command.DepositCommand;
import site.donghyeon.bank.common.exception.BadRequestException;

import java.util.UUID;

@Schema(description = "계좌 입금 요청")
public record DepositRequest(
        @Schema(description = "금액", example = "0")
        @NotBlank(message = "입금할 금액은 필수입니다.")
        long amount
) {
    public DepositCommand toCommand(UUID toAccountId) {
        if (toAccountId == null) {
            throw new BadRequestException("accountID is required");
        }
        return new DepositCommand(
                toAccountId,
                this.amount
        );
    }
}
