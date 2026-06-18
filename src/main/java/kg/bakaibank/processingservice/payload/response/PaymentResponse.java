package kg.bakaibank.processingservice.payload.response;

import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.util.UUID;

public record PaymentResponse(
    UUID paymentId,
    PaymentStatus status
) {
}
