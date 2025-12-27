package site.donghyeon.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import site.donghyeon.bank.common.domain.Money;

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
    void 음수_전환_테스트() {
        Money money = new Money(1000);
        Money minus = money.toMinus();

        assertThat(minus.amount()).isEqualTo(-1000);
    }

    @Test
    void 초과_여부_테스트() {
        Money money1 = new Money(1);
        Money money2 = new Money(2);
        Money money3 = new Money(2);

        assertThat(money1.exceeded(money2)).isFalse();
        assertThat(money2.exceeded(money3)).isFalse();
        assertThat(money3.exceeded(money1)).isTrue();
    }

    @Test
    void 수수료_테스트() {
        Money money = new Money(1000);
        Money fee = money.getFee();
        Money withFee = money.withFee();

        assertThat(fee.amount()).isEqualTo(10);
        assertThat(withFee.amount()).isEqualTo(1010);

        Money small = new Money(1);
        Money smallFee = small.getFee();
        Money smallWithFee = small.withFee();

        assertThat(smallFee.amount()).isEqualTo(0);
        assertThat(smallWithFee.amount()).isEqualTo(1);
    }
}
