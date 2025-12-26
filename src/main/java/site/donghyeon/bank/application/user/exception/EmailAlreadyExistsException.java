package site.donghyeon.bank.application.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super("Email already exists, email: %s".formatted(message));
    }
}
