package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.management.command.OpenAccountCommand;
import site.donghyeon.bank.presentation.common.resolver.CurrentUser;

import java.util.UUID;

@Schema(description = "계좌 등록 요청 - 토큰에서 추출")
public record OpenAccountRequest(
        UUID userId
) {
    public static OpenAccountRequest from(CurrentUser currentUser) {
        return new OpenAccountRequest(currentUser.userId());
    }
    public OpenAccountCommand toCommand() {
        return new OpenAccountCommand(this.userId);
    }
}
