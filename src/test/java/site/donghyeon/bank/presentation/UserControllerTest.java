package site.donghyeon.bank.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.donghyeon.bank.application.user.UserUseCase;
import site.donghyeon.bank.application.user.result.GetUserInfoResult;
import site.donghyeon.bank.presentation.user.controller.UserController;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final UUID TEST_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String TEST_EMAIL = "user@example.com";

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    void 인증된_유저_정보_조회_성공() throws Exception {
        when(userUseCase.getUserInfo(any()))
                .thenReturn(new GetUserInfoResult(TEST_USER_ID, TEST_EMAIL));

        MvcResult result = mockMvc.perform(
                        get("/user")
                                .with(org.springframework.security.test.web.servlet.request
                                        .SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt
                                                .claim("sub", TEST_USER_ID.toString())
                                                .claim("email", TEST_EMAIL)
                                        )
                                ))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains(TEST_EMAIL);
    }
}