package com.banklite.api.account;

import com.banklite.api.common.NotFoundException;
import com.banklite.api.customer.Customer;
import com.banklite.api.customer.CustomerRepository;
import com.banklite.api.dto.AccountResponse;
import com.banklite.api.dto.CreateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accounts;
    private final CustomerRepository customers;

    @Transactional
    public AccountResponse open(CreateAccountRequest req) {
        Customer c = customers.findById(req.customerId())
                .orElseThrow(() -> new NotFoundException("Customer " + req.customerId() + " not found"));
        Account a = new Account();
        a.setCustomer(c);
        a.setType(req.type());
        a.setBalance(req.openingBalance());
        a.setAccountNumber(generateAccountNumber());

        return AccountResponse.from(accounts.save(a));
    }

    public AccountResponse get(Long id) {
        Account account = accounts.findById(id)
                .orElseThrow(() -> new NotFoundException("Account " + id + " not found"));
        return AccountResponse.from(account);
    }

    public List<AccountResponse> forCustomer(Long customerId) {
        Customer c = customers.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
        List<Account> accounts = c.getAccounts();
        return accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return "BL" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
    }
}
