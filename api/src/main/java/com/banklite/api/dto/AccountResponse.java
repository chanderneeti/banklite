package com.banklite.api.dto;

import com.banklite.api.account.Account;
import com.banklite.api.account.AccountStatus;
import com.banklite.api.account.AccountType;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        AccountType type,
        BigDecimal balance,
        AccountStatus status,
        Long customerId
) {
    public static AccountResponse from(Account a) {
        return new AccountResponse(a.getId(), a.getAccountNumber(), a.getType(),
                a.getBalance(), a.getStatus(), a.getCustomer().getId());
    }
}
