package site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.operation.task.WithdrawalTask;

@Component
public class WithdrawalPublisher {

    private final RabbitTemplate rabbitTemplate;

    public WithdrawalPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(WithdrawalTask task) {
        WithdrawalMessage message =
                new WithdrawalMessage(
                        task.eventId(),
                        task.accountId(),
                        task.amount().amount()
                );

        rabbitTemplate.convertAndSend(
                WithdrawalRabbitMQConfig.WITHDRAWAL_EXCHANGE,
                "%s.%s".formatted(
                        WithdrawalRabbitMQConfig.WITHDRAWAL_ROUTING_KEY,
                        task.accountId()
                ),
                message
        );
    }
}
