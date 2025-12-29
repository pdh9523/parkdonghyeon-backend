package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.transaction.query.TransactionEventQuery;

import java.util.UUID;

@Schema(description = "거래 이벤트 조회 요청")
public record TransactionEventRequest(
        UUID userId,
        UUID accountId,
        UUID eventId
) {
    public static TransactionEventRequest of(UUID userId, UUID accountId, UUID eventId) {
        return new TransactionEventRequest(userId, accountId, eventId);
    }

    public TransactionEventQuery toQuery() {
        return new TransactionEventQuery(userId, accountId, eventId);
    }
}
