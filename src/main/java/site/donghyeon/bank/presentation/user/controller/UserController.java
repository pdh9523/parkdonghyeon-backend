package site.donghyeon.bank.presentation.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.donghyeon.bank.application.user.UserUseCase;
import site.donghyeon.bank.presentation.common.resolver.CurrentUser;
import site.donghyeon.bank.presentation.common.resolver.GetClaims;
import site.donghyeon.bank.presentation.user.request.GetUserInfoRequest;
import site.donghyeon.bank.presentation.user.response.GetUserInfoResponse;

@RestController
@RequestMapping("/user")
@Tag(name = "유저", description = "유저 관련 API 입니다.")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GetMapping()
    @Operation(
            summary = "회원 조회",
            description = "<p> 회원의 정보를 조회합니다. </p>" +
                    "<div> keycloak에서 회원가입을 진행하며, </div>" +
                    "<div> keycloak에서 로그인 후 받은 토큰을 통해 유저를 식별합니다. </div>" +
                    "<div> 회원 가입 후 한 번 실행해야 합니다. </div>"
    )
    public ResponseEntity<GetUserInfoResponse> getUserInfo(
            @GetClaims CurrentUser currentUser
            ) {
        return ResponseEntity.ok(
                GetUserInfoResponse.from(
                        userUseCase.getUserInfo(
                                GetUserInfoRequest.from(currentUser).toCommand()
                        )
                )
        );
    }
}
