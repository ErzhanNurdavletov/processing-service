package kg.bakaibank.processingservice.service.api;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountBalanceResponse;
import kg.bakaibank.processingservice.payload.response.AccountResponse;

import java.util.UUID;

public interface AccountService {
    Account findById(UUID accountId);
    Account findByIdForUpdate(UUID accountId);
    Account getBankTransitAccount();
    AccountResponse createAccount(AccountCreateRequest request);
    AccountBalanceResponse getBalance(UUID accountId);
}
