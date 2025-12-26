package site.donghyeon.bank.application.user.command;

public record RegisterCommand(
        String email,
        String password
) {
}
