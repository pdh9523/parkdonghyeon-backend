package site.donghyeon.bank.presentation.account.response;

import site.donghyeon.bank.application.account.transaction.result.TransactionsResult;
import site.donghyeon.bank.application.account.transaction.view.TransactionsView;
import site.donghyeon.bank.domain.accountTransaction.enums.TransactionType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionsResponse(
        List<TransactionItem> accountTransactions,
        int page,
        int size,
        long total
) {
    public record TransactionItem(
            UUID eventId,
            Instant createdAt,
            TransactionType type,
            long amount,
            long balanceAfter
    ) {
        public static TransactionItem from(TransactionsView view) {
            return new TransactionItem(
                    view.eventId(),
                    view.createdAt(),
                    view.type(),
                    view.amount().amount(),
                    view.balanceAfter().amount()
            );
        }
    }
    public static TransactionsResponse from(TransactionsResult result) {
        return new TransactionsResponse(
                result.accountTransactions().stream()
                        .map(TransactionItem::from)
                        .toList(),
                result.page(),
                result.size(),
                result.total()
        );
    }
}
