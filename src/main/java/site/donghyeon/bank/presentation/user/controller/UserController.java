package site.donghyeon.bank.presentation.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.donghyeon.bank.application.user.UserUseCase;
import site.donghyeon.bank.presentation.user.request.GetUserInfoRequest;
import site.donghyeon.bank.presentation.user.request.RegisterRequest;
import site.donghyeon.bank.presentation.user.response.GetUserInfoResponse;
import site.donghyeon.bank.presentation.user.response.RegisterResponse;

@RestController
@RequestMapping("/user")
@Tag(name = "유저", description = "유저 관련 API 입니다.")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping()
    @Operation(
            summary = "회원 등록",
            description = "<p>이메일과 비밀번호로 신규 사용자를 등록합니다.</p>"
    )
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(
                RegisterResponse.from(
                    userUseCase.register(request.toCommand())
                )
        );
    }

    @GetMapping()
    @Operation(
            summary = "회원 조회",
            description = "<p>회원의 정보를 조회합니다.</p>" +
                    "<p>TODO: keycloak 도입 시 인증을 통해 파라미터를 받도록 수정합니다.</p>"
    )
    public ResponseEntity<GetUserInfoResponse> getUserInfo(
            @RequestParam GetUserInfoRequest request
    ) {
        return ResponseEntity.ok(
                GetUserInfoResponse.from(
                        userUseCase.getUserInfo(request.toCommand())
                )
        );
    }
}
