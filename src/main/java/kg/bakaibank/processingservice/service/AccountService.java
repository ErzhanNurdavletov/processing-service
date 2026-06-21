package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.exception.custom.AccountNotFoundException;
import kg.bakaibank.processingservice.mapper.AccountMapper;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final UUID BANK_TRANSIT_ACCOUNT_ID =
        UUID.fromString("fb3cdca1-b5a9-4662-a248-f527d781f364");

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public Account findById(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account with id: "
                + accountId + " not found"));
    }

    @Transactional(readOnly = true)
    public Account getBankTransitAccount() {
        return findById(BANK_TRANSIT_ACCOUNT_ID);
    }

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        Account account = accountMapper.toEntity(request);
        account.setBalance(new BigDecimal(0));
        accountRepository.save(account);
        return accountMapper.toResponse(account);
    }
}
