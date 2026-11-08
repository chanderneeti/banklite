package com.banklite.api.dto;

import com.banklite.api.account.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotNull Long customerId,
        @NotNull AccountType type,
        @NotNull @PositiveOrZero BigDecimal openingBalance
        ) {}
