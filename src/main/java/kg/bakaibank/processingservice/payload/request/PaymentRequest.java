package kg.bakaibank.processingservice.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
    @NotNull(message = "sourceCardId can't be null")
    UUID sourceCardId,

    @NotNull(message = "destinationCardId can't be null")
    UUID destinationCardId,

    @NotNull(message = "maxAmount can't be empty or null")
    @Min(value = 1, message = "amount can't be less than 0")
    @Max(value = 1_000_000, message = "amount can't be more than 1 000 000")
    BigDecimal amount,

    @NotNull(message = "currency can be only SOM")
    PaymentCurrency currency,

    @Size(min = 1, max = 255, message = "comment's size must be between 1 and 255(included)")
    String comment
) {
}
