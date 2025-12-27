package site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.task.DepositTask;

@Component
public class DepositPublisher {

    private final RabbitTemplate rabbitTemplate;

    public DepositPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(DepositTask task) {
        DepositMessage message =
                new DepositMessage(
                        task.txId(),
                        task.toAccountId(),
                        task.amount().amount()
                );

        rabbitTemplate.convertAndSend(
                DepositRabbitMQConfig.DEPOSIT_EXCHANGE,
                "%s.%s".formatted(
                        DepositRabbitMQConfig.DEPOSIT_ROUTING_KEY,
                        task.toAccountId()
                ),
                message
        );
    }
}
