package site.donghyeon.bank.domain.user.repository;

import site.donghyeon.bank.domain.user.User;

import java.util.UUID;

public interface UserRepository {
    User findById(UUID userId);
    User save(User user);
    boolean existsByEmail(String email);
}
