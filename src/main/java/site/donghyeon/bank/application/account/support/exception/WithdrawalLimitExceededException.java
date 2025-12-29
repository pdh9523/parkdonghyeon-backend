package site.donghyeon.bank.application.account.support.exception;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.common.exception.BadRequestException;

public class WithdrawalLimitExceededException extends BadRequestException {
    public WithdrawalLimitExceededException(Money spentLimit, Money requested) {
        super("Withdrawal limit exceeded, (spent: %d, requested: %d)"
                .formatted(
                        spentLimit.amount(),
                        requested.amount()
                )
        );
    }
}
