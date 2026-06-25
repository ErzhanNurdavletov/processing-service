package kg.bakaibank.processingservice.controller;

import jakarta.validation.Valid;
import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountBalanceResponse;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.payload.response.TransactionShortResponse;
import kg.bakaibank.processingservice.service.api.facade.AccountFacade;
import kg.bakaibank.processingservice.service.api.service.AccountService;
import kg.bakaibank.processingservice.service.api.service.PaymentService;
import kg.bakaibank.processingservice.service.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountFacade accountFacade;

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

    @GetMapping("/{accountId}/payments-page")
    public ResponseEntity<?> getPaymentsPage(@PathVariable UUID accountId,
                                      @PageableDefault(sort = "createdAt",
                                          direction = Sort.Direction.ASC) Pageable pageable,
                                      @RequestParam(required = false) PaymentAccountType accountType,
                                      @RequestParam OffsetDateTime from,
                                      @RequestParam OffsetDateTime to) {
        Page<PaymentShortResponse> response =
            accountFacade.getPayments(accountId, pageable, accountType, from, to);
        log.info("GET /api/v1/accounts/{}/payments-page - getPaymentsPage response={}",accountId, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{accountId}/transactions-page")
    public ResponseEntity<?> getTransactionsPage(@PathVariable UUID accountId,
                                             @PageableDefault(sort = "createdAt",
                                                 direction = Sort.Direction.ASC) Pageable pageable,
                                             @RequestParam OffsetDateTime from,
                                             @RequestParam OffsetDateTime to) {
        Page<TransactionShortResponse> response =
            accountFacade.getTransactions(accountId, pageable, from, to);
        log.info("GET /api/v1/accounts/{}/transactions-page - getTransactionsPage response={}",accountId, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
