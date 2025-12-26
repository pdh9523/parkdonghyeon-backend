package site.donghyeon.bank.application.user.command;

import java.util.UUID;

public record GetUserInfoCommand(
        UUID userId
) {
}
