package site.donghyeon.bank.application.account.repository;

import site.donghyeon.bank.application.account.view.AccountView;
import site.donghyeon.bank.domain.account.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<Account> findById(UUID accountId);
    List<AccountView> findAllByUserId(UUID userId);
    Account save(Account account);
    boolean existsById(UUID accountId);
    boolean existsByUserIdAndAccountId(UUID userId, UUID accountId);
}
