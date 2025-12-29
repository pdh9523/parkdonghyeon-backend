package site.donghyeon.bank.application.account.operation.command;

import java.util.UUID;

public record TransferCommand(
        UUID userId,
        UUID fromAccountId,
        UUID toAccountId,
        long amount
) {
}
