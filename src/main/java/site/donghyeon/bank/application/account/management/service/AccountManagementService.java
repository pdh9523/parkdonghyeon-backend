package site.donghyeon.bank.application.account.management.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.donghyeon.bank.application.account.management.AccountManagementUseCase;
import site.donghyeon.bank.application.account.management.command.CloseAccountCommand;
import site.donghyeon.bank.application.account.management.command.OpenAccountCommand;
import site.donghyeon.bank.application.account.support.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.support.exception.RemainingBalanceException;
import site.donghyeon.bank.application.account.management.query.MyAccountsQuery;
import site.donghyeon.bank.application.account.management.result.MyAccountsResult;
import site.donghyeon.bank.application.account.management.result.OpenAccountResult;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.application.account.support.repository.AccountRepository;

import java.util.UUID;

@Service
public class AccountManagementService implements AccountManagementUseCase {

    private final AccountRepository accountRepository;

    public AccountManagementService(
            AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }

    @Override
    public MyAccountsResult getMyAccounts(MyAccountsQuery query) {
        return MyAccountsResult.from(accountRepository.findAllByUserId(query.userId()));
    }

    @Override
    @Transactional
    public OpenAccountResult openAccount(OpenAccountCommand command) {
        return OpenAccountResult.from(
                accountRepository.save(
                        Account.open(UUID.randomUUID(), command.userId())
                )
        );
    }

    @Override
    @Transactional
    public void closeAccount(CloseAccountCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (account.getBalance().exceeded(Money.zero())) {
            throw new RemainingBalanceException(account.getBalance());
        }

        account.close(command.userId());
        accountRepository.save(account);
    }
}
