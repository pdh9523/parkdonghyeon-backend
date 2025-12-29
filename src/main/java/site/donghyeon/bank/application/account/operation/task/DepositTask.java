package site.donghyeon.bank.application.account.operation.task;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositMessage;

import java.util.UUID;

public record DepositTask(
        UUID eventId,
        UUID accountId,
        Money amount
) {
    public static DepositTask from(DepositMessage msg) {
        return new DepositTask(
                msg.eventId(),
                msg.accountId(),
                new Money(msg.amount())
        );
    }
}
