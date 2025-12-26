package site.donghyeon.bank.presentation.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import site.donghyeon.bank.application.user.command.RegisterCommand;

@Schema(description = "회원 등록 요청")
public record RegisterRequest(
        @Schema(description = "로그인용 이메일", example = "user@example.com", pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @Schema(description = "로그인 비밀번호(8~64자, 대소문자/숫자/특수문자 포함)", example = "Password123!", pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=?]).{8,64}$")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=?]).{8,64}$", message = "비밀번호는 8~64자이며 대소문자, 숫자, 특수문자를 포함해야 합니다.")
        String password
) {
    public RegisterCommand toCommand() {
        return new RegisterCommand(
                this.email,
                this.password
        );
    }
}
