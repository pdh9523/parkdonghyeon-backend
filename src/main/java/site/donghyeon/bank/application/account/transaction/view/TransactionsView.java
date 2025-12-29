package site.donghyeon.bank.application.account.transaction.view;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.accountTransaction.enums.TransactionType;

import java.time.Instant;
import java.util.UUID;

public record TransactionsView(
        UUID eventId,
        Instant createdAt,
        TransactionType type,
        Money amount,
        Money balanceAfter
) {
}
