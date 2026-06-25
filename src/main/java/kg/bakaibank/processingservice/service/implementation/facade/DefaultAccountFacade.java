package kg.bakaibank.processingservice.service.implementation.facade;

import kg.bakaibank.processingservice.exception.custom.AccountNotFoundException;
import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.payload.response.TransactionShortResponse;
import kg.bakaibank.processingservice.service.api.facade.AccountFacade;
import kg.bakaibank.processingservice.service.api.service.AccountService;
import kg.bakaibank.processingservice.service.api.service.PaymentService;
import kg.bakaibank.processingservice.service.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultAccountFacade implements AccountFacade {

    private final PaymentService paymentService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @Override
    public Page<PaymentShortResponse> getPayments(UUID accountId,
                                                  Pageable pageable,
                                                  PaymentAccountType type,
                                                  OffsetDateTime from,
                                                  OffsetDateTime to) {
        if (!accountService.existsById(accountId)) {
            throw new AccountNotFoundException("account with id: " + accountId + " not found");
        }
        return paymentService.getPayments(accountId, pageable, type, from, to);
    }

    @Override
    public Page<TransactionShortResponse> getTransactions(UUID accountId,
                                                      Pageable pageable,
                                                      OffsetDateTime from,
                                                      OffsetDateTime to) {
        if (!accountService.existsById(accountId)) {
            throw new AccountNotFoundException("account with id: " + accountId + " not found");
        }
        return transactionService.getTransactions(accountId, pageable, from, to);
    }
}
