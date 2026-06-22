package kg.bakaibank.processingservice.payload.response;

import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.util.UUID;

public record PaymentShortResponse(
    UUID paymentId,
    PaymentStatus status,
    PaymentDeclineReason declineReason
) {
}
