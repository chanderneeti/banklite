package com.banklite.api.account;

import com.banklite.api.common.NotFoundException;
import com.banklite.api.customer.Customer;
import com.banklite.api.dto.AccountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static com.banklite.api.Constants.ACCOUNT_BALANCE;
import static com.banklite.api.Constants.ACCOUNT_NUMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest (AccountController.class)
public class AccountControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    AccountService service;

    @Test
    void createNewAccountSuccess() throws Exception {
        Account account = createMockAccount();
        when(service.open(any()))
                .thenReturn(AccountResponse.from(account));


        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"customerId":1,"type":"CHECKING","openingBalance":100}
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(100))
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void createNewAccountCustomerDoesNotExist() throws Exception {
        Account account = createMockAccount();
        when(service.open(any()))
                .thenThrow(new NotFoundException("Customer does not exist"));


        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"customerId":1,"type":"CHECKING","openingBalance":100}
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsNegativeOpeningBalance () throws Exception {
       mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
               .content("""
                    {"customerId":1,"type":"CHECKING","openingBalance":-5}
                    """))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors.openingBalance").exists());
    }

    @Test
    void rejectsMissingCustomerId () throws Exception {
        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"type":"CHECKING","openingBalance":100}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerId").exists());
    }

    @Test
    void rejectsMissingAccountType () throws Exception {
        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"customerId":1,"openingBalance":100}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.type").exists());
    }

    @Test
    void getAccountByIdSuccess() throws Exception {
        Account account = createMockAccount();
        when(service.get(account.getId()))
                .thenReturn(AccountResponse.from(account));


        mvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100))
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.type").value(account.getType().toString()))
                .andExpect(jsonPath("$.status").value(account.getStatus().toString()))
            .andExpect(jsonPath("$.customerId").value(account.getCustomer().getId()));

    }

    @Test
    void getAccountByIdNotFound() throws Exception {
        Account account = createMockAccount();
        when(service.get(account.getId()))
                .thenThrow(new NotFoundException("Account with id " + account.getId() + " not found."));


        mvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountByCustomerIdSuccess() throws Exception {
        Account account = createMockAccount();
        Account anotherAccount = createAnotherMockAccount();
        List<AccountResponse> accounts = List.of(AccountResponse.from(account), AccountResponse.from(anotherAccount));
        when(service.forCustomer(account.getCustomer().getId()))
                .thenReturn(accounts);


        mvc.perform(get("/api/accounts")
                        .param("customerId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value(account.getCustomer().getId()))
                .andExpect(jsonPath("$[1].customerId").value(anotherAccount.getCustomer().getId()))
                .andExpect(jsonPath("$[0].id").value(account.getId()))
                .andExpect(jsonPath("$[1].id").value(anotherAccount.getId()));
        verify(service).forCustomer(1L);
    }

    @Test
    void getAccountByCustomerIdFailure() throws Exception {
        Account account = createMockAccount();
        Account anotherAccount = createAnotherMockAccount();
        List<AccountResponse> accounts = List.of(AccountResponse.from(account), AccountResponse.from(anotherAccount));
        when(service.forCustomer(2L))
                .thenThrow(new NotFoundException("Customer 2 not found"));


        mvc.perform(get("/api/accounts")
                        .param("customerId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private Account createMockAccount() {
        Account a = new Account();
        a.setId(1L);
        a.setAccountNumber(ACCOUNT_NUMBER);
        a.setBalance(ACCOUNT_BALANCE);
        a.setType(AccountType.CHECKING);
        a.setCreatedAt(Instant.now());

        Customer customer = new Customer();
        customer.setId(1L);

        a.setCustomer(customer);
        return a;
    }

    private Account createAnotherMockAccount() {
        Account a = new Account();
        a.setId(2L);
        a.setAccountNumber(ACCOUNT_NUMBER);
        a.setBalance(ACCOUNT_BALANCE);
        a.setType(AccountType.CHECKING);
        a.setCreatedAt(Instant.now());

        Customer customer = new Customer();
        customer.setId(1L);

        a.setCustomer(customer);
        return a;
    }
}
