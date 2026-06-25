package kg.bakaibank.processingservice.service.implementation.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.entity.enums.TransactionStatus;
import kg.bakaibank.processingservice.mapper.TransactionMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.TransactionShortResponse;
import kg.bakaibank.processingservice.repository.TransactionRepository;
import kg.bakaibank.processingservice.service.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction initTransaction(PaymentRequest request,
                                       Account debitAccount,
                                       Account creditAccount,
                                       Payment payment) {
        return Transaction.builder()
            .payment(payment)
            .debitAccount(debitAccount)
            .creditAccount(creditAccount)
            .createdAt(OffsetDateTime.now())
            .amount(request.amount())
            .currency(request.currency())
            .status(TransactionStatus.NEW)
            .comment(request.comment())
            .build();
    }

    @Override
    public void closeTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

    @Override
    public Page<TransactionShortResponse> getTransactions(UUID accountId,
                                                          Pageable pageable,
                                                          OffsetDateTime from,
                                                          OffsetDateTime to) {
        Page<Transaction> transactionsPage =
            transactionRepository.getTransactionsPage(accountId, pageable, from, to);

        return transactionsPage.map(transactionMapper::toShortResponse);
    }
}
