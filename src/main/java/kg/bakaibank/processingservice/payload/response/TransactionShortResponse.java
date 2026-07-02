package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kg.bakaibank.processingservice.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Response of short transaction info")
public record TransactionShortResponse(

    @Schema(description = "transaction id")
    UUID transactionId,

    @Schema(description = "payment id, payment consists of two transactions")
    UUID paymentId,

    @Schema(description = "payment amount money")
    BigDecimal amount,

    @Schema(description = "Current transaction status")
    TransactionStatus status
) {
}
