package site.donghyeon.bank.application.account.support.exception;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.common.exception.BadRequestException;

public class RemainingBalanceException extends BadRequestException {
    public RemainingBalanceException(Money balance) {
        super("This account remain balance, balance: %d".formatted(balance.amount()));
    }
}
