package site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer;

import java.util.UUID;

public record TransferMessage(
        UUID eventId,
        UUID fromAccountId,
        UUID toAccountId,
        long amount
) {
}
