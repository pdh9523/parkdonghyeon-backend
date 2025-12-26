package site.donghyeon.bank.application.user;

import site.donghyeon.bank.application.user.command.GetUserInfoCommand;
import site.donghyeon.bank.application.user.command.RegisterCommand;
import site.donghyeon.bank.application.user.result.GetUserInfoResult;
import site.donghyeon.bank.application.user.result.RegisterResult;

public interface UserUseCase {
    RegisterResult register(RegisterCommand command);
    GetUserInfoResult getUserInfo(GetUserInfoCommand command);
}
