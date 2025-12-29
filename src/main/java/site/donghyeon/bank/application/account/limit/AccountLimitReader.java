package site.donghyeon.bank.application.account.limit;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.accountTransaction.enums.LimitType;

import java.util.UUID;

public interface AccountLimitReader {
    Money checkTransferLimit(UUID accountId);
    Money checkWithdrawalLimit(UUID accountId);
    Money checkLimit(UUID accountId, LimitType type);
    boolean tryConsumeTransfer(UUID accountId, Money amount, Money limit);
    boolean tryConsumeWithdrawal(UUID accountId, Money amount, Money limit);
    void rollbackTransferLimit(UUID accountId, Money amount);
    void rollbackWithdrawalLimit(UUID accountId, Money amount);
}
