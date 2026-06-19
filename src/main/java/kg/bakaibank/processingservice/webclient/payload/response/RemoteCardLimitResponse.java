package kg.bakaibank.processingservice.webclient.payload.response;

import java.math.BigDecimal;
import java.util.UUID;

public record RemoteCardLimitResponse(
    UUID limitId,
    String limitName,
    BigDecimal currentAmount,
    Integer currentCount
) {
}
