package site.donghyeon.bank.application.account.operation.task;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalMessage;

import java.util.UUID;

public record WithdrawalTask(
        UUID eventId,
        UUID accountId,
        Money amount
) {
    public static WithdrawalTask from(WithdrawalMessage msg) {
        return new WithdrawalTask(
                msg.eventId(),
                msg.accountId(),
                new Money(msg.amount())
        );
    }
}
