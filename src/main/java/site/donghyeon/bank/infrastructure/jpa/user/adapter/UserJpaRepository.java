package site.donghyeon.bank.infrastructure.jpa.user.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import site.donghyeon.bank.infrastructure.jpa.user.entity.UserJpaEntity;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);
}
