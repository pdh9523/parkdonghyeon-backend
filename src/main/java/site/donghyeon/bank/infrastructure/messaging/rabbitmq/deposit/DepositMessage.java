package site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit;

import site.donghyeon.bank.application.account.task.DepositTask;

import java.util.UUID;

public record DepositMessage(
        UUID txId,
        UUID toAccountId,
        long amount
) {
    public static DepositMessage from(DepositTask task) {
        return new DepositMessage(
                task.txId(),
                task.toAccountId(),
                task.amount().amount()
        );
    }
}
