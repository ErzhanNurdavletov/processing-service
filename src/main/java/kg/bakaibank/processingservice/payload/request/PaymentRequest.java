package kg.bakaibank.processingservice.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to make payment")
public record PaymentRequest(

    @Schema(description = "debit cardId", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "sourceCardId can't be null")
    UUID sourceCardId,

    @Schema(description = "credit cardId", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "destinationCardId can't be null")
    UUID destinationCardId,

    @Schema(description = "amount to transfer", requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "1", maximum = "1000000")
    @NotNull(message = "maxAmount can't be empty or null")
    @Min(value = 1, message = "amount can't be less than 0")
    @Max(value = 1_000_000, message = "amount can't be more than 1 000 000")
    BigDecimal amount,

    @Schema(description = "currency(only SOM)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "currency can be only SOM")
    PaymentCurrency currency,

    @Schema(description = "comment of payment")
    @Size(min = 1, max = 255, message = "comment's size must be between 1 and 255(included)")
    String comment
) {
}
