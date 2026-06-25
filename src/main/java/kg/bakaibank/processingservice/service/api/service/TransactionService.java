package kg.bakaibank.processingservice.service.api.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.TransactionShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface TransactionService {
    Transaction initTransaction(PaymentRequest request,
                                Account debitAccount,
                                Account creditAccount,
                                Payment payment);
    void closeTransaction(Transaction transaction);
    Page<TransactionShortResponse> getTransactions(UUID accountId,
                                                   Pageable pageable,
                                                   OffsetDateTime from,
                                                   OffsetDateTime to);
}
