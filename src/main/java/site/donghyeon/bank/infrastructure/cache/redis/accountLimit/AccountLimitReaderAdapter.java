package site.donghyeon.bank.infrastructure.cache.redis.accountLimit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import site.donghyeon.bank.application.account.limit.AccountLimitReader;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.accountTransaction.enums.LimitType;
import site.donghyeon.bank.infrastructure.jpa.accountTransaction.adapter.AccountLimitJpaAdapter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class AccountLimitReaderAdapter implements AccountLimitReader {
    private static final Duration TTL = Duration.ofDays(1);

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> tryConsumeLimitScript;
    private final AccountLimitJpaAdapter limitJpaAdapter;

    public AccountLimitReaderAdapter(
            StringRedisTemplate stringRedisTemplate,
            RedisScript<Long> tryConsumeLimitScript,
            AccountLimitJpaAdapter limitJpaAdapter
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.tryConsumeLimitScript = tryConsumeLimitScript;
        this.limitJpaAdapter = limitJpaAdapter;
    }

    public Money checkWithdrawalLimit(UUID accountId) {
        return checkLimit(accountId, LimitType.WITHDRAWAL);
    }

    public Money checkTransferLimit(UUID accountId) {
        return checkLimit(accountId, LimitType.TRANSFER);
    }

    public boolean tryConsumeTransfer(UUID accountId, Money amount, Money limit) {
        return tryConsume(accountId, amount, limit, LimitType.TRANSFER);
    }

    public boolean tryConsumeWithdrawal(UUID accountId, Money amount, Money limit) {
        return tryConsume(accountId, amount, limit, LimitType.WITHDRAWAL);
    }

    public void rollbackTransferLimit(UUID accountId, Money amount) {
        rollback(accountId, amount, LimitType.TRANSFER);
    }

    public void rollbackWithdrawalLimit(UUID accountId, Money amount) {
        rollback(accountId, amount, LimitType.WITHDRAWAL);
    }


    private String keyOf(UUID accountId, LimitType type) {
        return "limit:%s:daily:%s:%s"
                .formatted(type.toString().toLowerCase(), accountId, LocalDate.now());
    };

    //TODO: 장애 시 서킷브레이커 패턴 적용 가능성
    public Money checkLimit(UUID accountId, LimitType type) {
        String key = keyOf(accountId, type);
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            return new Money(Long.parseLong(value));
        }

        Money limitFromDB = limitJpaAdapter.readDailyLimit(accountId, type);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(limitFromDB), TTL);

        return limitFromDB;
    }

    // true: 가능
    // false: 한도 초과
    private boolean tryConsume(UUID accountId, Money amount, Money limit, LimitType type) {
        String key = keyOf(accountId, type);
        Long result = stringRedisTemplate.execute(
                tryConsumeLimitScript,
                List.of(key),
                String.valueOf(amount.amount()),
                String.valueOf(limit.amount()),
                String.valueOf(TTL.toSeconds())
            );

        return result != null && result == 1;
    }

    private void rollback(UUID accountId, Money amount, LimitType type) {
        stringRedisTemplate.opsForValue().decrement(keyOf(accountId, type), amount.amount());
    }
}
