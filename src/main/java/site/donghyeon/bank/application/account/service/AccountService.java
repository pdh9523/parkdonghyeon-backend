package site.donghyeon.bank.application.account.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.donghyeon.bank.application.account.AccountUseCase;
import site.donghyeon.bank.application.account.command.CloseAccountCommand;
import site.donghyeon.bank.application.account.command.OpenAccountCommand;
import site.donghyeon.bank.application.account.exception.AccountNotFoundException;
import site.donghyeon.bank.application.account.exception.RemainingBalanceException;
import site.donghyeon.bank.application.account.query.MyAccountsQuery;
import site.donghyeon.bank.application.account.result.MyAccountsResult;
import site.donghyeon.bank.application.account.result.OpenAccountResult;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.application.account.repository.AccountRepository;

import java.util.UUID;

@Service
public class AccountService implements AccountUseCase {

    private final AccountRepository accountRepository;

    public AccountService(
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
