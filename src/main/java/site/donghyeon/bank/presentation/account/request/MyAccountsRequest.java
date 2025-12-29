package site.donghyeon.bank.presentation.account.request;

import site.donghyeon.bank.application.account.management.query.MyAccountsQuery;

import java.util.UUID;

public record MyAccountsRequest(
        UUID userId
) {
    public static MyAccountsRequest from(UUID userId) {
        return new MyAccountsRequest(userId);
    }

    public MyAccountsQuery toQuery() {
        return new MyAccountsQuery(this.userId);
    }
}
