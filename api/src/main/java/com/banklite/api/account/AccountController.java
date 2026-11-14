package com.banklite.api.account;

import com.banklite.api.dto.AccountResponse;
import com.banklite.api.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping ("/accounts")
    public ResponseEntity<AccountResponse> open(@Valid @RequestBody CreateAccountRequest req)  {
        AccountResponse accountResponse = accountService.open(req);
        return ResponseEntity.created(URI.create("/api/accounts/" +accountResponse.id())).body(accountResponse);
    }

    @GetMapping ("/accounts/{id}")
    public AccountResponse get (@PathVariable Long id) {
        return accountService.get(id);
    }

    @GetMapping ("/accounts")
    public List<AccountResponse> getByCustomerId (@RequestParam Long customerId) {
        return accountService.forCustomer(customerId);
    }
}
