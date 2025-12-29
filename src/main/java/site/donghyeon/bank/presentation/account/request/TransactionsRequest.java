package site.donghyeon.bank.presentation.account.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.transaction.query.TransactionsQuery;
import site.donghyeon.bank.common.exception.BadRequestException;

import java.util.UUID;

@Schema(description = "거래 내역 조회 요청")
public record TransactionsRequest(
        @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
        Integer page,
        @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
        Integer size
) {
    public TransactionsQuery toQuery(UUID userId, UUID accountId) {
        if (accountId == null) {
            throw new BadRequestException("accountId is null");
        }

        return new TransactionsQuery(
                userId,
                accountId,
                this.page == null ? 0 : page,
                this.size == null ? 20 : size
        );
    }
}
