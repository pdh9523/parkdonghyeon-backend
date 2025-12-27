package site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.task.TransferTask;

@Component
public class TransferPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TransferPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(TransferTask task) {
        TransferMessage message =
                new TransferMessage(
                        task.eventId(),
                        task.fromAccountId(),
                        task.toAccountId(),
                        task.amount().amount()
                );

        rabbitTemplate.convertAndSend(
                TransferRabbitMQConfig.TRANSFER_EXCHANGE,
                "%s.%s".formatted(
                        TransferRabbitMQConfig.TRANSFER_ROUTING_KEY,
                        task.toAccountId()
                ),
                message
        );
    }
}
