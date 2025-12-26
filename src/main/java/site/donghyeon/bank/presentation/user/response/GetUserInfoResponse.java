package site.donghyeon.bank.presentation.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.user.result.GetUserInfoResult;

import java.util.UUID;

@Schema(description = "회원 조회 응답")
public record GetUserInfoResponse(
        @Schema(description = "조회된 사용자 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID userId,
        @Schema(description = "조회된 사용자 이메일", example = "user@example.com")
        String email
) {
    public static GetUserInfoResponse from(GetUserInfoResult result) {
        return new GetUserInfoResponse(
                result.userId(),
                result.email()
        );
    }
}
