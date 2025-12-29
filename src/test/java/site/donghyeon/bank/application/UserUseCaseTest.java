package site.donghyeon.bank.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.donghyeon.bank.application.user.UserService;
import site.donghyeon.bank.application.user.command.GetUserInfoCommand;
import site.donghyeon.bank.application.user.result.GetUserInfoResult;
import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.application.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String TEST_EMAIL = "user@example.com";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 유저_조회_테스트() {
        User existing = new User(TEST_USER_ID, TEST_EMAIL);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(existing));

        GetUserInfoCommand command = new GetUserInfoCommand(TEST_USER_ID, TEST_EMAIL);

        GetUserInfoResult result = userService.getUserInfo(command);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        assertThat(result.email()).isEqualTo(existing.email());

        verify(userRepository).findById(TEST_USER_ID);
    }
}
