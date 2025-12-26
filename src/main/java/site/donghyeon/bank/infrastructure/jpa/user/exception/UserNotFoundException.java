package site.donghyeon.bank.infrastructure.jpa.user.exception;

import site.donghyeon.bank.common.exception.InfraException;

public class UserNotFoundException extends InfraException {
    public UserNotFoundException(String type, Object query) {
        super("Account not found, %s: %s ".formatted(type, query));
    }


}
