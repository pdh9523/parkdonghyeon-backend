package site.donghyeon.bank.application.account.repository;

import site.donghyeon.bank.domain.account.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<Account> findById(UUID accountId);
    Account save(Account account);
    boolean existsById(UUID accountId);
}
