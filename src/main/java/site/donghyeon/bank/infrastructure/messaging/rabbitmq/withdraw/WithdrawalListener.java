package site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.operation.executor.WithdrawalExecutor;
import site.donghyeon.bank.application.account.operation.task.WithdrawalTask;

@Component
public class WithdrawalListener {

    private final WithdrawalExecutor withdrawalExecutor;

    public WithdrawalListener(WithdrawalExecutor withdrawalExecutor) {
        this.withdrawalExecutor = withdrawalExecutor;
    }

    @RabbitListener(queues = WithdrawalRabbitMQConfig.WITHDRAWAL_QUEUE)
    public void handle(WithdrawalMessage msg) {
        withdrawalExecutor.execute(WithdrawalTask.from(msg));
    }
}
