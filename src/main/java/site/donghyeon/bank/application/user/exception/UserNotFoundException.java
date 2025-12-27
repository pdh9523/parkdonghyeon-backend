package site.donghyeon.bank.application.user.exception;

import site.donghyeon.bank.common.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String type, Object query) {
        super("Account not found, %s: %s ".formatted(type, query));
    }
}
