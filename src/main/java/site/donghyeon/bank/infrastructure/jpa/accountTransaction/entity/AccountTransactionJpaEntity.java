package site.donghyeon.bank.infrastructure.jpa.accountTransaction.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import site.donghyeon.bank.domain.accountTransaction.enums.TransactionType;
import site.donghyeon.bank.infrastructure.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(
        name = "account_transactions",
        indexes = {
                @Index(name = "idx_tx_account_id", columnList = "account_id")
        }
)
public class AccountTransactionJpaEntity extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "account_id", columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "event_id", columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    public AccountTransactionJpaEntity(
            UUID id, UUID accountId, UUID eventId, long amount,
            TransactionType transactionType
    ) {
        this.id = id;
        this.accountId = accountId;
        this.eventId = eventId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    protected AccountTransactionJpaEntity() {}

    public UUID getId() {
        return this.id;
    }

    public UUID getAccountId() {
        return this.accountId;
    }

    public UUID getEventId() {
        return this.eventId;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public long getAmount() {
        return this.amount;
    }
}
