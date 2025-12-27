package site.donghyeon.bank.common.domain;

public record Money(long amount) {

    public static Money zero() {
        return new Money(0);
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        return new Money(this.amount - other.amount);
    }

    public boolean exceeded(Money other) {
        return this.amount > other.amount;
    }

    public Money toMinus() {
        return new Money(-this.amount);
    }

    public Money getFee() {
        return new Money((long) (this.amount * 0.01));
    }

    public Money withFee() {
        return new Money((long) (this.amount * 1.01));
    }
}

