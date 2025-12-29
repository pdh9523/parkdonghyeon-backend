package site.donghyeon.bank.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.donghyeon.bank.application.account.operation.task.DepositTask;
import site.donghyeon.bank.application.account.operation.task.TransferTask;
import site.donghyeon.bank.application.account.operation.task.WithdrawalTask;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositListener;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositMessage;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.deposit.DepositRabbitMQConfig;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferListener;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferMessage;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.transfer.TransferRabbitMQConfig;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalListener;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalMessage;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalPublisher;
import site.donghyeon.bank.infrastructure.messaging.rabbitmq.withdraw.WithdrawalRabbitMQConfig;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=none",
                "spring.jpa.generate-ddl=false"
        }
)
public class RabbitMQPublisherTest {

    private static final UUID TEST_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID OTHER_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Container
    @ServiceConnection
    static RabbitMQContainer mq = new RabbitMQContainer("rabbitmq:3-management");

    @MockitoBean
    SecurityFilterChain securityFilterChain;

    @MockitoBean
    DepositListener depositListener;

    @MockitoBean
    WithdrawalListener withdrawalListener;

    @MockitoBean
    TransferListener transferListener;

    @Autowired
    DepositPublisher depositPublisher;

    @Autowired
    WithdrawalPublisher withdrawalPublisher;

    @Autowired
    TransferPublisher transferPublisher;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void 입금_메시지_발행() {
        DepositTask task = new DepositTask(
                UUID.randomUUID(),
                TEST_ACCOUNT_ID,
                new Money(10_000)
        );

        depositPublisher.publish(task);

        AtomicReference<Object> received = new AtomicReference<>();

        await().atMost(5, SECONDS)
                .until(() -> {
                    Object msg = rabbitTemplate.receiveAndConvert(
                            DepositRabbitMQConfig.DEPOSIT_QUEUE
                    );
                    if (msg != null) {
                        received.set(msg);
                        return true;
                    }
                    return false;
                });

        assertThat(received.get())
                .isInstanceOf(DepositMessage.class);
    }

    @Test
    void 출금_메시지_발행() {
        WithdrawalTask task = new WithdrawalTask(
                UUID.randomUUID(),
                TEST_ACCOUNT_ID,
                new Money(10_000)
        );

        withdrawalPublisher.publish(task);

        AtomicReference<Object> received = new AtomicReference<>();

        await().atMost(5, SECONDS)
                .until(() -> {
                    Object msg = rabbitTemplate.receiveAndConvert(
                            WithdrawalRabbitMQConfig.WITHDRAWAL_QUEUE
                    );
                    if (msg != null) {
                        received.set(msg);
                        return true;
                    }
                    return false;
                });

        assertThat(received.get())
                .isInstanceOf(WithdrawalMessage.class);
    }

    @Test
    void 이체_메시지_발행() {
        TransferTask task = new TransferTask(
                UUID.randomUUID(),
                TEST_ACCOUNT_ID,
                OTHER_ACCOUNT_ID,
                new Money(10_000)
        );

        transferPublisher.publish(task);

        AtomicReference<Object> received = new AtomicReference<>();

        await().atMost(5, SECONDS)
                .until(() -> {
                    Object msg = rabbitTemplate.receiveAndConvert(
                            TransferRabbitMQConfig.TRANSFER_QUEUE
                    );
                    if (msg != null) {
                        received.set(msg);
                        return true;
                    }
                    return false;
                });

        assertThat(received.get())
                .isInstanceOf(TransferMessage.class);
    }
}
