package site.donghyeon.bank.application.user;

import org.springframework.stereotype.Service;
import site.donghyeon.bank.application.user.command.GetUserInfoCommand;
import site.donghyeon.bank.application.user.command.RegisterCommand;
import site.donghyeon.bank.application.user.result.GetUserInfoResult;
import site.donghyeon.bank.application.user.result.RegisterResult;
import site.donghyeon.bank.domain.user.User;
import site.donghyeon.bank.application.user.exception.EmailAlreadyExistsException;
import site.donghyeon.bank.domain.user.repository.UserRepository;

@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegisterResult register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }
        // TODO: keycloak 에 유저 생성

        return RegisterResult.from(
                userRepository.save(
                        User.newUser(command.email())
                )
        );
    }

    @Override
    public GetUserInfoResult getUserInfo(GetUserInfoCommand command) {
        return GetUserInfoResult.from(
                userRepository.findById(command.userId())
        );
    }
}
