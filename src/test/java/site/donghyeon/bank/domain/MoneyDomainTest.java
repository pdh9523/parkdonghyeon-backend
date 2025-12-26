package site.donghyeon.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.common.exception.NegativeAmountException;

class MoneyDomainTest {

    @Test
    void 금액_생성_테스트() {
        Money money = new Money(1000L);

        assertThat(money.amount()).isEqualTo(1000L);
    }

    @Test
    void 제로_생성_테스트() {
        Money money = Money.zero();

        assertThat(money.amount()).isZero();
    }

    @Test
    void 금액_덧셈_테스트() {
        Money money = new Money(1000L);

        Money result = money.add(new Money(1000L));

        assertThat(result.amount()).isEqualTo(2000L);
    }

    @Test
    void 금액_뺄셈_테스트() {
        Money money = new Money(1000L);

        Money result = money.subtract(new Money(100L));

        assertThat(result.amount()).isEqualTo(900L);
    }

    @Test
    void 금액은_0미만이_될_수_없다() {
        assertThatThrownBy(() -> new Money(-1L))
                .isInstanceOf(NegativeAmountException.class);
    }
}
