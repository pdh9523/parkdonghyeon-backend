package site.donghyeon.bank.application.account.support.exception;

import site.donghyeon.bank.common.exception.NotFoundException;

import java.util.UUID;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException(UUID id) {
        super("Account not found, id: %s".formatted(id));
    }
}
