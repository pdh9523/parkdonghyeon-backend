package site.donghyeon.bank.application.account.support.exception;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.common.exception.BadRequestException;

public class TransferLimitExceededException extends BadRequestException {
    public TransferLimitExceededException(Money spentLimit, Money requested) {
        super("Transfer limit exceeded, (spent: %d, requested: %d)"
                .formatted(
                        spentLimit.amount(),
                        requested.amount()
                )
        );
    }
}
