package site.donghyeon.bank.infrastructure.jpa.account.adapter;

import org.springframework.stereotype.Repository;
import site.donghyeon.bank.application.account.view.AccountView;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.application.account.repository.AccountRepository;
import site.donghyeon.bank.infrastructure.jpa.account.entity.AccountJpaEntity;
import site.donghyeon.bank.infrastructure.jpa.account.mapper.AccountMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    public AccountRepositoryAdapter(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        return accountJpaRepository.findById(accountId)
                .map(AccountMapper::toDomain);
    }

    @Override
    public List<AccountView> findAllByUserId(UUID userId) {
        return accountJpaRepository.findAllByUserId(userId).stream()
                .map(AccountMapper::toAccountView)
                .toList();
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountMapper.toEntity(account);
        AccountJpaEntity saved = accountJpaRepository.save(entity);
        return AccountMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(UUID accountId) {
        return accountJpaRepository.existsById(accountId);
    }

    @Override
    public boolean existsByUserIdAndAccountId(UUID userId, UUID accountId) {
        return accountJpaRepository.existsByUserIdAndId(userId, accountId);
    }
}
