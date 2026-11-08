package com.banklite.api.customer;

import com.banklite.api.account.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table (name = "customers")
@Getter @Setter @NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column (name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column (name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column (name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany (mappedBy = "customer")
    private List<Account> accounts;
}
