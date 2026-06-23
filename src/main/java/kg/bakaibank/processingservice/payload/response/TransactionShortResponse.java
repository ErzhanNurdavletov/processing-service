package kg.bakaibank.processingservice.payload.response;

import kg.bakaibank.processingservice.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionShortResponse(
    UUID transactionId,
    UUID paymentId,
    BigDecimal amount,
    TransactionStatus status
) {
}
