package site.donghyeon.bank.infrastructure.jpa.user.mapper;

import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.infrastructure.jpa.user.entity.UserJpaEntity;

public final class UserMapper {

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getUserId(),
                user.getEmail()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getUserId(),
                entity.getEmail()
        );
    }
}
