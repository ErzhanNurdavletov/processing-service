package kg.bakaibank.processingservice.kafka.events;

import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OutboxEvent(
    UUID id,
    UUID paymentId,
    PaymentStatus paymentStatus,
    BigDecimal amount,
    PaymentDeclineReason paymentDeclineReason,
    OffsetDateTime createdAt,
    OffsetDateTime publishedAt
) {
}
