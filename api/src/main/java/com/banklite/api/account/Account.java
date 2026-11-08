package com.banklite.api.account;

import com.banklite.api.common.InsufficientFundsException;
import com.banklite.api.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table (name = "accounts")
@Getter @Setter @NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column (name = "account_type", nullable = false, length = 10)
    private AccountType type;

    @Column (name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Version
    private Long version;

    @Column (name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "customer_id")
    private Customer customer;

    public void debit (BigDecimal amount) {
        if (balance.compareTo(amount) < 0) throw new InsufficientFundsException(accountNumber);
        balance = balance.subtract(amount);
    }

    public void credit (BigDecimal amount) {
        balance = balance.add(amount);
    }
}
