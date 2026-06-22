package kg.bakaibank.processingservice.service.api;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;

public interface TransactionService {
    Transaction initTransaction(PaymentRequest request,
                                Account debitAccount,
                                Account creditAccount);
    void closeTransaction(Transaction transaction);
}
