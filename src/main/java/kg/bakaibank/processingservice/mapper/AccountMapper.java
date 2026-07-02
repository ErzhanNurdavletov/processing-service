package kg.bakaibank.processingservice.mapper;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountBalanceResponse;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.payload.response.AccountShortResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountCreateRequest request) {
        if (request == null) {
            return null;
        }
        return Account.builder()
            .clientId(request.clientId())
            .build();
    }

    public AccountShortResponse toShortResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountShortResponse(account.getId(),
            account.getAccountNumber());
    }

    public AccountBalanceResponse toBalanceResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountBalanceResponse(
            account.getId(),
            account.getBalance());
    }

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountResponse(
            account.getId(),
            account.getClientId(),
            account.getAccountNumber(),
            account.getBalance(),
            account.getOpenedAt(),
            account.getClosedAt(),
            account.getEndedAt());
    }
}
