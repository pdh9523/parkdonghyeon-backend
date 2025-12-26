package site.donghyeon.bank.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.infrastructure.jpa.user.adapter.UserRepositoryAdapter;
import site.donghyeon.bank.infrastructure.jpa.user.exception.UserNotFoundException;

@DataJpaTest
@Import(UserRepositoryAdapter.class)
class UserRepositoryTest {

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String TEST_EMAIL = "user@example.com";

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void 저장_후_조회_테스트() {
        User saved = userRepositoryAdapter.save(new User(TEST_USER_ID, TEST_EMAIL));

        User found = userRepositoryAdapter.findById(saved.getUserId());

        assertThat(found.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(found.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void 저장_후_존재여부_테스트() {
        userRepositoryAdapter.save(new User(TEST_USER_ID, TEST_EMAIL));

        boolean exists = userRepositoryAdapter.existsByEmail(TEST_EMAIL);

        assertThat(exists).isTrue();
    }

    @Test
    void 조회_불가_시_에러_반환() {
        assertThatThrownBy(() -> userRepositoryAdapter.findById(TEST_USER_ID))
                .isInstanceOf(UserNotFoundException.class);
    }
}
