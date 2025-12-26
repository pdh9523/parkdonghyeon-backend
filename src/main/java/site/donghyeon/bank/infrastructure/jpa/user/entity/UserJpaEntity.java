package site.donghyeon.bank.infrastructure.jpa.user.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class UserJpaEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    protected UserJpaEntity() {}

    public UserJpaEntity(UUID userId, String email) {
        this.id = userId;
        this.email = email;
    }

    public UUID getUserId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
}
