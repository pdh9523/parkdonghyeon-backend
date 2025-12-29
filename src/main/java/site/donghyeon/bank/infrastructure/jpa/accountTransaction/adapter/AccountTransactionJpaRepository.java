package site.donghyeon.bank.infrastructure.jpa.accountTransaction.adapter;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.donghyeon.bank.domain.accountTransaction.enums.TransactionType;
import site.donghyeon.bank.infrastructure.jpa.accountTransaction.entity.AccountTransactionJpaEntity;

import java.time.Instant;
import java.util.UUID;

public interface AccountTransactionJpaRepository extends JpaRepository<AccountTransactionJpaEntity, UUID> {

    Page<AccountTransactionJpaEntity> findByAccountId(UUID accountId, Pageable pageable);
    boolean existsByEventId(UUID eventId);
    @Query("""
        select coalesce(sum(t.amount), 0)
        from AccountTransactionJpaEntity t
        where t.accountId = :accountId
          and t.transactionType = :txType
          and t.createdAt >= :from
          and t.createdAt < :to
    """)
    long sumDailyAmount(
            @Param("accountId") UUID accountId,
            @Param("txType") TransactionType txType,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}
