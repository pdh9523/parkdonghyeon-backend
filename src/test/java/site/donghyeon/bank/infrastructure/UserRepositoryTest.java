package site.donghyeon.bank.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.infrastructure.jpa.user.adapter.UserRepositoryAdapter;

@Testcontainers
@SpringBootTest
@Import(UserRepositoryAdapter.class)
class UserRepositoryTest {

    private static final UUID TEST_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final String TEST_EMAIL = "user@example.com";

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("bank")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Hibernate는 절대 DDL 생성하지 않음
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.generate-ddl", () -> "false");

        // Flyway로만 스키마 관리
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @MockitoBean
    SecurityFilterChain securityFilterChain;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE account_transactions CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE accounts CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    @Test
    void 저장_후_조회_테스트() {
        userRepositoryAdapter.save(new User(TEST_USER_ID, TEST_EMAIL));

        Optional<User> found = userRepositoryAdapter.findById(TEST_USER_ID);

        assertThat(found).isPresent();

        User user = found.get();
        assertThat(user.userId()).isEqualTo(TEST_USER_ID);
        assertThat(user.email()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void 저장_후_존재여부_테스트() {
        userRepositoryAdapter.save(new User(TEST_USER_ID, TEST_EMAIL));

        boolean exists = userRepositoryAdapter.existsByEmail(TEST_EMAIL);

        assertThat(exists).isTrue();
    }

    @Test
    void 조회_불가_시_에러_반환() {
        Optional<User> found = userRepositoryAdapter.findById(TEST_USER_ID);
        assertThat(found).isNotPresent();
    }
}