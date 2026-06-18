package kg.bakaibank.processingservice.mapper;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountCreateRequest request) {
        if (request == null) {
            return null;
        }
        return Account.builder()
            .clientId(request.clientId())
            .accountNumber(request.accountNumber())
            .build();
    }

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountResponse(account.getId());
    }
}
