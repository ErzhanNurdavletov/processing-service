package kg.bakaibank.processingservice.controller;

import jakarta.validation.Valid;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountBalanceResponse;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.service.api.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse response = accountService.createAccount(request);
        log.info("POST /api/v1/accounts - createAccount response={}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable UUID accountId) {
        AccountBalanceResponse response = accountService.getBalance(accountId);
        log.info("GET /api/v1/accounts/{accountId}/balance - getBalance response={}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
