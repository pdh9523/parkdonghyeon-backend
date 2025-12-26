package site.donghyeon.bank.presentation.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.user.result.RegisterResult;

import java.util.UUID;

@Schema(description = "회원 등록 응답")
public record RegisterResponse(
        @Schema(description = "생성된 사용자 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID userId,
        @Schema(description = "등록된 이메일", example = "user@example.com")
        String email
) {
    public static RegisterResponse from(RegisterResult result) {
        return new RegisterResponse(
                result.userId(),
                result.email()
        );
    }
}
