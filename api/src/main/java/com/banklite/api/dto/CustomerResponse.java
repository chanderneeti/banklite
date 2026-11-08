package com.banklite.api.dto;

import com.banklite.api.customer.Customer;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Instant createdAt
) {
    public static CustomerResponse from (Customer c) {
        return new CustomerResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getCreatedAt());
    }
}
