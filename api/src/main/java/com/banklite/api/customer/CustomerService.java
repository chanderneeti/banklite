package com.banklite.api.customer;

import com.banklite.api.common.DuplicateResourceException;
import com.banklite.api.common.NotFoundException;
import com.banklite.api.dto.CreateCustomerRequest;
import com.banklite.api.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customers;

    public CustomerResponse get(Long id) {
        Customer c = customers.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer " + id + " not found"));
        return CustomerResponse.from(c);
    }

    public CustomerResponse create (CreateCustomerRequest req) {
        if (customers.existsByEmail(req.email())) {
            throw new DuplicateResourceException("Customer with " + req.email() + " already exists");
        }
        Customer c = new Customer();
        c.setFirstName(req.firstName());
        c.setLastName(req.lastName());
        c.setEmail(req.email());

        return CustomerResponse.from(customers.save(c));

    }

}
