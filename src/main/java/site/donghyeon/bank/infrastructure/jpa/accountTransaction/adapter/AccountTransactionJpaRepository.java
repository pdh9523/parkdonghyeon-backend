package site.donghyeon.bank.infrastructure.jpa.accountTransaction.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import site.donghyeon.bank.infrastructure.jpa.accountTransaction.entity.AccountTransactionJpaEntity;

import java.util.UUID;

public interface AccountTransactionJpaRepository extends JpaRepository<AccountTransactionJpaEntity, UUID> {

    boolean existsByEventId(UUID eventId);
}
