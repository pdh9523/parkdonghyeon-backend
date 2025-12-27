package site.donghyeon.bank.application.account.result;

import site.donghyeon.bank.application.account.task.DepositTask;

import java.util.UUID;

public record DepositResult(
        UUID eventId
) {
    public static DepositResult from(DepositTask task) {
        return new DepositResult(
                task.eventId()
        );
    }
}
