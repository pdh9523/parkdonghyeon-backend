package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.donghyeon.bank.application.account.management.command.CloseAccountCommand;

import java.util.UUID;

@Schema(description = "계좌 해지 요청")
public record CloseAccountRequest(
        @Schema(description = "요청한 계좌 ID", example = "00000000-0000-0000-0000-000000000000")
        @NotBlank(message = "계좌 ID는 필수입니다.")
        UUID accountId
) {
    public CloseAccountCommand toCommand(UUID userId) {
        return new CloseAccountCommand(userId, accountId);
    }
}
