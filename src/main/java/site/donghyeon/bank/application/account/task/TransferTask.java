package site.donghyeon.bank.application.account.task;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferMessage;

import java.util.UUID;

public record TransferTask(
        UUID eventId,
        UUID fromAccountId,
        UUID toAccountId,
        Money amount
) {
    public static TransferTask from(TransferMessage msg) {
        return new TransferTask(
                msg.eventId(),
                msg.fromAccountId(),
                msg.toAccountId(),
                new Money(msg.amount())
        );
    }
}
