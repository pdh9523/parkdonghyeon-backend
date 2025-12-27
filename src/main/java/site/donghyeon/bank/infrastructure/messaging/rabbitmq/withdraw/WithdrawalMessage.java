package site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw;

import java.util.UUID;

public record WithdrawalMessage(
        UUID eventId,
        UUID accountId,
        long amount
) {
}
