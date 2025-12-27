package site.donghyeon.bank.domain.accountTransaction;

import site.donghyeon.bank.common.domain.Money;
import site.donghyeon.bank.domain.accountTransaction.enums.TransactionType;

import java.util.UUID;

public class AccountTransaction {
    UUID txId;
    UUID eventId;
    UUID accountId;
    Money amount;
    TransactionType transactionType;

    public AccountTransaction(
            UUID txId, UUID eventId, UUID accountId,
            Money amount, TransactionType transactionType
    ) {
        this.txId = txId;
        this.eventId = eventId;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public static AccountTransaction deposit(
            UUID eventId, UUID accountId, Money amount
    ) {
        return new AccountTransaction(
                UUID.randomUUID(),
                eventId,
                accountId,
                amount,
                TransactionType.DEPOSIT
        );
    }

    public static AccountTransaction withdrawal(
            UUID eventId, UUID accountId, Money amount
    ) {
        return new AccountTransaction(
                UUID.randomUUID(),
                eventId,
                accountId,
                amount.toMinus(),
                TransactionType.WITHDRAW
        );
    }

    public static AccountTransaction transferFrom(
            UUID eventId, UUID accountId, Money amount
    ) {
        return new AccountTransaction(
                UUID.randomUUID(),
                eventId,
                accountId,
                amount.toMinus(),
                TransactionType.TRANSFER
        );
    }

    public static AccountTransaction transferTo(
            UUID eventId, UUID accountId, Money amount
    ) {
        return new AccountTransaction(
                UUID.randomUUID(),
                eventId,
                accountId,
                amount,
                TransactionType.TRANSFER
        );
    }

    public static AccountTransaction fee(
            UUID eventId, UUID accountId, Money amount
    ) {
        return new AccountTransaction(
                UUID.randomUUID(),
                eventId,
                accountId,
                amount.toMinus(),
                TransactionType.FEE
        );
    }

    public UUID getTxId() {
        return this.txId;
    }

    public UUID getAccountId() {
        return this.accountId;
    }

    public UUID getEventId() {
        return this.eventId;
    }

    public Money getAmount() {
        return this.amount;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public void markFailed() {
        this.transactionType = TransactionType.FAIL;
    }
}