package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.entity.enums.TransactionStatus;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Transaction initTransaction(PaymentRequest request,
                                       Account debitAccount,
                                       Account creditAccount) {
        return Transaction.builder()
            .debitAccount(debitAccount)
            .creditAccount(creditAccount)
            .amount(request.amount())
            .currency(request.currency())
            .status(TransactionStatus.NEW)
            .comment(request.comment())
            .build();
    }

    public void closeTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }
}
