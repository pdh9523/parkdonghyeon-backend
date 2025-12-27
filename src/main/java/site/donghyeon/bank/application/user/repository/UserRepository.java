package site.donghyeon.bank.application.user.repository;

import site.donghyeon.bank.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID userId);
    User save(User user);
    boolean existsByEmail(String email);
}
