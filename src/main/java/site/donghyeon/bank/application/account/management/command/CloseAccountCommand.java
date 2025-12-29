package site.donghyeon.bank.application.account.management.command;

import java.util.UUID;

public record CloseAccountCommand(
        UUID userId,
        UUID accountId
) {
}
