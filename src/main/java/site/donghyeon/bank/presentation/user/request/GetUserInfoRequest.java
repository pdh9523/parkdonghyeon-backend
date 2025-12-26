package site.donghyeon.bank.presentation.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.donghyeon.bank.application.user.command.GetUserInfoCommand;

import java.util.UUID;

@Schema(description = "회원 조회 요청")
public record GetUserInfoRequest(
        @Schema(description = "조회할 사용자 ID", example = "00000000-0000-0000-0000-000000000000")
        @NotBlank(message = "ID는 필수입니다.")
        UUID userId
) {
    public GetUserInfoCommand toCommand() {
        return new GetUserInfoCommand(
                this.userId
        );
    }
}
