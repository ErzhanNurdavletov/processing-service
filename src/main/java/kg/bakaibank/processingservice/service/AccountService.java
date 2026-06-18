package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.exception.AccountNotFoundException;
import kg.bakaibank.processingservice.mapper.AccountMapper;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public Account findById(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account with id: "
                + accountId + " not found"));
    }

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        Account account = accountMapper.toEntity(request);
        accountRepository.save(account);
        return accountMapper.toResponse(account);
    }
}
