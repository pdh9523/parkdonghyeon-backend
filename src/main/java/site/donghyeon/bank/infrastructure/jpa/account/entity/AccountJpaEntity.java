package site.donghyeon.bank.infrastructure.jpa.account.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.infrastructure.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(
        name = "accounts",
        indexes = {
                @Index(name = "idx_user_stats", columnList = "userId, status")
        }
)
@Access(AccessType.FIELD)
@SQLDelete(sql = "UPDATE accounts SET status = 'CLOSED'::account_status WHERE id = ?")
@SQLRestriction("status = 'OPEN'::account_status")
public class AccountJpaEntity extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "balance", nullable = false)
    private Long balance = 0L;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    protected AccountJpaEntity() {}

    public AccountJpaEntity(UUID id, UUID userId, Long balance,  AccountStatus status) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.status = status;
    }

    public UUID getId() {
        return this.id;
    }

    public Long getBalance() {
        return this.balance;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public AccountStatus getStatus() {
        return this.status;
    }
}
