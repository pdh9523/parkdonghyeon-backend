package site.donghyeon.bank.application.account.operation.result;

import site.donghyeon.bank.application.account.operation.task.DepositTask;

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
