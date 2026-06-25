package kg.bakaibank.processingservice.payload.response;

import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentResponse(
    UUID id,
    UUID debitAccountId,
    UUID creditAccountId,
    UUID sourceCardId,
    UUID destinationCardId,
    BigDecimal amount,
    PaymentCurrency currency,
    PaymentStatus status,
    OffsetDateTime createdAt,
    String comment,
    PaymentDeclineReason declineReason
) {
}
