package site.donghyeon.bank.presentation.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import site.donghyeon.bank.application.account.management.result.MyAccountsResult;
import site.donghyeon.bank.application.account.management.view.AccountView;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "계좌 조회 응답")
public record MyAccountsResponse(
        @Schema(description = "계좌 리스트")
        List<AccountItem> accounts
) {
    public record AccountItem(
            @Schema(description = "계좌 식별자", example = "00000000-0000-0000-0000-000000000000")
            UUID accountId,
            @Schema(description = "계좌 잔고", example = "0")
            long amount,
            @Schema(description = "계좌 생성일", example = "2006-01-02T00:00:00.000000Z")
            Instant createdAt
    ) {
        public static AccountItem from(AccountView view) {
            return new AccountItem(
                    view.accountId(),
                    view.balance().amount(),
                    view.createdAt()
            );
        }
    }
    public static MyAccountsResponse from(MyAccountsResult result) {
        return new MyAccountsResponse(
                result.accounts().stream()
                        .map(AccountItem::from)
                        .toList()
        );
    }
}
