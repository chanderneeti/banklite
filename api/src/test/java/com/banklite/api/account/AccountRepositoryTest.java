package com.banklite.api.account;

import com.banklite.api.customer.Customer;
import com.banklite.api.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.banklite.api.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles ("test")
public class AccountRepositoryTest {


    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void createAccount() {
        Account createdAccount = saveAccountWithCustomer();
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getId()).isNotNull();
        assertThat(createdAccount.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        assertThat(createdAccount.getBalance()).isEqualTo(ACCOUNT_BALANCE);
        assertThat(createdAccount.getCustomer()).isNotNull();

        // Retrieve and verify account
        Account retrieveAccount = accountRepository.findById(createdAccount.getId()).get();
        verifyRetrievedAccount(retrieveAccount);

    }

    @Test
    public void findByCustomerId() {
        Account createdAccount = saveAccountWithCustomer();
        List<Account> retrievedAccounts = accountRepository.findByCustomerId(createdAccount.getCustomer().getId());
        verifyRetrievedAccount(retrievedAccounts.get(0));
    }

    @Test
    public void findByAccountNumber() {
        Account createdAccount = saveAccountWithCustomer();
        Account retrievedAccount = accountRepository.findByAccountNumber(createdAccount.getAccountNumber()).get();
        verifyRetrievedAccount(retrievedAccount);
    }

    private Account saveAccountWithCustomer() {
        // save a new customer
        Customer c = new Customer();
        c.setFirstName(CUSTOMER_FIRST_NAME);
        c.setLastName(CUSTOMER_LAST_NAME);
        c.setEmail(CUSTOMER_EMAIL);
        Customer createdCustomer = customerRepository.save(c);
        // create account for customer
        Account a = new Account();
        a.setCustomer(createdCustomer);
        a.setAccountNumber(ACCOUNT_NUMBER);
        a.setBalance(ACCOUNT_BALANCE);
        a.setType(AccountType.CHECKING);
        return accountRepository.save(a);
    }

    private void verifyRetrievedAccount(Account retrieveAccount) {
        assertThat(retrieveAccount).isNotNull();
        assertThat(retrieveAccount.getId()).isNotNull();
        assertThat(retrieveAccount.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        assertThat(retrieveAccount.getBalance()).isEqualTo(ACCOUNT_BALANCE);
        assertThat(retrieveAccount.getCreatedAt()).isNotNull();

        assertThat(retrieveAccount.getCustomer()).isNotNull();
        assertThat(retrieveAccount.getCustomer().getId()).isNotNull();
        assertThat(retrieveAccount.getCustomer().getFirstName()).isEqualTo(CUSTOMER_FIRST_NAME);
        assertThat(retrieveAccount.getCustomer().getLastName()).isEqualTo(CUSTOMER_LAST_NAME);
        assertThat(retrieveAccount.getCustomer().getEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(retrieveAccount.getCustomer().getCreatedAt()).isNotNull();
    }

}
