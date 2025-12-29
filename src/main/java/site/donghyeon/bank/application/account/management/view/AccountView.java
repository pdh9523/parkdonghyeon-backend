package site.donghyeon.bank.application.account.management.view;

import site.donghyeon.bank.common.domain.Money;

import java.time.Instant;
import java.util.UUID;

public record AccountView(
        UUID accountId,
        Money balance,
        Instant createdAt
) {
}
