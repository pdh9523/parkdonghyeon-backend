package site.donghyeon.bank.domain.accountTransaction.enums;

public enum LimitType {
    WITHDRAWAL(TransactionType.WITHDRAW),
    TRANSFER(TransactionType.TRANSFER);

    private final TransactionType transactionType;

    LimitType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
}
