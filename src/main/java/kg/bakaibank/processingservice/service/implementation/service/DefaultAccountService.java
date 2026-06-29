package kg.bakaibank.processingservice.service.implementation.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.exception.custom.AccountNotFoundException;
import kg.bakaibank.processingservice.mapper.AccountMapper;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.AccountBalanceResponse;
import kg.bakaibank.processingservice.payload.response.AccountResponse;
import kg.bakaibank.processingservice.repository.AccountRepository;
import kg.bakaibank.processingservice.service.api.service.AccountService;
import kg.bakaibank.processingservice.webclient.ClientWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private static final UUID BANK_TRANSIT_ACCOUNT_ID =
        UUID.fromString("fb3cdca1-b5a9-4662-a248-f527d781f364");

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientWebClient clientWebclient;

    @Override
    public Account findById(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account with id: "
                + accountId + " not found"));
    }

    @Override
    public Account findByIdForUpdate(UUID accountId) {
        return accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account with id: "
                + accountId + " not found"));
    }

    @Override
    public Account getBankTransitAccount() {
        return findByIdForUpdate(BANK_TRANSIT_ACCOUNT_ID);
    }

    @Transactional
    @Override
    public AccountResponse createAccount(AccountCreateRequest request) {
        clientWebclient.checkIfClientByIdExists(request.clientId());
        Account account = accountMapper.toEntity(request);
        account.setOpenedAt(OffsetDateTime.now());
        account.setEndedAt(account.getOpenedAt().plusYears(3));
        account.setBalance(new BigDecimal(0));
        accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountBalanceResponse getBalance(UUID accountId) {
        Account account = findById(accountId);
        return accountMapper.toBalanceResponse(account);
    }

    @Override
    public boolean existsById(UUID accountId) {
        return accountRepository.existsById(accountId);
    }
}
