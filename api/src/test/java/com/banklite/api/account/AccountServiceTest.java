package com.banklite.api.account;

import com.banklite.api.common.NotFoundException;
import com.banklite.api.customer.Customer;
import com.banklite.api.customer.CustomerRepository;
import com.banklite.api.dto.AccountResponse;
import com.banklite.api.dto.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.banklite.api.Constants.ACCOUNT_BALANCE;
import static com.banklite.api.Constants.ACCOUNT_NUMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountRepository accountRepository;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    AccountService service;

    @Test
    void openAccount() {
        Account mockAccount = createMockAccount();
        when(customerRepository.findById(mockAccount.getCustomer().getId()))
                .thenReturn(Optional.of(mockAccount.getCustomer()));
        when(accountRepository.save(any())).thenReturn(mockAccount);

        CreateAccountRequest req = new CreateAccountRequest(mockAccount.getCustomer().getId(), AccountType.CHECKING, ACCOUNT_BALANCE);
        AccountResponse accountResponse = service.open(req);

        assertEquals(mockAccount.getId(), accountResponse.id());
        assertEquals(mockAccount.getAccountNumber(), accountResponse.accountNumber());
        assertEquals(mockAccount.getType(), accountResponse.type());
        assertEquals(mockAccount.getBalance(), accountResponse.balance());
        assertEquals(mockAccount.getStatus(), accountResponse.status());
        assertEquals(mockAccount.getCustomer().getId(), accountResponse.customerId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void openAccountWithMissingCustomer() {
        Account mockAccount = createMockAccount();
        when(customerRepository.findById(mockAccount.getCustomer().getId()))
                .thenThrow(new NotFoundException("Account with id " + mockAccount.getId() + " not found."));

        CreateAccountRequest req = new CreateAccountRequest(mockAccount.getCustomer().getId(), AccountType.CHECKING, ACCOUNT_BALANCE);
        NotFoundException ex =
                assertThrows(NotFoundException.class, () -> service.open(req) );


        assertTrue(ex.getMessage().contains("Account with id " + mockAccount.getId()));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void get() {
        Account mockAccount = createMockAccount();
        when(accountRepository.findById(mockAccount.getId()))
                .thenReturn(Optional.of(mockAccount));

        AccountResponse accountResponse = service.get(mockAccount.getId());
        assertEquals(mockAccount.getId(), accountResponse.id());
        assertEquals(mockAccount.getAccountNumber(), accountResponse.accountNumber());
        assertEquals(mockAccount.getType(), accountResponse.type());
        assertEquals(mockAccount.getBalance(), accountResponse.balance());
        assertEquals(mockAccount.getStatus(), accountResponse.status());
        assertEquals(mockAccount.getCustomer().getId(), accountResponse.customerId());
    }

    @Test
    void getWithNotFoundError() {
        Account mockAccount = createMockAccount();
        when(accountRepository.findById(mockAccount.getId()))
                .thenThrow(new NotFoundException("Account with id " + mockAccount.getId() + " not found."));

        NotFoundException ex =
                assertThrows(NotFoundException.class, () -> service.get(mockAccount.getId()));
        assertTrue(ex.getMessage().contains("Account with id " + mockAccount.getId()));
    }

    @Test
    void listAccountsForCustomer() {
        Account mockAccount = createMockAccount();
        Account mockAnotherAccount = createAnotherMockAccount();
        List<Account> accounts = List.of(mockAccount, mockAnotherAccount);

        Customer mockCustomer = new Customer();
        mockCustomer.setId(mockAccount.getCustomer().getId());
        mockCustomer.setAccounts(accounts);
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(mockCustomer));

        List<AccountResponse> accountResponseList = service.forCustomer(mockAccount.getCustomer().getId());
        assertTrue(accountResponseList.size() == 2);

        assertEquals(mockAccount.getId(), accountResponseList.get(0).id());
        assertEquals(mockAccount.getAccountNumber(), accountResponseList.get(0).accountNumber());
        assertEquals(mockAccount.getType(), accountResponseList.get(0).type());
        assertEquals(mockAccount.getBalance(), accountResponseList.get(0).balance());
        assertEquals(mockAccount.getStatus(), accountResponseList.get(0).status());
        assertEquals(mockAccount.getCustomer().getId(), accountResponseList.get(0).customerId());

        assertEquals(mockAnotherAccount.getId(), accountResponseList.get(1).id());
        assertEquals(mockAnotherAccount.getAccountNumber(), accountResponseList.get(1).accountNumber());
        assertEquals(mockAnotherAccount.getType(), accountResponseList.get(1).type());
        assertEquals(mockAnotherAccount.getBalance(), accountResponseList.get(1).balance());
        assertEquals(mockAnotherAccount.getStatus(), accountResponseList.get(1).status());
        assertEquals(mockAnotherAccount.getCustomer().getId(), accountResponseList.get(1).customerId());

    }

    @Test
    void listAccountsForCustomerNotExists() {
        Account mockAccount = createMockAccount();

        when(customerRepository.findById(1L))
                .thenThrow(new NotFoundException("Customer does not exist"));

        NotFoundException ex =
                assertThrows(NotFoundException.class, () -> service.forCustomer(mockAccount.getCustomer().getId()));
        assertTrue(ex.getMessage().contains("Customer does not exist"));
    }

    @Test
    void listEmptyAccountsForCustomer() {
        Account mockAccount = createMockAccount();

        Customer mockCustomer = new Customer();
        mockCustomer.setId(mockAccount.getCustomer().getId());
        mockCustomer.setAccounts(new ArrayList<Account>());
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(mockCustomer));

        List<AccountResponse> accountResponseList = service.forCustomer(mockAccount.getCustomer().getId());
        assertTrue(accountResponseList.size() == 0);

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
