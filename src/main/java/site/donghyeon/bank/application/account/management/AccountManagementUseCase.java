package site.donghyeon.bank.application.account.management;

import site.donghyeon.bank.application.account.management.command.CloseAccountCommand;
import site.donghyeon.bank.application.account.management.command.OpenAccountCommand;
import site.donghyeon.bank.application.account.management.query.MyAccountsQuery;
import site.donghyeon.bank.application.account.management.result.MyAccountsResult;
import site.donghyeon.bank.application.account.management.result.OpenAccountResult;

public interface AccountManagementUseCase {
    MyAccountsResult getMyAccounts(MyAccountsQuery query);
    OpenAccountResult openAccount(OpenAccountCommand command);
    void closeAccount(CloseAccountCommand command);
}
