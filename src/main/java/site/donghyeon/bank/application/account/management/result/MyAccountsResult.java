package site.donghyeon.bank.application.account.management.result;

import site.donghyeon.bank.application.account.management.view.AccountView;

import java.util.List;

public record MyAccountsResult(
        List<AccountView> accounts
) {
    public static MyAccountsResult from(List<AccountView> accounts) {
        return new MyAccountsResult(accounts);
    }
}
