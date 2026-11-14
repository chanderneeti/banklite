package com.banklite.api.customer;

import com.banklite.api.dto.AccountResponse;
import com.banklite.api.dto.CreateCustomerRequest;
import com.banklite.api.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping ("/api")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @GetMapping ("/customers/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping ("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest req) {
        CustomerResponse customerResponse = service.create(req);
        return ResponseEntity.created(URI.create("/api/customers/" + customerResponse.id())).body(customerResponse);
    }
}
