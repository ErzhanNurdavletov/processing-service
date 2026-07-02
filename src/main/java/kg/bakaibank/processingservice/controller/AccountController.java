package kg.bakaibank.processingservice.controller;

import jakarta.validation.Valid;
import kg.bakaibank.processingservice.controller.api.AccountControllerApi;
import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.*;
import kg.bakaibank.processingservice.service.api.facade.AccountFacade;
import kg.bakaibank.processingservice.service.api.service.AccountService;
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
public class AccountController implements AccountControllerApi {
    private final AccountService accountService;
    private final AccountFacade accountFacade;

    @PostMapping
    @Override
    public ResponseEntity<AccountShortResponse> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        AccountShortResponse response = accountService.createAccount(request);
        log.info("POST /api/v1/accounts - createAccount response={}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}/balance")
    @Override
    public ResponseEntity<AccountBalanceResponse> getBalance(@PathVariable UUID accountId) {
        AccountBalanceResponse response = accountService.getBalance(accountId);
        log.info("GET /api/v1/accounts/{accountId}/balance - getBalance response={}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{accountId}/payments-page")
    @Override
    public ResponseEntity<Page<PaymentShortResponse>> getPaymentsPage(@PathVariable UUID accountId,
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
    @Override
    public ResponseEntity<Page<TransactionShortResponse>> getTransactionsPage(@PathVariable UUID accountId,
                                             @PageableDefault(sort = "createdAt",
                                                 direction = Sort.Direction.ASC) Pageable pageable,
                                             @RequestParam OffsetDateTime from,
                                             @RequestParam OffsetDateTime to) {
        Page<TransactionShortResponse> response =
            accountFacade.getTransactions(accountId, pageable, from, to);
        log.info("GET /api/v1/accounts/{}/transactions-page - getTransactionsPage response={}",accountId, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{accountId}/exists")
    @Override
    public ResponseEntity<AccountExistsResponse> doesAccountExist(@PathVariable UUID accountId) {
        AccountExistsResponse response = accountService.existsByIdToResponse(accountId);
        log.info("GET /api/v1/accounts/{}/exists - doesAccountExist response={}",accountId, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{accountId}")
    @Override
    public ResponseEntity<AccountResponse> findById(@PathVariable UUID accountId) {
        AccountResponse response = accountService.findByIdToResponse(accountId);
        log.info("GET /api/v1/accounts/{} - findById response={}",accountId, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
