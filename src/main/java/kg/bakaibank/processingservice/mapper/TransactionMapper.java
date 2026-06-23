package kg.bakaibank.processingservice.mapper;

import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.payload.response.TransactionShortResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionShortResponse toShortResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionShortResponse(
            transaction.getId(),
            transaction.getPayment().getId(),
            transaction.getAmount(),
            transaction.getStatus());
    }
}
