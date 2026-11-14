package com.banklite.api.customer;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.banklite.api.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles ("test")
public class CustomerRepositoryTest {

    @Autowired
    CustomerRepository repository;

    @Test
    public void createCustomer () {
        Customer createdCustomer = saveCustomer();
        verifyCustomer(createdCustomer);

        // Find customer
        Optional<Customer> customerOptional = repository.findById(createdCustomer.getId());
        verifyCustomer(customerOptional.get());
    }

    @Test
    public void existsByEmail() {
        Customer createdCustomer = saveCustomer();

        boolean exists = repository.existsByEmail(createdCustomer.getEmail());
        assertThat(exists).isTrue();

    }

    private Customer saveCustomer() {
        Customer c = new Customer();
        c.setFirstName(CUSTOMER_FIRST_NAME);
        c.setLastName(CUSTOMER_LAST_NAME);
        c.setEmail(CUSTOMER_EMAIL);
        return repository.save(c);
    }

    private void verifyCustomer(Customer customer) {
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo(CUSTOMER_FIRST_NAME);
        assertThat(customer.getLastName()).isEqualTo(CUSTOMER_LAST_NAME);
        assertThat(customer.getCreatedAt()).isNotNull();
    }
}
