package kg.bakaibank.processingservice.payload.request;

import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
    UUID sourceAccountId,
    UUID destinationAccountId,
    BigDecimal amount,
    PaymentCurrency currency,
    String comment
) {
}
