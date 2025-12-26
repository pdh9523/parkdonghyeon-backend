package site.donghyeon.bank.application.user.result;

import site.donghyeon.bank.domain.user.User;

import java.util.UUID;

public record GetUserInfoResult(
        UUID userId,
        String email
) {
    public static GetUserInfoResult from(User user) {
        return new GetUserInfoResult(user.getUserId(), user.getEmail());
    }
}
