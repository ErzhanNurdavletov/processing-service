package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Response of all info of payment")
public record PaymentResponse(
    @Schema(description = "payment id")
    UUID id,

    @Schema(description = "account id money transferred from")
    UUID debitAccountId,

    @Schema(description = "account id money transferred to")
    UUID creditAccountId,

    @Schema(description = "card id money transferred from")
    UUID sourceCardId,

    @Schema(description = "card id money transferred to")
    UUID destinationCardId,

    @Schema(description = "transfer amount money")
    BigDecimal amount,

    @Schema(description = "Payment currency - only SOM")
    PaymentCurrency currency,

    @Schema(description = "Current payment status")
    PaymentStatus status,

    @Schema(description = "Date and time of payment creation(in UTC+00:00")
    OffsetDateTime createdAt,

    String comment,

    @Schema(description = "Decline reason if payment status DECLINED")
    PaymentDeclineReason declineReason
) {
}
