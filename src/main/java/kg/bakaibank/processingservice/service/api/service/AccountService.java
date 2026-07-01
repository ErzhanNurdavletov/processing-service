package kg.bakaibank.processingservice.service.api.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountBalanceResponse;
import kg.bakaibank.processingservice.payload.response.AccountExistsResponse;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.payload.response.AccountShortResponse;

import java.util.UUID;

public interface AccountService {
    Account findById(UUID accountId);
    Account findByIdForUpdate(UUID accountId);
    Account getBankTransitAccount();
    AccountShortResponse createAccount(AccountCreateRequest request);
    AccountBalanceResponse getBalance(UUID accountId);
    boolean existsById(UUID accountId);
    AccountExistsResponse existsByIdToResponse(UUID accountId);
    AccountResponse findByIdToResponse(UUID accountId);
}
