package site.donghyeon.bank.application.account.operation.command;

import java.util.UUID;

public record DepositCommand(
        UUID toAccountId,
        long amount
) {
}
