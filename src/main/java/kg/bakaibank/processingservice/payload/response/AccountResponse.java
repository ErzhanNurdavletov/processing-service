package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Response of all info of account")
public record AccountResponse(

    @Schema(description = "Account id")
    UUID accountId,

    @Schema(description = "Client id of current account")
    UUID clientId,

    @Schema(description = "Unique 22 length account number")
    String accountNumber,

    @Schema(description = "current account balance")
    BigDecimal balance,

    @Schema(description = "date and time of account open")
    OffsetDateTime openedAt,

    @Schema(description = "account's actual closed date and time")
    OffsetDateTime closedAt,

    @Schema(description = "account's ended date and time(opened time + 3 years")
    OffsetDateTime endedAt
) {
}
