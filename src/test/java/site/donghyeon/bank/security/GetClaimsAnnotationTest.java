package site.donghyeon.bank.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.donghyeon.bank.config.security.resolver.GetClaimsArgumentResolver;
import site.donghyeon.bank.presentation.common.controller.test.TestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(value = MockitoExtension.class)
public class GetClaimsAnnotationTest {

    private static final UUID TEST_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String TEST_EMAIL = "user@example.com";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TestController controller = new TestController();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new GetClaimsArgumentResolver())
                .build();
    }

    @Test
    void Jwt_추출_테스트() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", TEST_USER_ID.toString())
                .claim("email", TEST_EMAIL)
                .build();

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(post("/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId")
                        .value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.email")
                        .value(TEST_EMAIL));
    }
}