package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Response of short payment info")
public record PaymentShortResponse(

    @Schema(description = "payment id")
    UUID paymentId,

    @Schema(description = "transfer amount money")
    BigDecimal amount,

    @Schema(description = "Current payment status")
    PaymentStatus status,

    @Schema(description = "Decline reason if payment status DECLINED")
    PaymentDeclineReason declineReason
) {
}
