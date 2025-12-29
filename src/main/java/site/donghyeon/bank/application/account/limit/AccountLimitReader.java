package site.donghyeon.bank.application.account.limit;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.accountTransaction.enums.LimitType;

import java.util.UUID;

public interface AccountLimitReader {
    Money read(UUID accountId, LimitType type);
}
