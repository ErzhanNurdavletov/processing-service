package kg.bakaibank.processingservice.payload.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse(
    UUID accountId,
    UUID clientId,
    String accountNumber,
    BigDecimal balance,
    OffsetDateTime openedAt,
    OffsetDateTime closedAt,
    OffsetDateTime endedAt
) {
}
