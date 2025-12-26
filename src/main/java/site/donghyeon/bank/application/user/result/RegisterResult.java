package site.donghyeon.bank.application.user.result;

import site.donghyeon.bank.domain.user.User;

import java.util.UUID;

public record RegisterResult(
        UUID userId,
        String email
) {
    public static RegisterResult from(User user) {
        return new RegisterResult(
                user.getUserId(),
                user.getEmail()
        );
    }
}
