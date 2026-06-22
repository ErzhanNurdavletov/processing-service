package kg.bakaibank.processingservice.payload.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceResponse(
        UUID accountId,
        BigDecimal balance
) {
}
