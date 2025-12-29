package site.donghyeon.bank.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import site.donghyeon.bank.application.account.management.AccountManagementUseCase;
import site.donghyeon.bank.application.account.management.result.OpenAccountResult;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.presentation.account.controller.AccountController;
import site.donghyeon.bank.presentation.account.request.CloseAccountRequest;
import site.donghyeon.bank.presentation.account.request.OpenAccountRequest;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private AccountManagementUseCase accountManagementUseCase;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void 계좌_개설_성공시_200과_응답() throws Exception {
        when(accountManagementUseCase.openAccount(any())).thenReturn(new OpenAccountResult(TEST_ACCOUNT_ID, Money.zero()));
        OpenAccountRequest request = new OpenAccountRequest(TEST_USER_ID);

        MvcResult result = mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains(TEST_ACCOUNT_ID.toString());
    }

    @Test
    void 계좌_해지_성공시_204() throws Exception {
        doNothing().when(accountManagementUseCase).closeAccount(any());
        CloseAccountRequest request = new CloseAccountRequest(TEST_ACCOUNT_ID);

        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
