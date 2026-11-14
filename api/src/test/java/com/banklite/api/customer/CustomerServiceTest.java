package com.banklite.api.customer;

import com.banklite.api.common.DuplicateResourceException;
import com.banklite.api.dto.CreateCustomerRequest;
import com.banklite.api.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static com.banklite.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository repository;

    @InjectMocks
    CustomerService service;

    @Test
    public void get() {
        Customer mockCustomer = createMockCustomer();
        when(repository.findById(1L)).thenReturn(Optional.of(mockCustomer));

        CustomerResponse customerResponse = service.get(1L);
        assertEquals(mockCustomer.getFirstName(), customerResponse.firstName());
        assertEquals(mockCustomer.getLastName(), customerResponse.lastName());
        assertEquals(mockCustomer.getEmail(), customerResponse.email());
        assertEquals(mockCustomer.getCreatedAt(), customerResponse.createdAt());
    }

    @Test
    public void create() {
        Customer mockCustomer = createMockCustomer();
        when(repository.existsByEmail(CUSTOMER_EMAIL)).thenReturn(false);
        when(repository.save(any())).thenReturn(mockCustomer);

        CreateCustomerRequest req = new CreateCustomerRequest(CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_EMAIL);
        CustomerResponse customerResponse = service.create(req);

        assertEquals(mockCustomer.getFirstName(), customerResponse.firstName());
        assertEquals(mockCustomer.getLastName(), customerResponse.lastName());
        assertEquals(mockCustomer.getEmail(), customerResponse.email());
        assertEquals(mockCustomer.getCreatedAt(), customerResponse.createdAt());
        verify(repository).save(any(Customer.class));
    }

    @Test
    public void createWithDuplicate() {
        Customer mockCustomer = createMockCustomer();
        when(repository.existsByEmail(CUSTOMER_EMAIL)).thenReturn(true);

        CreateCustomerRequest req = new CreateCustomerRequest(CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_EMAIL);
        DuplicateResourceException ex =
                assertThrows(DuplicateResourceException.class, () -> service.create(req));

        assertTrue(ex.getMessage().contains(CUSTOMER_EMAIL));
        verify(repository, never()).save(any());

    }

    private Customer createMockCustomer() {
        Customer c = new Customer();
        c.setId(1L);
        c.setFirstName(CUSTOMER_FIRST_NAME);
        c.setLastName(CUSTOMER_LAST_NAME);
        c.setEmail(CUSTOMER_EMAIL);
        c.setCreatedAt(Instant.now());
        return c;
    }
}
