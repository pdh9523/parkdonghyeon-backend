package site.donghyeon.bank.infrastructure.jpa.user.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import site.donghyeon.bank.domain.user.enums.UserStatus;
import site.donghyeon.bank.infrastructure.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
@SQLDelete(sql = "UPDATE users SET status = 'DISABLED'::user_status WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'::user_status")
public class UserJpaEntity extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    protected UserJpaEntity() {}

    public UserJpaEntity(UUID userId, String email) {
        this.id = userId;
        this.email = email;
        this.status = UserStatus.ACTIVE;
    }

    public UUID getUserId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
}
