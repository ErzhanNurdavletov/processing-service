package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Response of account balance")
public record AccountBalanceResponse(

    @Schema(description = "Account id")
    UUID accountId,

    @Schema(description = "current balance of account")
    BigDecimal balance
) {
}
