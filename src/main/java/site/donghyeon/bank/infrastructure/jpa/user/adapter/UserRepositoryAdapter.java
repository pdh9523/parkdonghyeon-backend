package site.donghyeon.bank.infrastructure.jpa.user.adapter;

import org.springframework.stereotype.Repository;
import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.domain.user.repository.UserRepository;
import site.donghyeon.bank.infrastructure.jpa.user.entity.UserJpaEntity;
import site.donghyeon.bank.infrastructure.jpa.user.exception.UserNotFoundException;
import site.donghyeon.bank.infrastructure.jpa.user.mapper.UserMapper;

import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User findById(UUID userId) {
        UserJpaEntity entity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId));
        return UserMapper.toDomain(entity);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserMapper.toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
