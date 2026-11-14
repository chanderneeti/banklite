package com.banklite.api.customer;

import com.banklite.api.common.DuplicateResourceException;
import com.banklite.api.common.NotFoundException;
import com.banklite.api.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static com.banklite.api.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest (CustomerController.class)
public class CustomerControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    CustomerService service;

    @Test
    void createNewCustomerSuccess() throws Exception {
        Customer c = createMockCustomer();
        when(service.create(any()))
                .thenReturn(CustomerResponse.from(c));


        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"firstName":"John","lastName":"Day","email":"johnd@yahoo.com"}
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(c.getId()))
                .andExpect(jsonPath("$.firstName").value(c.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(c.getLastName()))
                .andExpect(jsonPath("$.email").value(c.getEmail()));
    }

    @Test
    void rejectsMissingFirstName () throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"lastName":"Day","email":"johnd@yahoo.com"}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void rejectsMissingLastName () throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"firstName":"John","email":"johnd@yahoo.com"}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.lastName").exists());
    }

    @Test
    void rejectsEmailFormatError () throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"firstName":"John","lastName":"Day","email":"johndyahoo.com"}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void createNewCustomerDuplicateEmailError() throws Exception {
        Customer c = createMockCustomer();
        when(service.create(any()))
                .thenThrow(new DuplicateResourceException("Customer with " + c.getEmail() + " already exists"));


        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"firstName":"John","lastName":"Day","email":"johnd@yahoo.com"}
                    """))
                .andExpect(status().isConflict());
    }

    @Test
    void getCustomerByIdSuccess() throws Exception {
        Customer c = createMockCustomer();
        when(service.get(c.getId()))
                .thenReturn(CustomerResponse.from(c));


        mvc.perform(get("/api/customers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c.getId()))
                .andExpect(jsonPath("$.firstName").value(c.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(c.getLastName()))
                .andExpect(jsonPath("$.email").value(c.getEmail()))
                .andExpect(jsonPath("$.createdAt").value(c.getCreatedAt().toString()));

    }

    @Test
    void getCustomerByIdFailure() throws Exception {
        Customer c = createMockCustomer();
        when(service.get(c.getId()))
                .thenThrow(new NotFoundException("Customer " + c.getId() + " not found"));


        mvc.perform(get("/api/customers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

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
