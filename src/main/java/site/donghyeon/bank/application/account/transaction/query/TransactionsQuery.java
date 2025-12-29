package site.donghyeon.bank.application.account.transaction.query;

import java.util.UUID;

public record TransactionsQuery(
        UUID userId,
        UUID accountId,
        int page,
        int size
) {
}
