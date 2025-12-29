package site.donghyeon.bank.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.account.Account;
import site.donghyeon.bank.domain.account.enums.AccountStatus;
import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.infrastructure.jpa.account.adapter.AccountRepositoryAdapter;
import site.donghyeon.bank.infrastructure.jpa.user.adapter.UserRepositoryAdapter;

@Testcontainers
@SpringBootTest
@Import(AccountRepositoryAdapter.class)
class AccountRepositoryTest {

    private static final UUID TEST_ACCOUNT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final UUID TEST_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

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

        // 핵심 설정
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.generate-ddl", () -> "false");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @MockitoBean
    SecurityFilterChain securityFilterChain;

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        userRepositoryAdapter.save(
                new User(
                        TEST_USER_ID,
                        "test@test.com"
                )
        );
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE account_transactions CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE accounts CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    @Test
    void 저장_후_조회_테스트() {
        accountRepositoryAdapter.save(
                new Account(
                        TEST_ACCOUNT_ID,
                        TEST_USER_ID,
                        Money.zero(),
                        AccountStatus.OPEN
                )
        );

        Optional<Account> found = accountRepositoryAdapter.findById(TEST_ACCOUNT_ID);

        assertThat(found).isPresent();

        Account account = found.get();
        assertThat(account.getAccountId()).isEqualTo(TEST_ACCOUNT_ID);
        assertThat(account.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(account.getBalance().amount()).isZero();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.OPEN);
    }

    @Test
    void 조회_불가_시_에러_반환() {
        Optional<Account> found = accountRepositoryAdapter.findById(TEST_ACCOUNT_ID);
        assertThat(found).isNotPresent();
    }
}