package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.transaction.result.TransactionEventResult;

import java.util.List;

@Schema(description = "거래 이벤트 조회 응답")
public record TransactionEventResponse(
        @Schema(description = "거래 이벤트")
        List<TransactionItem> accountTransactions
) {

    public static TransactionEventResponse from(TransactionEventResult result) {
        return new TransactionEventResponse(
            result.accountTransactions().stream()
                    .map(TransactionItem::from)
                    .toList()
        );
    }
}
