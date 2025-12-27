package site.donghyeon.bank.application.account.task;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositMessage;

import java.util.UUID;

public record DepositTask(
        UUID txId,
        UUID toAccountId,
        Money amount
) {
    public static DepositTask from(DepositMessage msg) {
        return new DepositTask(
                msg.txId(),
                msg.toAccountId(),
                new Money(msg.amount())
        );
    }
}
