package com.banklite.api.common;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException (String accountNumber) {
        super("Insufficient funds for account number: " + accountNumber);
    }
}
