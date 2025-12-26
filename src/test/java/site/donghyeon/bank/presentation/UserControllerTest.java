package site.donghyeon.bank.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.donghyeon.bank.application.user.UserUseCase;
import site.donghyeon.bank.application.user.result.RegisterResult;
import site.donghyeon.bank.presentation.user.controller.UserController;
import site.donghyeon.bank.presentation.user.request.RegisterRequest;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "Secret123!";

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void 회원가입_성공_시_200과_응답() throws Exception {
        when(userUseCase.register(any())).thenReturn(new RegisterResult(TEST_USER_ID, TEST_EMAIL));
        RegisterRequest request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains(TEST_EMAIL);
    }

    @Test
    void 이메일_형식_오류_시_400() throws Exception {
        RegisterRequest request = new RegisterRequest("invalid-email", TEST_PASSWORD);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 비밀번호_형식_오류_시_400() throws Exception {
        RegisterRequest request = new RegisterRequest(TEST_EMAIL, "short");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
