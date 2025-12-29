package site.donghyeon.bank.presentation.common.resolver;

import java.util.UUID;

public record CurrentUser(
        UUID userId,
        String email
) {
}
