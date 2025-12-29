package site.donghyeon.bank.infrastructure.cache.redis.accountLimit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.infrastructure.cache.redis.common.CacheError;
import site.donghyeon.bank.infrastructure.cache.redis.common.CacheHit;
import site.donghyeon.bank.infrastructure.cache.redis.common.CacheMiss;
import site.donghyeon.bank.infrastructure.cache.redis.common.CacheResult;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public abstract class AbstractOperationLimitCache {
    private static final Duration TTL = Duration.ofDays(1);

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> tryConsumeLimitScript;

    public AbstractOperationLimitCache(
            StringRedisTemplate stringRedisTemplate,
            RedisScript<Long> tryConsumeLimitScript
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.tryConsumeLimitScript = tryConsumeLimitScript;
    }

    protected abstract String keyOf(UUID accountId);

    // 다시 직접 해
    public Money checkLimit(UUID accountId) {
        try {
            String value = stringRedisTemplate.opsForValue().get(keyOf(accountId));
            return value == null
                    ? new CacheMiss<>()
                    : new CacheHit<>(new Money(Long.parseLong(value)));
        } catch (Exception e) {
            return new CacheError<>();
        }
    }

    // true: 가능
    // false: 한도 초과
    public CacheResult<Boolean> tryConsume(UUID accountId, Money amount, Money limit) {
        String key = keyOf(accountId);
        try {
            Long result = stringRedisTemplate.execute(
                    tryConsumeLimitScript,
                    List.of(key),
                    String.valueOf(amount.amount()),
                    String.valueOf(limit.amount()),
                    String.valueOf(TTL.toSeconds())
            );
            if (result.equals(1L)) {
                return new CacheHit<>(true);
            }
            return new CacheHit<>(false);
        } catch (Exception e) {
            return new CacheError<>();
        }
    }

    public void rollback(UUID accountId, Money amount) {
        stringRedisTemplate.opsForValue().decrement(keyOf(accountId), amount.amount());
    }
}
