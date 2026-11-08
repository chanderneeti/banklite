package com.banklite.api.account;

import com.banklite.api.dto.AccountResponse;
import com.banklite.api.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
}
