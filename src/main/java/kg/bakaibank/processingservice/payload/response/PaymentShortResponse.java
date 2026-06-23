package kg.bakaibank.processingservice.payload.response;

import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentShortResponse(
    UUID paymentId,
    BigDecimal amount,
    PaymentStatus status,
    PaymentDeclineReason declineReason
) {
}
