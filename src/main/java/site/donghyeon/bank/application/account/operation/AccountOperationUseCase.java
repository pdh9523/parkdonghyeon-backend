package site.donghyeon.bank.application.account.operation;

import site.donghyeon.bank.application.account.operation.command.DepositCommand;
import site.donghyeon.bank.application.account.operation.command.TransferCommand;
import site.donghyeon.bank.application.account.operation.command.WithdrawalCommand;
import site.donghyeon.bank.application.account.operation.result.DepositResult;
import site.donghyeon.bank.application.account.operation.result.TransferResult;
import site.donghyeon.bank.application.account.operation.result.WithdrawalResult;

public interface AccountOperationUseCase {
    DepositResult deposit(DepositCommand command);
    WithdrawalResult withdrawal(WithdrawalCommand command);
    TransferResult transfer(TransferCommand command);
}
